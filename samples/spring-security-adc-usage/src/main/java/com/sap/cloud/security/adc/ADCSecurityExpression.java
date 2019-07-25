package com.sap.cloud.security.adc;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.xs2.security.container.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: extract as library
 */
public class ADCSecurityExpression extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

	/**
	 * appId, e.g. "xsappname!t500"
	 */
	private XsuaaServiceConfiguration xsuaaServiceConfiguration;

	// TODO alternativly use webClient
	private RestTemplate restTemplate;

	private String adcUrl = "";

	private Logger logger = LoggerFactory.getLogger(getClass());

	public ADCSecurityExpression(Authentication authentication) {
		super(authentication);
	}

	//    public String getScopeExpression(String localScope) {
	//        // http://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/expression/OAuth2SecurityExpressionMethods.html
	//        return "#oauth2.hasScope('" + getGlobalScope(localScope) + "')";
	//    }

	public boolean hasScope(String localScope) {
		return hasGlobalScope(getGlobalScope(localScope));
	}

	private boolean hasGlobalScope(String globalScope) {
		boolean hasScope = false;

		Map<String, Object> input = new HashMap<String, Object>();
		input.put("token", SecurityContext.getToken().getAppToken());
		input.put("scope", globalScope);

		hasScope = checkAuthorization(input);
		logger.info("Has user scope '{}' ? {}", globalScope, hasScope);

		return hasScope;
	}

	public boolean hasAttributeValue(String attributeName, String attributeValue) {
		boolean hasAttribute = false;

		Map<String, Object> input = new HashMap<String, Object>();
		input.put("token", SecurityContext.getToken().getAppToken());
		input.put("attributeName", attributeName);
		input.put("attributeValue", attributeValue);

		hasAttribute = checkAuthorization(input);

		logger.info("Has user attribute '{}' = '{}'? {}", attributeName, attributeValue, hasAttribute);
		return hasAttribute;
	}

	private boolean checkAuthorization(Map<String, Object> input) {
		boolean isAuthorized = false;
		String opaUrl = this.adcUrl + "/v1/data/rbac/allow";
		HttpEntity<?> request = new HttpEntity<>(new OPADataRequest(input));
		try {
			OPADataResponse response = restTemplate.postForObject(opaUrl, request, OPADataResponse.class);
			isAuthorized = response.getResult();
		} catch (HttpClientErrorException e) {
			logger.error("Error accessing ADC service.", e);
		}
		return isAuthorized;
	}

	@Override public void setFilterObject(Object o) {
	}

	@Override public Object getFilterObject() {
		return null;
	}

	@Override public void setReturnObject(Object o) {
	}

	@Override public Object getReturnObject() {
		return null;
	}

	@Override public Object getThis() {
		return null;
	}

	public void setXsuaaServiceConfiguration(XsuaaServiceConfiguration xsuaaServiceConfiguration) {
		this.xsuaaServiceConfiguration = xsuaaServiceConfiguration;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setAdcUrl(String adcUrl) {
		this.adcUrl = adcUrl;
	}

	public String getGlobalScope(String localScope) {
		return xsuaaServiceConfiguration.getAppId() + "." + localScope;
	}
}
