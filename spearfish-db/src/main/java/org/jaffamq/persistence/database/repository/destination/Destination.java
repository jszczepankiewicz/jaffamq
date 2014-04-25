package org.jaffamq.persistence.database.repository.destination;

import org.jaffamq.persistence.database.repository.Identifiable;
import org.jaffamq.persistence.database.repository.group.Group;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.Set;

/**
 * Represents destination configuration
 */
public class Destination implements Identifiable {

    private Long id;

    private String name;

    private DateTime creationTime;

    private Set<Group> readAuthorizedGroups = Collections.emptySet();

    private Set<Group> writeAuthorizedGroups = Collections.emptySet();

    private Set<Group> adminAuthorizedGroups = Collections.emptySet();

    public Destination(String name){
        this.name = name;
        //this.creationTime = new DateTime(DBConst.DB_TIMEZONE);
    }

    public Destination(Long id, String name, DateTime creationTime) {
        this.id = id;
        this.name = name;
        this.creationTime = creationTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void setReadAuthorizedGroups(Set<Group> readAuthorizedGroups) {
        this.readAuthorizedGroups = readAuthorizedGroups;
    }

    public void setWriteAuthorizedGroups(Set<Group> writeAuthorizedGroups) {
        this.writeAuthorizedGroups = writeAuthorizedGroups;
    }

    public void setAdminAuthorizedGroups(Set<Group> adminAuthorizedGroups) {
        this.adminAuthorizedGroups = adminAuthorizedGroups;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "id=" + id +
                ", name='" + name + '\'' +
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
        if (writeAuthorizedGroups != null ? !writeAuthorizedGroups.equals(that.writeAuthorizedGroups) : that.writeAuthorizedGroups != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        result = 31 * result + (readAuthorizedGroups != null ? readAuthorizedGroups.hashCode() : 0);
        result = 31 * result + (writeAuthorizedGroups != null ? writeAuthorizedGroups.hashCode() : 0);
        result = 31 * result + (adminAuthorizedGroups != null ? adminAuthorizedGroups.hashCode() : 0);
        return result;
    }
}
