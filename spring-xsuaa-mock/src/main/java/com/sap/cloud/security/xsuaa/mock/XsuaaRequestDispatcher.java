package com.sap.cloud.security.xsuaa.mock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class XsuaaRequestDispatcher extends Dispatcher {
	protected static final String RESPONSE_404 = "Xsuaa mock authorization server does not support this request";
	protected static final String RESPONSE_401 = "Xsuaa mock authorization server can't authenticate client/user";
	protected static final String RESPONSE_500 = "Xsuaa mock authorization server can't process request";

	@Override
	public MockResponse dispatch(RecordedRequest request) {
		if ("/testdomain/token_keys".equals(request.getPath())) {
			return getResponseFromFile("/mock/testdomain_token_keys.json", HttpStatus.OK);
		}
		if (request.getPath().endsWith("/token_keys")) {
			return getResponseFromFile("/mock/testdomain_token_keys.json", HttpStatus.OK);
		}
		return getResponse(RESPONSE_404, HttpStatus.NOT_FOUND);
	}

	protected MockResponse getResponseFromFile(String path, HttpStatus status) {
		String body;
		try {
			body = IOUtils.toString(XsuaaRequestDispatcher.class.getResourceAsStream(path), StandardCharsets.UTF_8);
			return new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setResponseCode(status.value()).setBody(body);
		} catch (IOException e) {
			return new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).setBody(RESPONSE_500 + ": " + e.getMessage());
		}
	}

	protected MockResponse getResponse(String message, HttpStatus status) {
		return new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setResponseCode(status.value()).setBody(message);
	}
}
