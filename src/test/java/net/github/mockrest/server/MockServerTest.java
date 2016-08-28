package net.github.mockrest.server;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.github.mockrest.server.MockServer;
import net.github.mockrest.server.MockRestServiceClient;

public class MockServerTest {
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Test
	public void testCreate() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("key", "value");

		String args[] = {};
		MockServer service = MockServer.create(args);
		MockRestServiceClient client = new MockRestServiceClient();
		client.expectationBuilder("test", params, "test");				
		service.stop();
		
		args = new String[]{"8082"};
		service = MockServer.create(args);
		client = new MockRestServiceClient("localhost", 8082);
		client.expectationBuilder("test", params, "test");
		service.stop();

		String jsonFileName = tempFolder.getRoot().getAbsolutePath() + "/" + "expectation.json";
		BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFileName));
		bw.write("[]\n");
		bw.close();
		args = new String[]{"8082", jsonFileName};
		service = MockServer.create(args);
		client = new MockRestServiceClient("localhost", 8082);
		client.expectationBuilder("test", params, "test");
		service.stop();
	}

	@Test
	public void testStart() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("key", "value");
		MockRestServiceClient client = new MockRestServiceClient();
		MockServer service = null;
		service = new MockServer("/");
		assertTrue(service.start());
		client.expectationBuilder("test", params, "test");
		boolean startExpectionOccured = false;
		try {			
			service.start();
		} catch(Exception e) {
			startExpectionOccured = true;
			client.expectationBuilder("test", params, "test");				
			service.stop();
		}
		assertTrue(startExpectionOccured);
		boolean clientExpectionOccured = false;
		try {
			client.expectationBuilder("test", params, "test");
		} catch (Exception e) {
			clientExpectionOccured = true;
		}
		assertTrue(clientExpectionOccured);
	}
	
	@Test
	public void testStop() throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("key", "value");
		MockRestServiceClient client = new MockRestServiceClient();

		MockServer service = null;
		boolean stopExpectionOccured = false;
		try {
			service = new MockServer("/");
			boolean clientExpectionOccured = false;
			try {
				client.expectationBuilder("test", params, "test");
			} catch (Exception e) {
				clientExpectionOccured = true;
			}			
			assertTrue(clientExpectionOccured);
			service.stop();
		} catch (Exception e) {
			stopExpectionOccured = true;
		}
		assertTrue(stopExpectionOccured);
		
		service.start();
		client.expectationBuilder("test", params, "test");
		assertTrue(service.stop());
	}
}