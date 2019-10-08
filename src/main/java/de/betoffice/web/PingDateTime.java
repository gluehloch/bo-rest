package de.betoffice.web;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Liefert einen Zeitstempel und signalisiert, dass dieser Controller erreichbar
 * ist. Diese Klasse wird von Jackson serialisiert: Einmal als formatierter
 * Datums-String. Im anderen Fall in Millisekunden (ab 01.01.1970).
 * 
 * @author Andre Winkler
 */
public class PingDateTime {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "Europe/Berlin")
    private Date dateTime;
    private Date dateTimeMillies;

    public Date getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
        this.dateTimeMillies = dateTime;
    }

    public Date getDateTimeMillies() {
        return dateTimeMillies;
    }
    
}
