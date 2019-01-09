/**
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This file is licensed under the Apache Software License,
 * v. 2 except as noted otherwise in the LICENSE file
 * https://github.com/SAP/cloud-security-xsuaa-integration/blob/master/LICENSE
 */
package com.sap.cloud.security.xsuaa.mock;

import static org.hamcrest.Matchers.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@ActiveProfiles("uaamock")
@SpringBootTest(classes = { XsuaaMockWebServer.class, XsuaaRequestDispatcher.class })
public class XsuaaMockWebServerSpringBootTest {

	RestTemplate restTemplate = new RestTemplate();

	@Value("${xsuaa.url}")
	private String xsuaaMockServerUrl;

	@Test
	public void xsuaaMockStarted() throws URISyntaxException {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(xsuaaMockServerUrl + "/token_keys"), String.class);
		Assert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		Assert.assertThat(response.getBody(), notNullValue());
	}

	@Test
	public void xsuaaMockReturnsTestDomainTokenKeys() throws URISyntaxException {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(xsuaaMockServerUrl + "/testdomain/token_keys"), String.class);
		Assert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		Assert.assertThat(response.getBody(), containsString("keys"));
		Assert.assertThat(response.getBody(), containsString("legacy-token-key-testdomain"));
	}

	@Test(expected = HttpClientErrorException.class)
	public void xsuaaMockReturnsNotFound() throws URISyntaxException {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(xsuaaMockServerUrl + "/anyNotSupportedPath"), String.class);
	}

	@Test
	public void xsuaaMockReturnsCustomResponse() throws URISyntaxException {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(xsuaaMockServerUrl + "/customdomain/token_keys"), String.class);
		Assert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		Assert.assertThat(response.getBody(), equalTo("custom response"));
	}
}
