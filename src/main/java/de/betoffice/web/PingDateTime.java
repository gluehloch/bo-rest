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
    private Date dateTimeBerlin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "UTC")
    private Date dateTimeUtc;

    private Date dateTimeMillies;

    private String version;
    private String groupId;
    private String artifactId;

    public PingDateTime(String version, String groupId, String artifactId) {
        this.version = version;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public Date getDateTimeUtc() {
        return dateTimeUtc;
    }

    public Date getDateTimeBerlin() {
        return dateTimeBerlin;
    }

    public void setDateTime(Date dateTime) {
        this.dateTimeUtc = dateTime;
        this.dateTimeBerlin = dateTime;
        this.dateTimeMillies = dateTime;
    }

    public Date getDateTimeMillies() {
        return dateTimeMillies;
    }

    public String getVersion() {
        return version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

}
