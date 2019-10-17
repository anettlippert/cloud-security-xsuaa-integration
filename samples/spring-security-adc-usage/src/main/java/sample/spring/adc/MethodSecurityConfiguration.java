package sample.spring.adc;

import com.sap.cloud.security.adc.ADCSecurityExpressionHandler;
import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

	@Autowired
	private XsuaaServiceConfiguration xsuaaServiceConfiguration;

	@Autowired(required = false)
	private RestTemplate restTemplate;

	@Value("${ADC_URL:#{null}}")
	private String adcUrl;
	
	
	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
	    
	    if (adcUrl == null) {
	        adcUrl = "http://localhost:8181";
	    }
	    
		RestTemplate restTemplate = this.restTemplate != null ? this.restTemplate : new RestTemplate();
		ADCSecurityExpressionHandler expressionHandler =
				new ADCSecurityExpressionHandler(xsuaaServiceConfiguration, restTemplate, adcUrl);
		return expressionHandler;
	}
}
