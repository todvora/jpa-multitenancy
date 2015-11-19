package cz.tomasdvorak.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tenant {

    @Id
    private String name;

    @Column(nullable = false, updatable = false)
    private String schemaName;

    @Column(nullable = false, updatable = true)
    private String password;

    public Tenant() {
    }

    public String getName() {
        return name;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
