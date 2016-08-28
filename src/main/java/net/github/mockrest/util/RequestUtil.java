package net.github.mockrest.util;

import java.net.URLEncoder;
import java.util.Map;

import lombok.SneakyThrows;

public class RequestUtil {
	
	public static String toQueryString(Map<String, String> requestParams) {
		return requestParams.entrySet()
				.stream()
				.map(e -> e.getKey() + encode("=") + e.getValue().replaceAll(" ", "%20"))
			    .reduce((p1, p2) -> p1 + encode("&") + p2)
			    .orElse("");
	}
	
	@SneakyThrows
	private static String encode(String str) {
		return URLEncoder.encode(str, "UTF-8");
	}
}