package sitemonitor.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		eventRepository.delete(eventRepository.findAll());
		Map<String,String> result = new HashMap<String,String>();
		result.put("message","success");
		return result;
	}
	
	@RequestMapping(value = "/chartdata/{id}", method = RequestMethod.GET)
	public String[][][] chartData(@PathVariable("id") Long id) {
		if (logger.isDebugEnabled()) {
			logger.debug("MonitorController.chartData(" + id + ")");
		}
		Site site = siteRepository.findOne(id);
		List<Event> events = eventRepository.findBySite(site, new Sort(Sort.Direction.ASC, "eventTime"));
		String[][][] result = new String[1][events.size()][2];
		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			//result[0][i][0] = i + "";
			result[0][i][0] = event.getChartTimeDisplay();
			result[0][i][1] = event.getResponseTime() + "";
		}
		return result;
	}

	@RequestMapping(value = "/chartalldata", method = RequestMethod.GET)
	public String[][][] chartData() {
		if (logger.isDebugEnabled()) {
			logger.debug("MonitorController.chartData()");
		}
		List<Site> sites = Lists.newArrayList(siteRepository.findAll());
		
		String[][][] result = new String[sites.size()][][];
		int s = 0;
		for (Site site : sites) {
			List<Event> events = eventRepository.findBySite(site, new Sort(Sort.Direction.ASC, "eventTime"));
			result[s] = new String[events.size()][2];
			for (int i = 0; i < events.size(); i++) {
				Event event = events.get(i);
				//result[0][i][0] = i + "";
				result[s][i][0] = event.getChartTimeDisplay();
				result[s][i][1] = event.getResponseTime() + "";
			}
			s++;
		}
		
		return result;
	}

}
