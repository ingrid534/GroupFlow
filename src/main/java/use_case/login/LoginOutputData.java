package use_case.login;

import java.util.Map;

/**
 * Output Data for the Login Use Case.
 */
public class LoginOutputData {

    private final String username;
    // id -> name
    private final Map<String, String> groups;

    public LoginOutputData(String username, Map<String, String> groups) {
        this.username = username;
        this.groups = groups;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, String> getGroups() {
        return groups;
    }
}
