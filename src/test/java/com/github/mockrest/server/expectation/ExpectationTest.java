package com.github.mockrest.server.expectation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.mockrest.server.expectation.Expectation;
import com.github.mockrest.server.expectation.Expectation.ExpectationBuilder;

public class ExpectationTest {

	@Test
	public void test() {
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("stringKey1", "this first value");
		requestParams.put("stringKey2", "this**second!!value");
		requestParams.put("intKey1", "123");
		Expectation expectation = new Expectation("/abc/service1", requestParams, "service1 response");
		String expectationString = expectation.toJSONString();
		assertEquals("{\"endPoint\":\"/abc/service1\",\"requestParams\":{\"intKey1\":\"123\",\"stringKey1\":\"this first value\",\"stringKey2\":\"this**second!!value\"},\"response\":\"service1 response\"}", expectationString);
		String expectationEncodedString = expectation.toEncodedString();
		assertEquals("endPoint=/abc/service1&request=intKey1%3D123%26stringKey1%3Dthis%20first%20value%26stringKey2%3Dthis**second!!value&response=service1 response", expectationEncodedString);
	}
	
	@Test
	public void testBuilder() {
		ExpectationBuilder builder = ExpectationBuilder.create();
		boolean expectionOccured = false;
		try {
			builder.build();
		} catch(Exception e) {
			expectionOccured = true;
		}
		if (!expectionOccured) {
			fail("Expection expected ");
		}
		builder.expect("endPoint");
		expectionOccured = false;
		try {
			builder.build();
		} catch(Exception e) {
			expectionOccured = true;
		}
		if (!expectionOccured) {
			fail("Expection expected ");
		}
		builder.with("key1", "value1");
		builder.with("key2", "value2");
		builder.thenReply("response");
		Expectation expectation = builder.build();
		assertNotNull(expectation);
		assertEquals("endPoint" , expectation.getEndPoint());
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("key1", "value1");
		requestParams.put("key2", "value2");
		assertEquals(requestParams, expectation.getRequestParams());
		assertEquals("endPoint" , expectation.getEndPoint());
	}
}