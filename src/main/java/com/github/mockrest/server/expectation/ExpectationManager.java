package net.github.mockrest.server.expectation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.github.mockrest.util.RequestUtil;

public class ExpectationManager {

	private Map<String, Map<String, Expectation>> expectationMap;

	public ExpectationManager() {
		expectationMap = new ConcurrentHashMap<>();
	}
	
	public ExpectationManager(String fileName) throws Exception {
		expectationMap = new ConcurrentHashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		Expectation[] expectations = mapper.readValue(new File(fileName), Expectation[].class);
		for (Expectation expectation : expectations) {
			this.addExpectation(expectation);
		}
	}

	public Expectation addExpectation(Expectation expectation) {
		Map<String, Expectation> map = expectationMap.get(expectation.getEndPoint());
		if (map == null) {
			map = new HashMap<>();
			expectationMap.put(expectation.getEndPoint(), map);
		}
		map.put(RequestUtil.toQueryString(expectation.getRequestParams()), expectation);
		return expectation;
	}

	public String getResponse(String target, Map<String, String> requestParams) {
		String queryString = RequestUtil.toQueryString(requestParams);
		Map<String, Expectation> map = expectationMap.get(target);
		if (map == null) {
			return "Expectation not found";
		}
		Expectation expectation = map.get(queryString);
		return expectation != null ? expectation.getResponse() : "Expectation not found";
	}
}