package cz.tomasdvorak.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TodoEntry {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date created;

    @Column(updatable = false, length = 100)
    private String text;

    private TodoEntry() {
    }

    public TodoEntry(final String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Date getCreated() {
        return created;
    }

    @PrePersist
    protected void setCreatedDate() {
        created = new Date();
    }
}
