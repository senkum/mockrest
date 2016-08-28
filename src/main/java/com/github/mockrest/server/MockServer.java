package com.github.mockrest.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;

import com.github.mockrest.server.expectation.ExpectationManager;

public class MockServer {

	private Server server;

	public MockServer() throws Exception {
		this(8080, new ExpectationManager());
	}

	public MockServer(String fileName) throws Exception {
		this(8080, new ExpectationManager(fileName));
	}

	public MockServer(int port, ExpectationManager expectationManager) throws Exception {
		init(port, expectationManager);
	}
		
	private void init(int port, ExpectationManager expectationManager) throws Exception {
		RestServiceHandler serviceHandler = new RestServiceHandler(expectationManager);
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {serviceHandler});
        server = new Server(port);
        server.setHandler(handlers);
	}

	public boolean start() throws Exception {
		if (server.isStarted()) {
			throw new RuntimeException(new Exception("Server already started"));
		}
		server.start();
		return true;
	}
	
	public boolean stop() throws Exception {
		if (!server.isStarted()) {
			throw new RuntimeException(new Exception("Server not started"));
		}
		server.stop();
		return true;
	}
	
	public static MockServer create(String args[]) throws Exception {
		int port = 8080;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}
		MockServer service = null;
		if (args.length < 2) {
			service = new MockServer(port, new ExpectationManager());
		} else {
			service = new MockServer(port, new ExpectationManager(args[1]));
		}
		service.start();
		return service;
	}
	
	public static void main(String args[]) throws Exception {
        create(args).server.join();
	}
}