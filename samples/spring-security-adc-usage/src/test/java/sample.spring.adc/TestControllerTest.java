package sample.spring.adc;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.test.JwtGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.Assert;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestControllerTest {

	private String jwt_viewer;
	private static final String VIEWER = "viewer";

	@Autowired
	private XsuaaServiceConfiguration xsuaaServiceConfiguration;

	@Before
	public void setUp() {
		Map<String, Object> xsSystemAttributesClaim = new HashMap<>();
		Map<String,Object> xsSystemAttributesClaims = new HashMap<>();
		xsSystemAttributesClaims.put("xs.saml.groups", Collections.singletonList("G1"));
		xsSystemAttributesClaims.put("xs.rolecollections", Collections.singletonList("Viewer"));
		xsSystemAttributesClaim.put("xs.system.attributes", xsSystemAttributesClaims);

		jwt_viewer = new JwtGenerator(xsuaaServiceConfiguration.getClientId())
				.setUserName(VIEWER)
				.addScopes(getGlobalScope("Read"))
				.addAttribute("confidentiality_level", new String[]{"PUBLIC"})
				.addCustomClaims(xsSystemAttributesClaim)
				.getTokenForAuthorizationHeader();
	}

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void readWithoutPermission_403() throws Exception {
		mockMvc.perform(get("/v1/method")
				.with(bearerToken(new JwtGenerator(xsuaaServiceConfiguration.getClientId()).getTokenForAuthorizationHeader())))
				.andExpect(status().isForbidden());
	}

	@Test
	public void readWithPermission_200() throws Exception {
		mockMvc.perform(get("/v1/method")
				.with(bearerToken(jwt_viewer)))
				.andExpect(status().isOk());
	}

	private static class BearerTokenRequestPostProcessor implements RequestPostProcessor {
		private String token;

		public BearerTokenRequestPostProcessor(String token) {
			this.token = token;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			request.addHeader(HttpHeaders.AUTHORIZATION, this.token);
			return request;
		}
	}

	private static BearerTokenRequestPostProcessor bearerToken(String token) {
		return new BearerTokenRequestPostProcessor(token);
	}

	private String getGlobalScope(String localScope) {
		Assert.hasText(xsuaaServiceConfiguration.getAppId(), "make sure that xsuaa.xsappname is configured properly.");
		return xsuaaServiceConfiguration.getAppId() + "." + localScope;
	}


}

