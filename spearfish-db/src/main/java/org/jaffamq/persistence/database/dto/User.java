package org.jaffamq.persistence.database.dto;

import org.joda.time.DateTimeZone;

import java.util.List;

/**
 * Represents user identity.
 */
public class User {

    private Integer id;

    /**
     * varchar(255)
     */
    private String login;

    /**
     * salted MD5
     */
    private String passwordhash;

    private List<Group> groups;

    private DateTimeZone creationTime;

    public User(Integer id, String login, String passwordhash, DateTimeZone creationTime) {
        this.id = id;
        this.login = login;
        this.passwordhash = passwordhash;
        this.creationTime = creationTime;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public DateTimeZone getCreationTime() {
        return creationTime;
    }
}
