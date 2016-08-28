package com.github.mockrest.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.mockrest.util.RequestUtil;

public class RequestUtilTest {

	@Test
	public void test() {
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("key1", "this is first value");
		requestParams.put("key2", "this**is~~second!!value");
		assertEquals("key1%3Dthis%20is%20first%20value%26key2%3Dthis**is~~second!!value", RequestUtil.toQueryString(requestParams));
	}
}