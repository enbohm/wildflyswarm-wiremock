package se.enbohms.wfswarm.wiremock;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

/**
 * A simple service which calls an external URL and returns the response as
 * string.
 * 
 * @author enbohm
 *
 */
public class MyService {

	private Client client = ClientBuilder.newClient();

	@Inject
	@ConfigurationValue("backend.url")
	private String url;

	public String callUrl() {
		return client.target(url).path("/test").request().get(String.class);
	}
}
