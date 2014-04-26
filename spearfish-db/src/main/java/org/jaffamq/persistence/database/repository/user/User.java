package org.jaffamq.persistence.database.repository.user;

import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.repository.Identifiable;
import org.jaffamq.persistence.database.repository.group.Group;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.Set;

/**
 * Represents user identity.
 */
public class User implements Identifiable {

    private final Long id;
    private final String login;
    private final String passwordhash;
    private final DateTime creationtime;

    private Set<Group> groups;
    private String password;

    public static class Builder {

        //  required
        private final String login;

        //  optional
        private Long id;
        private String passwordhash;
        private String password;
        private DateTime creationtime;
        private Set<Group> groups;

        public Builder(String login) {
            this.login = login;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder passwordhash(String passwordhash) {
            this.passwordhash = passwordhash;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder creationtime(DateTime creationtime) {
            this.creationtime = creationtime;
            return this;
        }

        public Builder groups(Set<Group> groups) {
            this.groups = groups;
            return this;
        }

        public User build() {

            if (creationtime == null) {
                creationtime = CalendarUtils.now();
            }

            if (groups == null) {
                groups = Collections.EMPTY_SET;
            }

            return new User(this);
        }

    }

    private User(Builder builder) {

        id = builder.id;
        creationtime = builder.creationtime;
        password = builder.password;
        passwordhash = builder.passwordhash;
        groups = builder.groups;
        login = builder.login;
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

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public DateTime getCreationTime() {
        return creationtime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (creationtime != null ? !creationtime.equals(user.creationtime) : user.creationtime != null) return false;
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
        result = 31 * result + (creationtime != null ? creationtime.hashCode() : 0);
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
