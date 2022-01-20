package sitemonitor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    
    @Override
	public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/dashboard").setViewName("index");
        registry.addViewController("/stats").setViewName("index-old");
        registry.addViewController("/alerttest").setViewName("notification");
    }
	
    @Bean
    public WebClient webClient() throws Exception {
		SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		TcpClient tcpClient = TcpClient.create().secure( t -> t.sslContext(sslContext) );
		HttpClient httpClient = HttpClient.from(tcpClient);
		ClientHttpConnector httpConnector = new ReactorClientHttpConnector(httpClient);
		
		WebClient webClient = WebClient.builder()
								.clientConnector(httpConnector)
								.defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
								.build();
		
		return webClient;
    }	
    
}
