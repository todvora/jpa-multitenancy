package cz.tomasdvorak.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class LogEntry {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date created;

    @Column(updatable = false, length = 100)
    private String message;

    public LogEntry() {
    }

    public LogEntry(final String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }


    public String getMessage() {
        return message;
    }

    public Date getCreated() {
        return created;
    }

    @PrePersist
    void setCreatedDate() {
        created = new Date();
    }
}
