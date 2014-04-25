package org.jaffamq.persistence.database;

/**
 * CRUD table names
 */
public enum Table {
    USER,
    GROUP,
    DESTINATION,
    DESTINATION_AND_GROUP_WITH_READ,
    DESTINATION_AND_GROUP_WITH_WRITE,
    DESTINATION_AND_GROUP_WITH_ADMIN,
    USER_AND_GROUP;


    public static String sqlTableNameOf(Table table){

        switch (table){
            case USER:
                return "security_user";
            case GROUP:
                return "security_group";
            case DESTINATION:
                return "destination";
            case DESTINATION_AND_GROUP_WITH_READ:
                return "destination_and_group_with_read";
            case DESTINATION_AND_GROUP_WITH_WRITE:
                return "destination_and_group_with_write";
            case DESTINATION_AND_GROUP_WITH_ADMIN:
                return "destination_and_group_with_admin";
            case USER_AND_GROUP:
                return "security_user_and_group";
            default:
                throw new IllegalStateException("Unrecognized table: " + table);

        }
    }
}
