package org.pleutre.jmeter.plugin;

import java.util.concurrent.CompletableFuture;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class NotificationReceiverCreation extends NotificationAbstract {

	private static final Logger LOG = LoggingManager.getLoggerForClass();

	private static final HttpServer httpServer = new HttpServer();
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String functionalIdentifier = context.getParameter(FUNCTIONAL_ID);
		LOG.debug("functionalIdentifier=" + functionalIdentifier);

		SampleResult result = new SampleResult();
		
		try {
			CompletableFuture<String> future = new CompletableFuture<String>();
			HttpServer.getResults().put(functionalIdentifier, future);
		} catch (Exception e) {
			LOG.error("Exception on " + functionalIdentifier, e);
		}
		
		result.setSuccessful(true);
		
		return result;
	}


}
