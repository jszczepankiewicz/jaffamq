package org.jaffamq.persistence.database.repository.user;

import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.repository.Identifiable;
import org.jaffamq.persistence.database.repository.group.Group;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents user identity.
 */
public class User implements Identifiable{

    private Long id;

    /**
     * varchar(255)
     */
    private final String login;

    /**
     * salted MD5
     */
    private String passwordhash;

    /**
     * FIXME: change to Set!
     */
    private Set<Group> groups = Collections.EMPTY_SET;

    private final DateTime creationTime;

    /**
     * transient field only for scenarios to update / create user. Not stored to db
     * @param login
     * @param passwordhash
     */
    private String password;

    public User (String login, String password) {
        this.login = login;
        this.password = password;
        creationTime = CalendarUtils.now();
    }

    public User (Long id, String login, String passwordhash, DateTime creationTime) {
        this.id = id;
        this.login = login;
        this.passwordhash = passwordhash;
        this.creationTime = creationTime;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (creationTime != null ? !creationTime.equals(user.creationTime) : user.creationTime != null) return false;
        if (groups != null ? !groups.equals(user.groups) : user.groups != null) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (passwordhash != null ? !passwordhash.equals(user.passwordhash) : user.passwordhash != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (passwordhash != null ? passwordhash.hashCode() : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}
