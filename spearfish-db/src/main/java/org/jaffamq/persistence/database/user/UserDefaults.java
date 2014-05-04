package org.jaffamq.persistence.database.user;

/**
 * Constant values used on initial state
 */
public class UserDefaults {

    public static String SUPERADMIN_LOGIN = "admin";
    public static Long SUPERADMIN_ID = 1L;
    public static String SUPERADMIN_PASSWORD_DEFAULT = "xyz321";
    public static String SUPERADMIN_PASSWORD_HASH = "8be94e85158147d335c31e7401565942785e79d7d446cc41f6427422d6755371";
    public static String ADMINS_GROUP = "admins";
    public static Long ADMINS_GROUP_ID = 1L;

    private UserDefaults() {
        //  no instantiation allowed
    }
}