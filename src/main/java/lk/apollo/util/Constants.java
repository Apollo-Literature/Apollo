package lk.apollo.util;

public class Constants {
    // API Endpoints
    public static final String API_PREFIX = "/api";
    public static final String AUTH_ENDPOINT = API_PREFIX + "/auth";
    public static final String USERS_ENDPOINT = API_PREFIX + "/users";
    public static final String ROLES_ENDPOINT = API_PREFIX + "/roles";
    public static final String PERMISSIONS_ENDPOINT = API_PREFIX + "/permissions";
    public static final String BOOKS_ENDPOINT = "/books";

    // Security
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    // Roles and Permissions
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_PUBLISHER = "PUBLISHER";
    public static final String ROLE_READER = "READER";

    public static final String PERM_READ = "READ";
    public static final String PERM_WRITE = "WRITE";
    public static final String PERM_DELETE = "DELETE";
    public static final String PERM_ADMIN = "ADMIN";
}