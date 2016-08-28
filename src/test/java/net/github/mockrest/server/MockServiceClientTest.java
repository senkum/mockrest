package net.github.mockrest.server;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.github.mockrest.server.MockServer;
import net.github.mockrest.server.MockServiceClient;

public class MockRestServiceClientTest {
	
	private MockServer mockRestService = null;

	@Before
	public void setup() throws Exception {
		mockRestService = new MockServer("/");
		mockRestService.start();
	}
	
	@After
	public void tearDown() throws Exception {
		mockRestService.stop();
	}
	
	@Test
	public void test() throws Exception {
		MockServiceClient mockRestServiceClient = new MockServiceClient();
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("key1", "this%20is%20first%20value");
		requestParams.put("key2", "this**is~~second!!value");
		mockRestServiceClient.expectationBuilder("/api/service1", requestParams, "Service1Response1");
		
		requestParams = new HashMap<>();
		requestParams.put("key3", "this%20is%20third%20value");
		requestParams.put("key4", "this**is~~fourth!!value");
		mockRestServiceClient.expectationBuilder("/api/service1", requestParams, "Service1Response2");

		requestParams = new HashMap<>();
		requestParams.put("key1", "this%20is%20first%20value");
		requestParams.put("key2", "this**is~~second!!value");		
		mockRestServiceClient.expectationBuilder("/api/service2", requestParams, "Service2Response1");
		
		requestParams = new HashMap<>();
		requestParams.put("key3", "this%20is%20third%20value");
		requestParams.put("key4", "this**is~~fourth!!value");
		mockRestServiceClient.expectationBuilder("/api/service2", requestParams, "Service2Response2");

		HttpClient client = new HttpClient();
		client.start();
		ContentResponse response = client.GET("http://localhost:8080/api/service1?key1=this%20is%20first%20value&key2=this**is~~second!!value");
		
		assertEquals("Service1Response1", new String(response.getContent()).trim());
		
		response = client.GET("http://localhost:8080/api/service1?key3=this%20is%20third%20value&key4=this**is~~fourth!!value");
		assertEquals("Service1Response2", new String(response.getContent()).trim());

		response = client.GET("http://localhost:8080/api/service2?key1=this%20is%20first%20value&key2=this**is~~second!!value");
		assertEquals("Service2Response1", new String(response.getContent()).trim());
		
		response = client.GET("http://localhost:8080/api/service2?key3=this%20is%20third%20value&key4=this**is~~fourth!!value");
		assertEquals("Service2Response2", new String(response.getContent()).trim());

		client.stop();
	}	
}