package org.jaffamq.persistence.database.dto;

import org.joda.time.DateTimeZone;

import java.util.Set;

/**
 * Represents destination configuration
 */
public class Destination {

    private long id;

    private String name;

    private DateTimeZone creationTime;

    private Set<Group> readAuthorizedGroups;

    private Set<Group> writeAuthorizedGroups;

    private Set<Group> adminAuthorizedGroups;


}
