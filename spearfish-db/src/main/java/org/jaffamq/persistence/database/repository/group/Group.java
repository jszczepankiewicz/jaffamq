package org.jaffamq.persistence.database.repository.group;

import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.repository.Identifiable;
import org.joda.time.DateTime;

/**
 * Represent security group attached to users.
 */
public class Group implements Identifiable {

    private final Long id;
    private final String name;
    private final DateTime creationtime;

    public static class Builder {

        //  required
        private final String name;

        //  optional
        private Long id;
        private DateTime creationtime;

        public Builder(String name) {
            this.name = name;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder creationtime(DateTime creationtime) {
            this.creationtime = creationtime;
            return this;
        }

        public Group build() {

            if (creationtime == null) {
                creationtime = CalendarUtils.now();
            }

            return new Group(this);
        }
    }

    private Group(Builder builder) {
        id = builder.id;
        name = builder.name;
        creationtime = builder.creationtime;
    }

    public DateTime getCreationtime() {
        return creationtime;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (id != null ? !id.equals(group.id) : group.id != null) return false;
        if (name != null ? !name.equals(group.name) : group.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
