package net.github.mockrest.server;

import java.util.Map;

import org.eclipse.jetty.client.HttpClient;

import lombok.SneakyThrows;
import net.github.mockrest.util.RequestUtil;

public class MockRestServiceClient {
	
	private String host;
	private int port;
	
	public MockRestServiceClient() {
		this("localhost", 8080);
	}
	
	public MockRestServiceClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@SneakyThrows
	public void expectationBuilder(String endPoint, Map<String, String> requestParams, String response) {
		String request = RequestUtil.toQueryString(requestParams);
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		httpClient.GET("http://" + host + ":" + port + "/expectation?endPoint=" + endPoint + "&request=" + request + "&response=" + response);
		httpClient.stop();
	}
}