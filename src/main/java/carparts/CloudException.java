package carparts;

import java.util.HashMap;
import java.util.Map;

import carparts.util.FString;

public class CloudException extends Exception {

	private static final long serialVersionUID = 1L;

	public final static int REQUEST_MALFORMED = 100;
	public final static int ENTITY_NOT_FOUND = 101;
	public final static int ENTITY_MALFORMED = 102;
	public final static int ENTITY_TTL_TO_BIG = 103;
	public final static int ENTITY_TTL_TO_SMALL = 104;

	private final static Map<Integer, String> intToStringMap = new HashMap<>();

	static {
		intToStringMap.put(REQUEST_MALFORMED, "request is malformed");
		intToStringMap.put(ENTITY_NOT_FOUND, "entity not found");
		intToStringMap.put(ENTITY_MALFORMED, "entity is malformed");
		intToStringMap.put(ENTITY_TTL_TO_BIG, "TTL to small");
		intToStringMap.put(ENTITY_TTL_TO_SMALL, "TTL to big");
	}

	public static CloudException user(int usercode, String msg, Object... objects) {
		return new CloudException(usercode, msg, objects);
	}

	public static CloudException entityNotFound(String msg, Object... objects) {
		return new CloudException(ENTITY_NOT_FOUND, msg, objects);
	}

	public static CloudException requestMalformed(String msg, Object... objects) {
		return new CloudException(REQUEST_MALFORMED, msg, objects);
	}

	public static CloudException entityMalformed(String msg, Object... objects) {
		return new CloudException(ENTITY_MALFORMED, msg, objects);
	}

	public static CloudException entityTtlToBig(String msg, Object... objects) {
		return new CloudException(ENTITY_TTL_TO_BIG, msg, objects);
	}

	public static CloudException entityTtlToSmall(String msg, Object... objects) {
		return new CloudException(ENTITY_TTL_TO_SMALL, msg, objects);
	}

	private final String message;
	private final int code;

	private CloudException(int code, String message, Object... objects) {
		super(FString.format("Code->{}, Message->{}", code,
				FString.format(intToStringMap.get(code) + "::" + message, objects)));
		this.message = message;
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "{\"message\":\"" + message + "\",\"code\":" + code + "}";
	}

	// ============================================================================================

}
