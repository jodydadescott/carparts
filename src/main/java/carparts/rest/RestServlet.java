package carparts.rest;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import carparts.CloudException;
import carparts.util.FString;

public class RestServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String getStringParam(String key, HttpServletRequest request) throws CloudException {
		assert key != null;
		assert request != null;
		String value = request.getParameter(key);
		if (value == null) {
			throw CloudException.requestMalformed(FString.format("Missing {}", key));
		}
		return value;
	}

	// protected String getStringParamOrNull(String key, HttpServletRequest
	// request) {
	// assert key != null;
	// assert request != null;
	// return request.getParameter(key);
	// }

	protected int getIntegerParam(String key, HttpServletRequest request) throws CloudException {
		assert key != null;
		assert request != null;
		String stringValue = request.getParameter(key);
		if (stringValue == null) {
			throw CloudException.requestMalformed(FString.format("Missing {}", key));
		}

		try {
			return Integer.valueOf(stringValue);
		} catch (NumberFormatException e) {
			throw CloudException.requestMalformed(FString.format("Value {} is not type int", key));
		}
	}

	protected StringBuilder getData(HttpServletRequest request) throws IOException {
		StringBuilder result = new StringBuilder();
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null)
			result.append(line);
		return result;
	}

}
