package cz.tomasdvorak;

import cz.tomasdvorak.beans.TodoListService;
import cz.tomasdvorak.dto.TodoItem;
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
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(Arquillian.class)
public class MultitenancyTest {

    private static final Logger logger = Logger.getLogger(MultitenancyTest.class);

    /**
    * url of running application servlet, injected by arquillian
    */
    @ArquillianResource
    private URL deploymentUrl;

    /**
     * Create arquillian deployment and start managed jboss server.
     */
    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "cz.tomasdvorak")
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("jbossas-ds.xml")
                .addAsResource("schema/main_db.sql", "main_db.sql")
                .addAsResource("schema/alice_db.sql", "alice_db.sql")
                .addAsResource("schema/bob_db.sql", "bob_db.sql")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        logger.debug(archive.toString(true));
        return archive;
    }


    @Test
    @RunAsClient
    public void testWebserviceCommunication() throws Exception {
        final TodoListService alicePort = getServicePort("Alice", "lorem");
        final TodoListService bobPort = getServicePort("Bob", "ipsum");

        alicePort.insertItem("Learn react.js");

        bobPort.insertItem("Buy food");
        bobPort.insertItem("Finish the JPA article");

        verify(
            alicePort.readItems(),
            "Learn react.js"
        );

        verify(
            bobPort.readItems(),
            "Buy food",
            "Finish the JPA article"
        );
    }

    @RunAsClient
    @Test(expected = InvalidCredentialsException.class)
    public void testInvalidLogin() throws Exception {
        final TodoListService alicePort = getServicePort("Alice", "wrong-pass");
        alicePort.insertItem("Learn react.js");
    }

    /**
     * Verify, that read messages match expected
     * @param readTodoItems messages supplied by the webservice
     * @param expectedMessages expected set of messages
     */
    private void verify(final List<TodoItem> readTodoItems, final String... expectedMessages) {
        Assert.assertNotNull(readTodoItems);
        final List<String> actualMessages = readTodoItems.stream().map(TodoItem::getText).collect(Collectors.toList());
        final List<String> expected = Arrays.asList(expectedMessages);
        Assert.assertEquals(expected, actualMessages);
    }

    /**
     * Connect to the SOAP CommunicationWebservice

     * @return port of the webservice
     * @throws MalformedURLException
     */
    private TodoListService getServicePort(final String username, final String password) throws MalformedURLException {
        final QName serviceName = new QName("http://beans.tomasdvorak.cz/", "TodoListServiceImplService");
        final URL wsdlURL = new URL(deploymentUrl, "TodoListServiceImpl?wsdl");
        final Service service = Service.create(wsdlURL, serviceName);
        final TodoListService port = service.getPort(TodoListService.class);

        final Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, username);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
        return port;
    }

}
