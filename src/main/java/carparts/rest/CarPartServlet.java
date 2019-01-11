package carparts.rest;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

import carparts.CloudException;
import carparts.api.CarPartApi;
import carparts.proto.Schema.CarPart;
import carparts.proto.Schema.ProtoEntry;
import carparts.proto.Schema.XHeaders;
import carparts.util.FString;

public class CarPartServlet extends RestServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final CarPartApi api;
	private final MyDefaultServlet defaultServlet;

	CarPartServlet(CarPartApi api, MyDefaultServlet defaultServlet) {
		assert api != null;
		this.api = api;
		this.defaultServlet = defaultServlet;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String[] uri = request.getRequestURI().split("/");
		String endpoint = uri[uri.length - 1];

		try {

			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_OK);

			switch (endpoint) {

			case "getbyid": {
				response.setContentType("application/json");
				response.getWriter().println(protoToString(api.get(getId(request))));
				break;
			}

			case "getall": {
				response.setContentType("application/json");
				response.getWriter().println(protoToString(api.getAll()));
				break;
			}

			case "getxheaders": {
				response.setContentType("application/json");
				response.getWriter().println(protoToString(getXHeaders(request).build()));
				break;
			}

			default:
				defaultServlet.doGet(request, response);

			}

		} catch (

		CloudException e) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.getWriter().println(e.toString());
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String[] uri = request.getRequestURI().split("/");
		String endpoint = uri[uri.length - 1];

		try {
			response.setStatus(HttpServletResponse.SC_OK);

			switch (endpoint) {

			case "createnew": {
				api.put(jsonToCarPartBuilder(getData(request).toString()).build());
				response.setContentType("application/text");
				response.getWriter().println("ok");
				break;
			}

			case "update": {
				api.put(jsonToCarPartBuilder(getData(request).toString()).build());
				response.setContentType("application/text");
				response.getWriter().println("ok");
				break;
			}

			case "decrement": {
				api.incrementOrDecrementInventory(getId(request), -1);
				response.setContentType("application/text");
				response.getWriter().println("ok");
				break;
			}

			case "increment": {
				api.incrementOrDecrementInventory(getId(request), 1);
				response.setContentType("application/text");
				response.getWriter().println("ok");
				break;
			}

			case "reset": {
				api.reset();
				response.setContentType("application/text");
				response.getWriter().println("ok");
				break;
			}

			default:
				throw CloudException.requestMalformed(FString.format("Enpoint {} not mapped", endpoint));

			}

		} catch (CloudException e) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.getWriter().println(e.toString());
		}

	}

	private XHeaders.Builder getXHeaders(HttpServletRequest request) {
		assert request != null;

		XHeaders.Builder xHeadersBuilder = XHeaders.newBuilder();

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = headerNames.nextElement();
			String value = request.getHeader(key);
			xHeadersBuilder.addProtoEntry(ProtoEntry.newBuilder().setKey(key).setValue(value));
		}

		return xHeadersBuilder;
	}

	private String protoToString(MessageOrBuilder m) {
		try {
			return JsonFormat.printer().print(m);
		} catch (InvalidProtocolBufferException e) {
			// e.printStackTrace();
			throw new AssertionError("This is not expected");
		}
	}

	private int getId(HttpServletRequest request) throws CloudException {
		assert request != null;
		return getIntegerParam("partid", request);
	}

	private CarPart.Builder jsonToCarPartBuilder(String json) {

		CarPart.Builder result = CarPart.newBuilder();
		try {
			JsonFormat.parser().ignoringUnknownFields().merge(json, result);
		} catch (InvalidProtocolBufferException e) {
			// e.printStackTrace();
			throw new AssertionError("This is not expected");
		}
		return result;
	}

}
