package net.github.mockrest.server.expectation;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.github.mockrest.server.expectation.Expectation;
import net.github.mockrest.server.expectation.ExpectationManager;

public class ExpectationManagerTest {
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void test() throws Exception {
		ExpectationManager manager = new ExpectationManager();
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("key1", "this is first value");
		requestParams.put("key2", "this**is~~second!!value");
		assertEquals("Expectation not found", manager.getResponse("/api/service1", requestParams));
		
		requestParams = new HashMap<>();
		requestParams.put("key1", "this is first value");
		requestParams.put("key2", "this**is~~second!!value");
		manager.addExpectation(new Expectation("/api/service1", requestParams, "Service1Response1"));		
		assertEquals("Service1Response1", manager.getResponse("/api/service1", requestParams));
		
		requestParams = new HashMap<>();
		requestParams.put("key1", "this is first value");
		requestParams.put("key2", "this**is~~second!!value");
		manager.addExpectation(new Expectation("/api/service2", requestParams, "Service2Response1"));
		assertEquals("Service2Response1", manager.getResponse("/api/service2", requestParams));

		requestParams = new HashMap<>();
		requestParams.put("key1", "this is first value");
		requestParams.put("key5", "this**is~~five!!value");
		assertEquals("Expectation not found", manager.getResponse("/api/service2", requestParams));
	}	

	@Test
	public void testFile() throws Exception {
		String jsonFileName = tempFolder.getRoot().getAbsolutePath() + "/" + "expectation.json";
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFileName));
		bw.write("[\n");
		bw.write("{\"endPoint\":\"/api/service1\",\"requestParams\":{\"key1\":\"this is first value\",\"key2\":\"this**is~~second!!value\"},\"response\":\"Service1Response1\"},\n");
		bw.write("{\"endPoint\":\"/api/service1\",\"requestParams\":{\"key3\":\"this is third value\",\"key4\":\"this**is~~fourth!!value\"},\"response\":\"Service1Response2\"},\n");
		bw.write("{\"endPoint\":\"/api/service2\",\"requestParams\":{\"key1\":\"this is first value\",\"key2\":\"this**is~~second!!value\"},\"response\":\"Service2Response1\"},\n");
		bw.write("{\"endPoint\":\"/api/service2\",\"requestParams\":{\"key3\":\"this is third value\",\"key4\":\"this**is~~fourth!!value\"},\"response\":\"Service2Response2\"}\n");
		bw.write("]\n");
		bw.close();
		ExpectationManager manager = new ExpectationManager(jsonFileName);

		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("key1", "this is first value");
		requestParams.put("key2", "this**is~~second!!value");
		assertEquals("Service1Response1", manager.getResponse("/api/service1", requestParams));
		
		requestParams = new HashMap<>();
		requestParams.put("key3", "this is third value");
		requestParams.put("key4", "this**is~~fourth!!value");		
		assertEquals("Service1Response2", manager.getResponse("/api/service1", requestParams));

		requestParams = new HashMap<>();
		requestParams.put("key1", "this is first value");
		requestParams.put("key2", "this**is~~second!!value");
		assertEquals("Service2Response1", manager.getResponse("/api/service2", requestParams));
		
		requestParams = new HashMap<>();
		requestParams.put("key3", "this is third value");
		requestParams.put("key4", "this**is~~fourth!!value");		
		assertEquals("Service2Response2", manager.getResponse("/api/service2", requestParams));

		assertEquals("Expectation not found", manager.getResponse("/api/service5", requestParams));
	}
}