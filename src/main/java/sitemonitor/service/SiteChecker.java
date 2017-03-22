package sitemonitor.service;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.protocol.HttpContext;
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
	public static final int REQUEST_TIMEOUT_SECONDS = 10;
	private Log logger = LogFactory.getLog(getClass());
	private RestTemplate restTemplate;
	
	@PostConstruct
	public void init() {
		class EasyCookieSpec extends DefaultCookieSpec {
			@Override
			public void validate(org.apache.http.cookie.Cookie cookie, CookieOrigin origin)
					throws MalformedCookieException {
		        //allow all cookies 
			}
		}
		class EasySpecProvider implements CookieSpecProvider {
		    @Override
		    public CookieSpec create(HttpContext context) {
		        return new EasyCookieSpec();
		    }
		}

		Registry<CookieSpecProvider> registryCookieSpec = RegistryBuilder.<CookieSpecProvider>create()
		            .register("easy", new EasySpecProvider())
		            .build();

		RequestConfig requestConfig = RequestConfig.custom()
		            .setCookieSpec("easy")
		            .build();
		
		CloseableHttpClient client = HttpClients.custom()
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.setDefaultCookieStore(new BasicCookieStore())
				.setDefaultCookieSpecRegistry(registryCookieSpec)
				.setDefaultRequestConfig(requestConfig)
				.build();
		
		HttpComponentsClientHttpRequestFactory httpClientFactory = new HttpComponentsClientHttpRequestFactory();
		httpClientFactory.setReadTimeout(REQUEST_TIMEOUT_SECONDS * 1000);
		httpClientFactory.setConnectTimeout(REQUEST_TIMEOUT_SECONDS * 1000);
		httpClientFactory.setHttpClient(client);
		
		this.restTemplate = new RestTemplate(httpClientFactory);
	}
	
	@Async
	public Future<Event> handleSiteCheck(Site site) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("SiteChecker.handleSiteCheck() [" + site.getName() + "]");
		}

		long start = System.currentTimeMillis();
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
