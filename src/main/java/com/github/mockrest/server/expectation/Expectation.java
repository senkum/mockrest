package com.github.mockrest.server.expectation;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mockrest.util.RequestUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expectation {
	
	private String endPoint;
	private Map<String, String> requestParams;
	private String response;

	@SneakyThrows
	public String toJSONString() {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(this);
	}
	
	public String toEncodedString() {
		StringBuffer sb = new StringBuffer();
		sb.append("endPoint=" + endPoint + "&");
		sb.append("request=" + RequestUtil.toQueryString(requestParams) + "&");
		sb.append("response=" + response);
		return sb.toString();
	}
		
	public static class ExpectationBuilder {
	
		private String endPoint;
		private Map<String, String> requestParams;
		private String response;
	
		public static ExpectationBuilder create() {
			return new ExpectationBuilder();
		}

		public ExpectationBuilder expect(String endPoint) {
			this.endPoint = endPoint;
			return this;
		}
		
		public ExpectationBuilder with(String key, String value) {
			if (requestParams == null) {
				this.requestParams = new HashMap<>();
			}
			this.requestParams.put(key, value);
			return this;
		}
		
		public ExpectationBuilder thenReply(String response) {
			this.response = response;
			return this;
		}

		public Expectation build() {
			if (this.endPoint == null) {
				throw new RuntimeException("Endpoint not set");
			}
			if (this.response == null) {
				throw new RuntimeException("Response not set");
			}
			return new Expectation(this.endPoint, this.requestParams, this.response);			
		}
	}
}