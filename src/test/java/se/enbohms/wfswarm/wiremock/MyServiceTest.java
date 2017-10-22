package se.enbohms.wfswarm.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * A simple test class using Arquillian, Wildfly Swarm and Wiremock to show how
 * to test a service (MyService) with external dependency by mocking a URL call.
 * 
 * @author enbohm
 *
 */
@RunWith(Arquillian.class)
public class MyServiceTest {

	private static final String HELLO_WORLD = "Hello world!";
	private static final int SERVICE_PORT = 11111;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(SERVICE_PORT);

	@Before
	public void init() {
		givenThat(get(urlEqualTo("/test")).willReturn(
				aResponse().withStatus(200)
						.withHeader("Content-Type", "text/plain")
						.withBody(HELLO_WORLD)));
	}

	@Deployment
	public static JAXRSArchive createDeployment() {
		JAXRSArchive archive = ShrinkWrap.create(JAXRSArchive.class);
		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.importRuntimeAndTestDependencies().resolve()
				.withTransitivity().asFile();
		archive.addAsLibraries(files);
		archive.addPackage(MyService.class.getPackage());
		archive.addAsResource("project-defaults.yml");
		archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		return archive;
	}

	@Inject
	private MyService myService;

	@Test
	public void should_return_valid_response() throws Exception {
		// given
		// when
		String result = myService.callUrl();

		// when
		assertThat(result).isEqualTo(HELLO_WORLD);
	}
}
