package cz.tomasdvorak.dto;

import java.util.Date;

public class Message {
    
    private String text;
    private Date date;


    public Message() {
    }

    public Message(final String text, final Date date) {
        this.text = text;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }
}
