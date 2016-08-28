package com.github.mockrest.server;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.IMocksControl;
import org.eclipse.jetty.server.Request;
import org.junit.Test;

import com.github.mockrest.server.RestServiceHandler;
import com.github.mockrest.server.expectation.Expectation;
import com.github.mockrest.server.expectation.ExpectationManager;

public class RestServiceHandlerTest {

	@Test
	public void testHandleGetResponse() throws Exception {

		Map<String, String> params = new HashMap<>();
		params.put("key1", "this is first value");
		params.put("key2", "this**is~~second!!value");		
		
		List<Expectation> expectationList = new ArrayList<>();
		expectationList.add(new Expectation("/api/service1", params, "Success Response"));
		ExpectationManager expectationManager = new ExpectationManager() {			
			@Override
			public String getResponse(String target, Map<String, String> requestParams) {
				return expectationList.get(0).getResponse();
			}
		};

		IMocksControl control = createControl();
		HttpServletRequest servletRequest = control.createMock(HttpServletRequest.class);
		Map<String, String[]> requestParams = new HashMap<>();
		requestParams.put("key1", new String[]{"this is first value"});
		requestParams.put("key2", new String[]{"this**is~~second!!value"});		
		expect(servletRequest.getParameterMap()).andReturn(requestParams).once();
		
		HttpServletResponse servletResponse = control.createMock(HttpServletResponse.class);		
		StringWriter sw = new StringWriter();
		servletResponse.setContentType("text/json; charset=utf-8");
		expectLastCall().once();
		servletResponse.setStatus(HttpServletResponse.SC_OK);
		expectLastCall().once();
		
		expect(servletResponse.getWriter()).andReturn(new PrintWriter(sw)).once();

		Request baseRequest = control.createMock(Request.class);
		baseRequest.setHandled(true);
		expectLastCall().once();

		control.replay();
		
		RestServiceHandler restServiceHandler = new RestServiceHandler(expectationManager);
		restServiceHandler.handle("/api/service1", baseRequest, servletRequest, servletResponse);

		String response = sw.toString();
		assertEquals("Success Response", response.trim());
		
		control.verify();
	}

	
	@Test
	public void testHandleBuildExpectation() throws Exception {
		IMocksControl control = createControl();
		HttpServletRequest servletRequest = control.createMock(HttpServletRequest.class);
		expect(servletRequest.getParameter("endPoint")).andReturn("/api/service1").once();
		expect(servletRequest.getParameter("request")).andReturn("key1=value1&key2=value2").once();
		expect(servletRequest.getParameter("response")).andReturn("Sucess response").once();		
		
		HttpServletResponse servletResponse = control.createMock(HttpServletResponse.class);		
		StringWriter sw = new StringWriter();
		servletResponse.setContentType("text/json; charset=utf-8");
		expectLastCall().once();
		servletResponse.setStatus(HttpServletResponse.SC_OK);
		expectLastCall().once();
		
		expect(servletResponse.getWriter()).andReturn(new PrintWriter(sw)).once();

		Request baseRequest = control.createMock(Request.class);
		baseRequest.setHandled(true);
		expectLastCall().once();

		List<Expectation> expectationList = new ArrayList<>();
		ExpectationManager expectationManager = new ExpectationManager() {			
			public Expectation addExpectation(Expectation expectation) {
				expectationList.add(expectation);
				return expectation;
			}
		};

		control.replay();
		
		RestServiceHandler restServiceHandler = new RestServiceHandler(expectationManager);
		restServiceHandler.handle("/expectation", baseRequest, servletRequest, servletResponse);

		String response = sw.toString();
		assertEquals("Expectation set \n{\"endPoint\":\"/api/service1\",\"requestParams\":{\"key1\":\"value1\",\"key2\":\"value2\"},\"response\":\"Sucess response\"}\nendPoint=/api/service1&request=key1%3Dvalue1%26key2%3Dvalue2&response=Sucess response", response.trim());
		
		assertEquals(1, expectationList.size());
		Expectation expectation = expectationList.get(0);
		assertEquals("/api/service1", expectation.getEndPoint());		
		assertEquals("value1", expectation.getRequestParams().get("key1"));
		assertEquals("value2", expectation.getRequestParams().get("key2"));
		assertEquals("Sucess response", expectation.getResponse());
		
		control.verify();
	}
	
	@Test
	public void testBuildExpectation() {
		IMocksControl control = createControl();
		HttpServletRequest servletRequest = control.createMock(HttpServletRequest.class);
		expect(servletRequest.getParameter("endPoint")).andReturn("/api/service1").once();
		expect(servletRequest.getParameter("request")).andReturn("key1=value1&key2=value2").once();
		expect(servletRequest.getParameter("response")).andReturn("Sucess response").once();
		control.replay();
		
		List<Expectation> expectationList = new ArrayList<>();
		ExpectationManager expectationManager = new ExpectationManager() {			
			public Expectation addExpectation(Expectation expectation) {
				expectationList.add(expectation);
				return expectation;
			}
		};		
		RestServiceHandler restServiceHandler = new RestServiceHandler(expectationManager);
		String response = restServiceHandler.buildExpectation(servletRequest);
		assertEquals("Expectation set \n{\"endPoint\":\"/api/service1\",\"requestParams\":{\"key1\":\"value1\",\"key2\":\"value2\"},\"response\":\"Sucess response\"}\nendPoint=/api/service1&request=key1%3Dvalue1%26key2%3Dvalue2&response=Sucess response", response.trim());
		
		assertEquals(1, expectationList.size());
		Expectation expectation = expectationList.get(0);
		assertEquals("/api/service1", expectation.getEndPoint());		
		assertEquals("value1", expectation.getRequestParams().get("key1"));
		assertEquals("value2", expectation.getRequestParams().get("key2"));
		assertEquals("Sucess response", expectation.getResponse());
		
		control.verify();
	}
}