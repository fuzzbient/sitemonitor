package sitemonitor.cron;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import sitemonitor.service.SiteMonitorService;

@Component
public class SiteMonitor {
	private Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private SiteMonitorService siteMonitorService;

	@Scheduled(cron="0 0/1 * * * ?")
	protected void checkSites() {
		if (logger.isDebugEnabled()) {
			logger.debug("SiteMonitor.checkSites()");
		}
		try {
			siteMonitorService.monitorSites();
		} catch (Exception e) {
			logger.error("SiteMonitor.checkSites() Problem", e);
		}
	}
}
