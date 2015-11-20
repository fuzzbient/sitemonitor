package sitemonitor.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sitemonitor.repository.Event;
import sitemonitor.repository.EventRepository;
import sitemonitor.repository.Site;
import sitemonitor.repository.SiteRepository;

import com.google.common.collect.Lists;

@RestController
@RequestMapping("/service")
public class MonitorController {
	private Log logger = LogFactory.getLog(getClass());
	private EventRepository eventRepository;
	private SiteRepository siteRepository;
	
	@Autowired
	public MonitorController(EventRepository eventRepository, SiteRepository siteRepository) {
		this.eventRepository = eventRepository;
		this.siteRepository = siteRepository;
	}
	
	@RequestMapping(value = "/events", method = RequestMethod.GET)
	public Iterable<Event> findEvents() {
		if (logger.isDebugEnabled()) {
			logger.debug("MonitorController.findEvents()");
		}
		return eventRepository.findAll(new Sort(Sort.Direction.DESC, "eventTime"));
	}

	@RequestMapping(value = "/eventchanges", method = RequestMethod.GET)
	public Iterable<Event> findEventChanges() {
		if (logger.isDebugEnabled()) {
			logger.debug("MonitorController.findEventChanges()");
		}
		return eventRepository.findByStatusChange("Y", new Sort(Sort.Direction.DESC, "eventTime"));
	}

	@RequestMapping(value = "/sites", method = RequestMethod.GET)
	public Iterable<Site> findSites() {
		if (logger.isDebugEnabled()) {
			logger.debug("MonitorController.findSites()");
		}
		return siteRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
	}
	
	@RequestMapping(value = "/eventspurge", method = RequestMethod.DELETE)
	public Map<String,String> eventsPurge() {
		if (logger.isDebugEnabled()) {
			logger.debug("MonitorController.eventsPurge()");
		}
		eventRepository.deleteAll();
		Map<String,String> result = new HashMap<String,String>();
		result.put("message","success");
		return result;
	}
	
	@RequestMapping(value = "/toggleactive/{id}", method = RequestMethod.GET)
	public Map<String,Object> toggleActive(@PathVariable("id") Long id) {
		if (logger.isDebugEnabled()) {
			logger.debug("MonitorController.toggleActive(" + id + ")");
		}
		
		Site site = siteRepository.findOne(id);
		site.toggleActive();
		siteRepository.save(site);
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("success", "true");
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/chartdata/{id}", method = RequestMethod.GET)
	public Map<String,Object> chartData(@PathVariable("id") Long id) {
		if (logger.isDebugEnabled()) {
			logger.debug("MonitorController.chartData(" + id + ")");
		}
		
		Site site = siteRepository.findOne(id);
		Map<String,Object> labels = new HashMap<String,Object>();
		labels.put("label", site.getName()); 
		labels.put("showMarker", Boolean.FALSE); 
		
		DateTime start = new DateTime().minusDays(3);
		List<Event> events = eventRepository.findBySiteAndEventTimeBetween(site, start.toDate(), new Date(), new Sort(Sort.Direction.ASC, "eventTime"));
		String[][][] data = new String[1][events.size()][2];
		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			data[0][i][0] = event.getChartTimeDisplay();
			data[0][i][1] = event.getResponseTime() + "";
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("labels", Lists.newArrayList(labels));
		result.put("data", data);
		
		return result;
	}

	@RequestMapping(value = "/chartalldata", method = RequestMethod.GET)
	public Map<String,Object> chartData() {
		if (logger.isDebugEnabled()) {
			logger.debug("MonitorController.chartData()");
		}
		List<Site> sites = Lists.newArrayList(siteRepository.findAll());
		List<Map<String,Object>> labels = new ArrayList<Map<String,Object>>();
		
		Map<String,List<Event>> eventsMap = new HashMap<String,List<Event>>();
		for (Iterator<Site> iter = sites.iterator(); iter.hasNext();) {
			Site site = iter.next();
			DateTime start = new DateTime().minusHours(2);
			List<Event> events = eventRepository.findBySiteAndEventTimeBetween(site, start.toDate(), new Date(), new Sort(Sort.Direction.ASC, "eventTime"));
			if (events.size() == 0) {
				iter.remove();
			} else {
				eventsMap.put(site.getIdentity(), events);
			}
		}
		
		String[][][] data = new String[sites.size()][][];
		int s = 0;
		for (Site site : sites) {
			Map<String,Object> label = new HashMap<String,Object>();
			label.put("label", site.getName()); 
			label.put("showMarker", Boolean.FALSE); 
			labels.add(label);
			
			List<Event> events = eventsMap.get(site.getIdentity());
			
			data[s] = new String[events.size()][2];
			for (int i = 0; i < events.size(); i++) {
				Event event = events.get(i);
				data[s][i][0] = event.getChartTimeDisplay();
				data[s][i][1] = event.getResponseTime() + "";
			}
			s++;
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("labels", Lists.newArrayList(labels));
		result.put("data", data);

		return result;
	}

}
