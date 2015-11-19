package cz.tomasdvorak;

import cz.tomasdvorak.auth.AuthenticationHeader;
import cz.tomasdvorak.beans.CommunicationService;
import cz.tomasdvorak.dto.Message;
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
import java.util.List;

@RunWith(Arquillian.class)
public class MultitenancyTest {

    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "cz.tomasdvorak")
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("jbossas-ds.xml")
                .addAsResource("schema/init.sql", "init.sql")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(archive.toString(true));
        return archive;
    }


    @Test
    @RunAsClient
    public void testSimpleStatelessWebServiceEndpoint(@ArquillianResource URL deploymentUrl) throws Exception {
        final CommunicationService port = getCommunicationService(deploymentUrl);
        port.storeMessage(new AuthenticationHeader("Tenant_A", "lorem"), "_secret_message_a_");
        port.storeMessage(new AuthenticationHeader("Tenant_B", "ipsum"), "_secret_message_b_");
        final List<Message> result = port.readMessages(new AuthenticationHeader("Tenant_A", "lorem"));
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("_secret_message_a_", result.get(0).getText());
    }

    private CommunicationService getCommunicationService(final @ArquillianResource URL deploymentUrl) throws MalformedURLException {
        final QName serviceName = new QName("http://beans.tomasdvorak.cz/", "CommunicationServiceImplService");
        final URL wsdlURL = new URL(deploymentUrl, "CommunicationServiceImpl?wsdl");
        final Service service = Service.create(wsdlURL, serviceName);
        return service.getPort(CommunicationService.class);
    }

}
