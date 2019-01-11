package carparts.rest;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

import carparts.proto.Schema.ProtoEntry;
import carparts.proto.Schema.XHeaders;

public class XHeadersServlet extends RestServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// String[] uri = request.getRequestURI().split("/");
		// String endpoint = uri[uri.length - 1];

		response.setContentType("application/json");
		response.getWriter().println(protoToString(getXHeaders(request).build()));
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// String[] uri = request.getRequestURI().split("/");
		// String endpoint = uri[uri.length - 1];

		response.setContentType("application/json");
		response.getWriter().println(protoToString(getXHeaders(request).build()));
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

}
