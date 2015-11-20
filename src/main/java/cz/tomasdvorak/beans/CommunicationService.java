package cz.tomasdvorak.beans;

import cz.tomasdvorak.dto.AuthenticationHeader;
import cz.tomasdvorak.exceptions.InvalidCredentialsException;
import cz.tomasdvorak.dto.Message;
import cz.tomasdvorak.exceptions.UnknownRecipientException;

import javax.jws.WebService;
import java.util.List;

@WebService
public interface CommunicationService {
    /**
     * Anyone can send a message to tenant, if the tenant name is known and valid
     * @param recipient tenant name to send message to
     * @param message text payload
     * @throws UnknownRecipientException if message sent to unknown tenant
     */
    void sendMessage(String recipient, String message) throws UnknownRecipientException;

    /**
     * Read all messages sent to tenant in {@link AuthenticationHeader#tenantName}
     * @param auth TenantName(=username) & password combination
     * @return List of all available messages for this tenant
     * @throws InvalidCredentialsException if tenant not found or password does not match.
     */
    List<Message> readMessages(AuthenticationHeader auth) throws InvalidCredentialsException;
}
