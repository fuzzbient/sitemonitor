package sitemonitor;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.google.common.cache.CacheBuilder;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
	@Bean
	public CacheManager cacheManager() {
		GuavaCacheManager cacheManager = new GuavaCacheManager();
		cacheManager.setCacheBuilder(CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES));
		return cacheManager;
	}     
}
