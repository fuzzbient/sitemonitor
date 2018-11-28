package sitemonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

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
//		GuavaCacheManager cacheManager = new GuavaCacheManager();
//		cacheManager.setCacheBuilder(CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.MINUTES));
    	CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    	cacheManager.setCacheSpecification("maximumSize=500,expireAfterWrite=60m");
    	return cacheManager;
	}     

}
