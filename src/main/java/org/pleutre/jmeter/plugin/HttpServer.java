package org.pleutre.jmeter.plugin;

import fi.iki.elonen.NanoHTTPD;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer extends NanoHTTPD {

	private static final Logger LOG = LoggingManager.getLoggerForClass();
	
	private String content;
	
	private static final ConcurrentHashMap<String, CompletableFuture<String>> results = new ConcurrentHashMap<String, CompletableFuture<String>>();

	private static final Pattern PATTERN_NOTIF = Pattern.compile(".*FunctionalIdentifier>([+\\d]+)</.*StatusMessage>([\\w_]+)</.*");

	/**
	 * Start a HTTP Server on port 8080
	 */
	public HttpServer() {
		super(8080);

		try {
			start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			content = readReponseFile();
			LOG.info("NanoHTTPD started");
		} catch (URISyntaxException | IOException e) {
			LOG.info("Can not start NanoHTTPD", e);
		}

	}

	/**
	 * Read a sample response file
	 * @return Content of the response file
	 * @throws URISyntaxException if uri is not understood
	 * @throws IOException if file can not be read/created
	 */
	private String readReponseFile() throws URISyntaxException, IOException {
		URI uri = HttpServer.class.getResource("/response.xml").toURI();
		LOG.info("uri = " + uri);
		Map<String, String> env = new HashMap<>(); 
		env.put("create", "true");
		FileSystems.newFileSystem(uri, env);
		Path path = Paths.get(uri);
		LOG.info("path = " + path);
		return new String(Files.readAllBytes(path));
	}

	/**
	 * Method called when HTTP server receive a request
	 *
	 * In my sample, the received request is XML
	 * @param session
	 * @return a HTTP response
	 */
	@Override
	public Response serve(IHTTPSession session) {

		try {
			// In my sample, the received request always contains a content-length.
			// See on internet for other way to close a HTTP response
			String length = session.getHeaders().get("content-length");
			if (length != null) {

				// Read response
				int contentLength = Integer.parseInt(length);
				byte[] buffer = new byte[contentLength];
				session.getInputStream().read(buffer, 0, contentLength);
				String body = new String(buffer);
				LOG.debug("request =" + body);

				// Look for a functional identifier and a status
				Matcher m = PATTERN_NOTIF.matcher(body);
				if (m.matches() && m.groupCount() == 2) {
					String functionalIdentifier = m.group(1);
					String status = m.group(2);

					LOG.debug("functionalIdentifier =" + functionalIdentifier);
					LOG.debug("status =" + status);

					// Notify the JMeter sample that response is received for this identifier
					CompletableFuture<String> waiter = results.get(functionalIdentifier);
					waiter.complete(status);
				}
				else {
					LOG.error("Can not parse = " + body);
				}

			}
		} catch (IOException e) {
			LOG.info("Can not serve response = " + session, e);
		}

		// constant response
		return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/xml", content);

	}

	public static ConcurrentHashMap<String, CompletableFuture<String>> getResults() {
		return results;
	}
	
	
	
}
