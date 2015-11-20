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
    void storeMessage(String recipient, String message) throws UnknownRecipientException;

    List<Message> readMessages(AuthenticationHeader auth) throws InvalidCredentialsException;
}
