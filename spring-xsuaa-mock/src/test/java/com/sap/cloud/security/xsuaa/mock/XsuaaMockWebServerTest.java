/**
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This file is licensed under the Apache Software License,
 * v. 2 except as noted otherwise in the LICENSE file
 * https://github.com/SAP/cloud-security-xsuaa-integration/blob/master/LICENSE
 */
package com.sap.cloud.security.xsuaa.mock;

import static org.hamcrest.Matchers.startsWith;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XsuaaMockWebServerTest {
	private XsuaaMockWebServer xsuaaMockServer;

	@Before
	public void setUp() {
		xsuaaMockServer = new XsuaaMockWebServer();
	}

	@Test
	public void getPropertyShouldStartMockServerAndReturnUrl() {
		String url = (String) xsuaaMockServer.getProperty(XsuaaMockWebServer.MOCK_XSUAA_PROPERTY_SOURCE_NAME);
		Assert.assertThat(url, startsWith("http://127.0.0.1"));
	}
}
