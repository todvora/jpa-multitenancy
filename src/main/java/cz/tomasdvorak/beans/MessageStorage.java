package cz.tomasdvorak.beans;

import cz.tomasdvorak.entities.MessageEntry;

import javax.ejb.Local;
import javax.jws.WebService;
import java.util.List;

@Local
public interface MessageStorage {

    void saveMessage(String message);

    List<MessageEntry> getAllMessages();
}
