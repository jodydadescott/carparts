package carparts.proto;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import carparts.CloudException;
import carparts.proto.Schema.CarPart;
import carparts.proto.Schema.CarParts;

public class CarPartDB {

	private static final Logger LOG = LoggerFactory.getLogger(CarPartDB.class);

	private Map<Integer, CarPart> map = new HashMap<>();

	public CarPart put(CarPart carPart) throws CloudException {
		assert carPart != null;

		if (carPart.getPartId() <= 0) {
			throw CloudException.entityNotFound("partId is null");
		}

		synchronized (map) {

			CarPart old = map.get(carPart.getPartId());

			if (old == null) {
				map.put(carPart.getPartId(), carPart);
				LOG.info("Created: obj->{}", carPart);
			} else {
				map.put(carPart.getPartId(), carPart);
				LOG.info("Updated: old->{}, new->{}", old, carPart);
			}

			map.put(carPart.getPartId(), carPart);
		}
		return carPart;
	}

	public void delete(int key) {
		synchronized (map) {
			CarPart obj = map.get(key);
			if (obj == null) {
				LOG.info("Not Found: key->{}", key);
			} else {
				map.remove(key);
				LOG.info("Removed: obj->{}", obj);
			}
		}
	}

	public CarPart get(int key) throws CloudException {
		CarPart obj = map.get(key);
		if (obj == null) {
			throw CloudException.entityNotFound("key:" + key);
		}
		return obj;
	}

	public CarParts.Builder getAll() {
		return CarParts.newBuilder().addAllCarPart(map.values());
	}

	public void clear() {
		map.clear();
	}

}
