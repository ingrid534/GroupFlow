package entity.user;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import entity.membership.Membership;

/**
 * A simple entity representing a user. Users have a username and password..
 */
public class User {

    private final String userID;
    private String name;
    private final String password;
    private List<Membership> memberships;
    private List<String> tasks;

    /**
     * Creates a new user with the given non-empty name and non-empty password.
     * 
     * @param name     the username
     * @param password the password
     * @throws IllegalArgumentException if the password or name are empty
     */
    public User(String name, String password) {
        if ("".equals(name)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if ("".equals(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.userID = UUID.randomUUID().toString();
        this.name = name;
        this.password = password;
        this.memberships = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getTasks() {
        return tasks;
    }

    /**
     * 
     * @return A list of all the group IDs associated with each group this user is
     *         currently a member of.
     */
    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();

        // will fix after Membership updated
        for (Membership m : memberships) {
            groups.add(m.getGroup());
        }

        return groups;
    }

    public void removeMembership(Membership membership) {
        this.memberships.remove(membership);
    }

    public void addMembership(Membership membership) {
        if (!memberships.contains(membership)) {
            memberships.add(membership);
        }
    }

    public void addTask(String taskID) {
        this.tasks.add(taskID);
    }

    public void removeTask(String taskID) {
        this.tasks.remove(taskID);
    }

}
