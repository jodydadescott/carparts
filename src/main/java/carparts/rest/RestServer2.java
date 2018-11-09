package carparts.rest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import carparts.api.CarPartApi;

public class RestServer2 {

	private static final Logger LOG = LoggerFactory.getLogger(RestServer2.class);

	private static int DEFAULT_PORT = 8080;

	private volatile Server server;

	private int port = DEFAULT_PORT;

	private final CarPartApi api;

	private Object lock = new Object();

	public RestServer2(CarPartApi api) {
		assert api != null;
		this.api = api;
	}

	public RestServer2 start() {
		synchronized (lock) {
			if (this.server == null) {

				this.server = new Server(port);

				HandlerList handlers = new HandlerList();

				ServletContextHandler context = new ServletContextHandler();
				context.setContextPath("/");

				URL url = RestServer2.class.getResource("/webapp/index.html");
				if (url == null) {
					throw new AssertionError("resources not found");
				}

				try {

					URI baseURI = url.toURI().resolve("./");

					context.setBaseResource(Resource.newResource(baseURI));

				} catch (MalformedURLException e) {
					throw new AssertionError(e);
				} catch (URISyntaxException e) {
					// e.printStackTrace();
					throw new AssertionError(e);
				}

				ServletHandler handler = new ServletHandler();

				ServletHolder servletHolder = new ServletHolder();
				CarPartServlet carPartServlet = new CarPartServlet(api);
				servletHolder.setServlet(carPartServlet);

				// Mapping to a few different URIs

				handler.addServletWithMapping(servletHolder, "/public/*");
				handler.addServletWithMapping(servletHolder, "/private/*");
				handler.addServletWithMapping(servletHolder, "/internal/*");

				ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
				context.addServlet(staticHolder, "/");

				// handlers.addHandler(handler);
				handlers.addHandler(context);
				handlers.addHandler(new DefaultHandler());

				server.setHandler(handlers);

				LOG.info("Running on port {}", port);

				try {
					LOG.trace("enter void start()");

					try {
						this.server.start();

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} finally {
					LOG.trace("exit void start()");
				}

			} else {
				LOG.warn("Already running; ignored");
			}
		}
		return this;
	}

	public RestServer2 shutdown() {
		synchronized (lock) {
			if (this.server == null) {
				LOG.warn("Not running; ignored");
			} else {
				try {
					LOG.debug("Stopping Jetty");
					this.server.stop();
					this.server = null;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return this;
	}

	public RestServer2 setPort(int port) {

		if (port == 0) {
			this.port = DEFAULT_PORT;
		} else if (port < 0) {
			throw new RuntimeException("Port must be 0 or greater");
		} else {
			this.port = port;
		}
		return this;
	}

}
