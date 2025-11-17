package entity.user;

import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import entity.membership.Membership;
import entity.user.UserRole;
import entity.group.Group;
import entity.task.Task;

/**
 * A simple entity representing a user. Users have a username and password..
 */
public class User {

    private String name;
    private final String password;
    private List<Membership> memberships = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();

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
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * 
     * @return A list of all the groups this user is currently a member of.
     */
    public List<Group> getGroups() {
        List<Group> groups = new ArrayList<>();

        for (Membership m : memberships) {
            groups.add(m.getGroup());
        }

        return groups;
    }

    /**
     * 
     * @param group
     * @return The user's role in the given group. If the
     *         user is not in the given group, throw an exception.
     */
    // public UserRole getRole(Group group) throws NoSuchElementException {
    // for (Membership m : memberships) {
    // if (group.equals(m.getGroup())) {
    // return m.getRole();
    // }
    // }
    // throw new NoSuchElementException("User is not in this group.");
    // }

    /**
     * 
     * @param membership
     * @return Whether the user is a moderator in the
     *         given group. If the user is not in the group, throw
     *         an exception.
     * @throws NoSuchElementException
     */
    // public Boolean checkModerator(Membership membership) throws
    // NoSuchElementException {

    // }

    public void removeMembership(Membership membership) {
        this.memberships.remove(membership);
    }

    public void addMembership(Membership membership) {
        if (!memberships.contains(membership)) {
            memberships.add(membership);
        }
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

}
