package cz.tomasdvorak.beans;

import cz.tomasdvorak.dto.TodoItem;
import cz.tomasdvorak.exceptions.InvalidCredentialsException;

import javax.jws.WebService;
import java.util.List;

@WebService
public interface TodoListService {
    /**
     * Add an entry to currently logged tenant
     * @param content text of the entry
     */
    void insertItem(String content) throws InvalidCredentialsException;

    /**
     * Read all entries of logged-in tenant
     * @return List of all available items for this tenant
     */
    List<TodoItem> readItems() throws InvalidCredentialsException;
}
