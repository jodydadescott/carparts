package carparts;

import carparts.api.CarPartApi;
import carparts.proto.CarPartDB;
import carparts.rest.RestServer;

public class IDERunner {

	private static final String WEBAPP = "src/main/resources/webapp/";

	private final CarPartDB database = new CarPartDB();

	private final CarPartApi api = new CarPartApi(database);

	private RestServer restServer = new RestServer(api);

	public static void main(String[] args) throws CloudException {
		new IDERunner().run();
	}

	private void run() throws CloudException {

		restServer.setWebAppPath(WEBAPP);

		api.reset();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("Shutting Down");
				restServer.shutdown();
			}
		});

		restServer.start();

	}

}
