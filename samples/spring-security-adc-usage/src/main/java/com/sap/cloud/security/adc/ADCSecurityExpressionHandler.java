package com.sap.cloud.security.adc;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;

public class ADCSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
	private AuthenticationTrustResolver trustResolver =
			new AuthenticationTrustResolverImpl();

	private XsuaaServiceConfiguration xsuaaServiceConfiguration;
	private RestTemplate restTemplate;
	private String opaUrl;

	public ADCSecurityExpressionHandler(XsuaaServiceConfiguration xsuaaServiceConfiguration, RestTemplate restTemplate, String opaUrl) {
		this.restTemplate = restTemplate;
		this.xsuaaServiceConfiguration = xsuaaServiceConfiguration;
		this.opaUrl = opaUrl;
	}

	@Override
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
			Authentication authentication, MethodInvocation invocation) {
		ADCSecurityExpression root =
				new ADCSecurityExpression(authentication);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(this.trustResolver);
		root.setRoleHierarchy(getRoleHierarchy());
		root.setRestTemplate(restTemplate);
		root.setXsuaaServiceConfiguration(xsuaaServiceConfiguration);
		root.setOpaUrl(opaUrl);
		return root;
	}
}
