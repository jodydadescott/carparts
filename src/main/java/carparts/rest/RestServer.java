package carparts.rest;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import carparts.api.CarPartApi;

public class RestServer {

	private static final Logger LOG = LoggerFactory.getLogger(RestServer2.class);

	private static int DEFAULT_PORT = 8080;

	private volatile Server server;

	private int port = DEFAULT_PORT;

	private final CarPartApi api;

	private Object lock = new Object();

	private String webAppPath;

	public RestServer(CarPartApi api) {
		assert api != null;
		this.api = api;
	}

	public RestServer start() {
		synchronized (lock) {
			if (this.server == null) {

				this.server = new Server(port);

				ServletContextHandler context = new ServletContextHandler();
				ServletHolder servletHolder = new ServletHolder();
				HandlerList handlers = new HandlerList();
				handlers.addHandler(context);

				CarPartServlet carPartServlet = new CarPartServlet(api);
				servletHolder.setServlet(carPartServlet);

				// Mapping to a few different URIs

				context.getServletHandler().addServletWithMapping(servletHolder, "/public/*");
				context.getServletHandler().addServletWithMapping(servletHolder, "/private/*");
				context.getServletHandler().addServletWithMapping(servletHolder, "/internal/*");

				ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
				context.setContextPath("/");
				context.addServlet(staticHolder, "/");

				if (webAppPath != null) {

					try {

						File filePath = new File(webAppPath);
						if (!filePath.exists()) {
							throw new RuntimeException("Directory " + webAppPath + " not found");
						}

						context.setBaseResource(new PathResource(filePath.toPath().toRealPath()));

					} catch (IOException e) {
						// e.printStackTrace();
						throw new RuntimeException(e);
					}

					handlers.addHandler(new DefaultHandler());
				}

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

	public RestServer shutdown() {
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

	public RestServer setWebAppPath(String webAppPath) {
		this.webAppPath = webAppPath;
		return this;
	}

	public RestServer setPort(int port) {

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
