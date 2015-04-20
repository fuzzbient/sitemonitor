package sitemonitor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import sitemonitor.repository.Event;
import sitemonitor.repository.EventRepository;
import sitemonitor.repository.Site;
import sitemonitor.repository.SiteRepository;
import sitemonitor.security.provider.XTrustProvider;

@Component
public class SiteMonitorService {
	private Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private SiteChecker siteChecker;
	@Autowired
	private SiteRepository siteRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Value("${sitemonitor.mail.from}")
	private String from;
	
	public SiteMonitorService() {
		if (logger.isDebugEnabled()) {
			logger.debug("SiteMonitorService() Loading XTrustProvider...");
		}		
		XTrustProvider.install();
	}
	
	public void monitorSites() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("SiteMonitorService.monitorSites() Starting...");
		}
		List<Future<Event>> tasks = new ArrayList<Future<Event>>();
		Iterable<Site> sites = siteRepository.findAll();
		for (Site site : sites) {
			if (!"YES".equalsIgnoreCase(site.getActive())) {
				continue;
			}
			tasks.add(siteChecker.handleSiteCheck(site));
		}
		while (!tasksDoneStatus(tasks)) {
			try { Thread.sleep(10); } catch (Exception e) { }
		}
		
		siteRepository.save(sites);
		
		for (Future<Event> task : tasks) {
			Event event = task.get();
			Site site = siteRepository.findOne(event.getSite().getId());
			if (!"OK".equals(site.getStatus()) && 
					site.getFailures() == site.getFailureLimit() &&
				"YES".equalsIgnoreCase(site.getActive())) {
				sendAlerts(event, site.getStatus());
				site.setLastNotification("FAIL");
				siteRepository.save(site);
			} else if ("OK".equals(site.getStatus()) && "Y".equals(event.getStatusChange()) && "FAIL".equals(site.getLastNotification())) {
				sendAlerts(event, "OK");
				site.setLastNotification("OK");
				siteRepository.save(site);
			}
			eventRepository.save(event);
		}
		
		purgeOldEvents();
		
		if (logger.isDebugEnabled()) {
			logger.debug("SiteMonitorService.monitorSites() Done.");
		}
	}
	
	private void purgeOldEvents() {
		if (logger.isDebugEnabled()) {
			logger.debug("SiteMonitorService.purgeOldEvents()");
		}
		int purgeCount = 0;
		for (Event event : eventRepository.findAll()) {
			if (new DateTime(event.getEventTime()).isBefore(new DateTime().minusHours(24))) {
				purgeCount++;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("SiteMonitorService.monitorSites() Events remaining to purge [" + purgeCount + "]...");
		}
		eventRepository.deleteEventsOlderThan(new DateTime().minusHours(24).toDate());
		if (logger.isDebugEnabled()) {
			logger.debug("SiteMonitorService.purgeOldEvents() Completed. Count of Events purged [" + purgeCount + "]");
		}
	}
	
	private void sendAlerts(Event event, String status) {
		if (logger.isDebugEnabled()) {
			logger.debug("SiteMonitorService.sendAlerts() [" + event.getSite().getName() + "] " + status);
		}
		try {
			if (StringUtils.isBlank(event.getSite().getNotify())) {
				return;
			}
			
			String[] to = event.getSite().getNotify().split(",");
			String subject = "**SiteMonitor** [" + event.getSite().getName() + "] " + status;
			
			final Context ctx = new Context();
			ctx.setVariable("timestamp", event.getEventTimeDisplay());
			ctx.setVariable("statusDescription", event.getDescription());
			ctx.setVariable("status", event.getState());
			String body = templateEngine.process("notification", ctx);
			
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
	
			helper.setTo(to);
			helper.setFrom(new InternetAddress(from));
			helper.setReplyTo(from);
			helper.setSubject(subject);
			helper.setText(body, true);
	     
	        mailSender.send(message);
		} catch (Exception e) {
			logger.error("SiteMonitorService.sendAlerts() [" + event.getSite().getName() + "] Problem", e);
		}
	}
	
	private boolean tasksDoneStatus(List<Future<Event>> tasks) {
		for (Future<Event> task : tasks) {
			if (!task.isDone()) {
				return false;
			}
		}
		return true;
	}	
}
