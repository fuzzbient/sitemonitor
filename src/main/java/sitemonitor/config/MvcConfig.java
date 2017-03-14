package sitemonitor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/stats").setViewName("index");
        registry.addViewController("/alerttest").setViewName("notification");
    }
	
//	@Bean
//	public ConditionalCommentsDialect conditionalCommentDialect() {
//	    return new ConditionalCommentsDialect();
//	}	
    
}
