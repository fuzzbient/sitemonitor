package sitemonitor.service;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import sitemonitor.repository.Event;
import sitemonitor.repository.Site;

@Service
public class SiteChecker {
	private Log logger = LogFactory.getLog(getClass());
	private static final RestTemplate restTemplate;
	static {
	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
	    requestFactory.setReadTimeout(15 * 1000);
	    requestFactory.setConnectTimeout(15 * 1000);
	    restTemplate = new RestTemplate(requestFactory);
	}
	
	@Async
	public Future<Event> handleSiteCheck(Site site) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("SiteChecker.handleSiteCheck() [" + site.getName() + "]");
		}

		long start = System.currentTimeMillis();
		//RestTemplate restTemplate = new RestTemplate();
		ClientHttpResponse response = null;
		String status = null;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("SiteChecker.handleSiteCheck() site:" + site.getName() + ", " + site.getAssertText());
			}
			ClientHttpRequest request = restTemplate.getRequestFactory().createRequest(new URI(site.getUrl()), HttpMethod.GET);
			response = request.execute();
			response.getStatusCode();
			if (response.getStatusCode() != HttpStatus.OK) {
				status = "FAIL " + response.getStatusText();
			} else {
				status = response.getStatusText();
				String body = new String(FileCopyUtils.copyToByteArray(response.getBody()));
				if (StringUtils.isNotEmpty(site.getAssertText())) {
					if (StringUtils.contains(body, site.getAssertText())) {
						status = "OK";
					} else {
						status = "FAIL";
					}
				}
			}
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("SiteChecker.handleSiteCheck() [" + site.getName() + "] Problem", e);
			}
			status = "FAIL " + e.toString();
		} finally {
			if (response != null) {
				try { response.close(); } catch (Exception  ex){}
			}
		}

		site.setResponseTime(System.currentTimeMillis() - start);
		
		Event event = new Event();
		if (!status.equals(site.getStatus())) {
			event.setStatusChange("Y");
		}
		event.setSite(site);
		event.setDescription(site.getName() + " " + status);
		event.setEventTime(new Date());
		event.setState(status);
		event.setResponseTime(site.getResponseTime());

		site.setStatus(status);
		if ("OK".equals(status)) {
			site.setFailures(0);
		} else {
			site.setFailures(site.getFailures() + 1);
		}
		
		return new AsyncResult<Event>(event);
	}
}
