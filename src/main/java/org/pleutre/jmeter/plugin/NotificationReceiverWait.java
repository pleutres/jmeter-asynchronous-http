package org.pleutre.jmeter.plugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class NotificationReceiverWait extends NotificationAbstract {

	private static final Logger LOG = LoggingManager.getLoggerForClass();


	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String functionalIdentifier = context.getParameter(FUNCTIONAL_ID);

		SampleResult result = new SampleResult();
		result.sampleStart();
		try {
			
			LOG.debug("functionalIdentifier=" + functionalIdentifier);
			
			CompletableFuture<String> future = HttpServer.getResults().get(functionalIdentifier);
			String status = future.get(1, TimeUnit.MINUTES);
			
			if (status.equals("VALID")) {
				result.setSuccessful(true);
			}
			else {
				result.setSuccessful(false);
				result.setResponseMessage("invalid status " + status);
			}
				
		} catch (Exception e) {
			LOG.error("Exception on " + functionalIdentifier, e);
			result.setSuccessful(false);
			result.setResponseMessage(e.getMessage());
		} finally {
			result.sampleEnd(); // stop stopwatch
		}
		

		return result;
	}


}
