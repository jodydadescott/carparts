package carparts.api;

import carparts.CloudException;
import carparts.proto.CarPartDB;
import carparts.proto.Schema.CarPart;
import carparts.proto.Schema.CarParts;

public class CarPartApi {

	private final CarPartDB database;

	public CarPartApi(CarPartDB database) {
		this.database = database;
	}

	public CarPart getCarPart(int partId) throws CloudException {
		return database.get(partId);
	}

	public CarPart put(CarPart carPart) throws CloudException {
		assert carPart != null;
		return database.put(carPart);
	}

	public void delete(int partId) {
		database.delete(partId);
	}

	public CarPart get(int partId) throws CloudException {
		return database.get(partId);
	}

	public CarParts.Builder getAll() {
		return database.getAll();
	}

	public CarPart incrementOrDecrementInventory(int partId, int incrementOrDecrement) throws CloudException {
		CarPart.Builder carPartBuilder = database.get(partId).toBuilder();

		int count = carPartBuilder.getInventoryCurrentCount() + incrementOrDecrement;

		if (count < 0) {
			count = 0;
		}

		carPartBuilder.setInventoryCurrentCount(count);

		if (carPartBuilder.getDiscontinued()) {

		} else {
			if (carPartBuilder.getInventoryDesiredCount() > carPartBuilder.getInventoryCurrentCount()) {
				carPartBuilder.setOnOrder(true);
			} else {
				carPartBuilder.setOnOrder(false);
			}
		}

		return database.put(carPartBuilder.build());
	}

	private void loadDev(int vendorId, int partId, String partName, int inventory, int inventoryDesired,
			boolean discontinued) throws CloudException {

		put(CarPart.newBuilder().setVendorId(vendorId).setPartId(partId).setPartName(partName)
				.setPartDescription(partName).setInventoryCurrentCount(inventory)
				.setInventoryDesiredCount(inventoryDesired).setDiscontinued(discontinued).build());

	}

	public void reset() throws CloudException {
		database.clear();
		loadDev(1001, 100453, "BigMuffler", 10, 8, false);
		loadDev(1001, 100224, "SparkPlug", 96, 22, false);
		loadDev(357, 4598443, "Starter", 2, 0, true);
	}

}
