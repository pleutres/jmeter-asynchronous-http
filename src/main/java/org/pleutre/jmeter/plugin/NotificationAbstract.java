package org.pleutre.jmeter.plugin;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.util.concurrent.CompletableFuture;

public class NotificationAbstract extends AbstractJavaSamplerClient {

	private static final Logger LOG = LoggingManager.getLoggerForClass();

	protected static final String FUNCTIONAL_ID = "FUNCTIONAL_ID";
	
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

	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument(FUNCTIONAL_ID, "");
		return defaultParameters;
	}


}
