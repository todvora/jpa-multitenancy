package cz.tomasdvorak;

import cz.tomasdvorak.dto.AuthenticationHeader;
import cz.tomasdvorak.beans.CommunicationService;
import cz.tomasdvorak.dto.Message;
import cz.tomasdvorak.exceptions.InvalidCredentialsException;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(Arquillian.class)
public class MultitenancyTest {

    private static final Logger logger = Logger.getLogger(MultitenancyTest.class);


    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "cz.tomasdvorak")
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("jbossas-ds.xml")
                .addAsResource("schema/init.sql", "init.sql")
                .addAsResource("log4j.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        logger.debug(archive.toString(true));
        return archive;
    }


    @Test
    @RunAsClient
    public void testSimpleStatelessWebServiceEndpoint(@ArquillianResource final URL deploymentUrl) throws Exception {
        final CommunicationService port = getCommunicationService(deploymentUrl);

        port.sendMessage("Tenant_A", "secret message a");

        port.sendMessage("Tenant_B", "secret message b");
        port.sendMessage("Tenant_B", "another message b");

        verify(
            port.readMessages(new AuthenticationHeader("Tenant_A", "lorem")),
            "secret message a"
        );

        verify(
                port.readMessages(new AuthenticationHeader("Tenant_B", "ipsum")),
                "secret message b",
                "another message b"
        );
    }

    private void verify(final List<Message> result, final String... expectedMessages) throws InvalidCredentialsException {
        Assert.assertNotNull(result);
        final List<String> actualMessages = result.stream().map(Message::getText).collect(Collectors.toList());
        final List<String> expected = Arrays.asList(expectedMessages);
        Assert.assertEquals(expected, actualMessages);
    }

    private CommunicationService getCommunicationService(final @ArquillianResource URL deploymentUrl) throws MalformedURLException {
        final QName serviceName = new QName("http://beans.tomasdvorak.cz/", "CommunicationServiceImplService");
        final URL wsdlURL = new URL(deploymentUrl, "CommunicationServiceImpl?wsdl");
        final Service service = Service.create(wsdlURL, serviceName);
        return service.getPort(CommunicationService.class);
    }

}
