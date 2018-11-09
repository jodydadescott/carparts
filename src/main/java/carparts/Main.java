package carparts;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import carparts.api.CarPartApi;
import carparts.proto.CarPartDB;
import carparts.rest.RestServer;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static final String ENV_PORT = "PORT";

	public static final String ENV_PREPOPULATE = "PREPOPULATE";

	public static final String ENV_WEBAPPPATH = "WEBAPPPATH";

	private final CarPartDB database = new CarPartDB();

	private final CarPartApi api = new CarPartApi(database);

	private RestServer restServer = new RestServer(api);

	public static void main(String[] args) throws CloudException {
		new Main().run();
	}

	private void run() throws CloudException {

		Map<String, String> env = System.getenv();

		if (env.containsKey(ENV_PORT)) {
			restServer.setPort(Integer.valueOf(env.get(ENV_PORT)));
		}

		if (env.containsKey(ENV_PREPOPULATE)) {
			if (Boolean.valueOf(env.get(ENV_PREPOPULATE).toLowerCase())) {
				api.reset();
			}
		}

		String webAppPath = null;

		if (env.containsKey(ENV_WEBAPPPATH)) {
			webAppPath = env.get(ENV_WEBAPPPATH);
		} else {
			webAppPath = getWebAppPath();
		}

		if (webAppPath == null) {
			throw new RuntimeException("Unable to determine webapp directory location");
		}

		restServer.setWebAppPath(webAppPath);

		api.reset();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				LOG.info("Shutting Down");
				restServer.shutdown();
			}
		});

		restServer.start();

	}

	private String getWebAppPath() {

		String path = null;

		URL url = Main.class.getResource("/webapp/");

		if (url != null) {
			path = url.getPath();
			if (new File(path).exists()) {
				return path;
			}
		}

		path = "src/main/resources/webapp/";
		if (new File(path).exists()) {
			return path;
		}

		path = "webapp";
		if (new File(path).exists()) {
			return path;
		}

		return null;
	}

}
