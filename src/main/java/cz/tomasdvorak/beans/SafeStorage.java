package cz.tomasdvorak.beans;

import cz.tomasdvorak.entities.LogEntry;

import javax.ejb.Local;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface SafeStorage {

    void saveMessage(String message);

    List<LogEntry> getAllMessages();
}
