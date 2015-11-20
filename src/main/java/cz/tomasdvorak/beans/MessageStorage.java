package cz.tomasdvorak.beans;

import cz.tomasdvorak.entities.MessageEntry;

import javax.ejb.Local;
import java.util.List;

@Local
public interface MessageStorage {

    /**
     * Save message in a persistent storage
     * @param message text payload
     */
    void saveMessage(String message);

    /**
     * Retrieve all messages available in a persistent storage
     */
    List<MessageEntry> getAllMessages();
}
