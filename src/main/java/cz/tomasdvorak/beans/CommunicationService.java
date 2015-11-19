package cz.tomasdvorak.beans;

import cz.tomasdvorak.auth.AuthenticationHeader;
import cz.tomasdvorak.auth.InvalidCredentialsException;
import cz.tomasdvorak.dto.Message;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CommunicationService {
    void storeMessage(AuthenticationHeader auth, String message) throws InvalidCredentialsException;

    List<Message> readMessages(AuthenticationHeader auth) throws InvalidCredentialsException;
}
