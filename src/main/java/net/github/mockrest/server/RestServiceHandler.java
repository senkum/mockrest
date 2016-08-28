package net.github.mockrest.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import net.github.mockrest.server.expectation.Expectation;
import net.github.mockrest.server.expectation.ExpectationManager;

public class RestServiceHandler extends AbstractHandler {

	private ExpectationManager expectationManager;
	
	public RestServiceHandler(ExpectationManager expectationManager) {
		this.expectationManager = expectationManager;
	}
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
			throws IOException, ServletException {
		servletResponse.setContentType("text/json; charset=utf-8");
		servletResponse.setStatus(HttpServletResponse.SC_OK);
		String responseString = null;
		if (target.equals("/expectation")) {
			responseString = buildExpectation(servletRequest);
		} else {
			Map<String, String> requestParams = new HashMap<>();
			for (Entry<String, String[]> entry : servletRequest.getParameterMap().entrySet()) {
				requestParams.put(entry.getKey(), entry.getValue()[0]);
			}
			responseString = expectationManager.getResponse(target, requestParams);
		}
		servletResponse.getWriter().println(responseString);
		baseRequest.setHandled(true);
	}

	protected String buildExpectation(HttpServletRequest servletRequest) {
		String endPoint = servletRequest.getParameter("endPoint");
		String request = servletRequest.getParameter("request");
		request = request.replaceAll("\r\n", "&");
		Map<String, String> requestMap = new HashMap<>();
		for (String line : request.split("&")) {
			String tokens[] = line.split("=");
			requestMap.put(tokens[0], tokens[1]);
		}
		String response = servletRequest.getParameter("response");
		Expectation expectation = new Expectation(endPoint, requestMap, response);
		expectationManager.addExpectation(expectation);
		String jsonString = expectation.toJSONString();
		String encodedString = expectation.toEncodedString();
		return "Expectation set \n" +  jsonString + "\n" + encodedString;
	}
}