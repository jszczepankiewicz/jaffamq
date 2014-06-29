package org.jaffamq.persistence.database.destination;

import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.Identifiable;
import org.jaffamq.persistence.database.group.Group;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.Set;

/**
 * Represents destination configuration
 */
public class Destination implements Identifiable {

    private final Long id;
    private final String name;
    private final Type type;
    private final DateTime creationTime;
    private Set<Group> readAuthorizedGroups;
    private Set<Group> writeAuthorizedGroups;
    private Set<Group> adminAuthorizedGroups;

    public static class Builder {

        //  required
        private final String name;
        private final Type type;

        //  optional
        private Long id;
        private DateTime creationtime;
        private Set<Group> readAuthorizedGroups = Collections.EMPTY_SET;
        private Set<Group> writeAuthorizedGroups = Collections.EMPTY_SET;
        private Set<Group> adminAuthorizedGroups = Collections.EMPTY_SET;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder creationtime(DateTime creationtime) {
            this.creationtime = creationtime;
            return this;
        }

        public Builder groupsAuthorizedToRead(Set<Group> groups) {
            this.readAuthorizedGroups = groups;
            return this;
        }

        public Builder groupsAuthorizedToWrite(Set<Group> groups) {
            this.writeAuthorizedGroups = groups;
            return this;
        }

        public Builder groupsAuthorizedToAdmin(Set<Group> groups) {
            this.adminAuthorizedGroups = groups;
            return this;
        }
        public Builder(String name, String type){
            this.name = name;

            if(type.length()!=1){
                throw new IllegalArgumentException("Type of destination should be of type character[1]");
            }

            this.type = Type.ofValue(type.charAt(0));
        }

        public Builder(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public Destination build() {

            if (creationtime == null) {
                creationtime = CalendarUtils.now();
            }

            return new Destination(this);
        }

    }

    private Destination(Builder builder) {

        id = builder.id;
        name = builder.name;
        type = builder.type;
        creationTime = builder.creationtime;
        readAuthorizedGroups = builder.readAuthorizedGroups;
        writeAuthorizedGroups = builder.writeAuthorizedGroups;
        adminAuthorizedGroups = builder.adminAuthorizedGroups;

    }

    public void setReadAuthorizedGroups(Set<Group> readAuthorizedGroups) {
        this.readAuthorizedGroups = readAuthorizedGroups;
    }

    public void setWriteAuthorizedGroups(Set<Group> writeAuthorizedGroups) {
        this.writeAuthorizedGroups = writeAuthorizedGroups;
    }

    public void setAdminAuthorizedGroups(Set<Group> adminAuthorizedGroups) {
        this.adminAuthorizedGroups = adminAuthorizedGroups;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public Set<Group> getReadAuthorizedGroups() {
        return readAuthorizedGroups;
    }

    public Set<Group> getWriteAuthorizedGroups() {
        return writeAuthorizedGroups;
    }

    public Set<Group> getAdminAuthorizedGroups() {
        return adminAuthorizedGroups;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Destination that = (Destination) o;

        if (adminAuthorizedGroups != null ? !adminAuthorizedGroups.equals(that.adminAuthorizedGroups) : that.adminAuthorizedGroups != null)
            return false;
        if (creationTime != null ? !creationTime.equals(that.creationTime) : that.creationTime != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (readAuthorizedGroups != null ? !readAuthorizedGroups.equals(that.readAuthorizedGroups) : that.readAuthorizedGroups != null)
            return false;
        if (type != that.type) return false;
        if (writeAuthorizedGroups != null ? !writeAuthorizedGroups.equals(that.writeAuthorizedGroups) : that.writeAuthorizedGroups != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        result = 31 * result + (readAuthorizedGroups != null ? readAuthorizedGroups.hashCode() : 0);
        result = 31 * result + (writeAuthorizedGroups != null ? writeAuthorizedGroups.hashCode() : 0);
        result = 31 * result + (adminAuthorizedGroups != null ? adminAuthorizedGroups.hashCode() : 0);
        return result;
    }

    public enum Type {

        QUEUE('Q'),
        TOPIC('T');

        private final char value;

        Type(char value) {
            this.value = value;
        }

        public static Type ofValue(char value) {

            switch (value) {
                case 'Q':
                    return QUEUE;
                case 'T':
                    return TOPIC;
                default:
                    throw new IllegalArgumentException("Unsupported type of destination for value: " + value);
            }
        }

        public char toValue() {
            return value;
        }
    }
}
