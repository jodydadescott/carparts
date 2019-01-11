package carparts.rest;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import carparts.api.CarPartApi;

public class RestServer {

	private static final Logger LOG = LoggerFactory.getLogger(RestServer.class);

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

				try {

					if (webAppPath == null) {
						throw new RuntimeException("webAppPath is not configured");

					}

					File filePath = new File(webAppPath);
					if (!filePath.exists()) {
						throw new RuntimeException("Directory " + webAppPath + " not found");
					}

					this.server = new Server(port);

					ServletContextHandler context = new ServletContextHandler();
					context.setContextPath("/");

					MyDefaultServlet myDefaultServlet = new MyDefaultServlet();
					ServletHolder staticHolder = new ServletHolder("default", myDefaultServlet);
					staticHolder.setInitParameter("resourceBase", webAppPath);
					context.addServlet(staticHolder, "/");

					HandlerList handlers = new HandlerList();
					handlers.addHandler(context);

					ServletHolder carPartServletHolder = new ServletHolder();
					CarPartServlet carPartServlet = new CarPartServlet(api, myDefaultServlet);
					carPartServletHolder.setServlet(carPartServlet);
					context.addServlet(carPartServletHolder, "/carparts/*");

					ServletHolder xHeadersServletHolder = new ServletHolder();
					XHeadersServlet xHeadersServlet = new XHeadersServlet();
					xHeadersServletHolder.setServlet(xHeadersServlet);
					context.addServlet(xHeadersServletHolder, "/xheaders/*");

					handlers.addHandler(new DefaultHandler());

					server.setHandler(handlers);

					LOG.info("Running on port {}", port);

					LOG.trace("enter void start()");

					this.server.start();

				} catch (Exception e) {
					throw new RuntimeException(e);

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