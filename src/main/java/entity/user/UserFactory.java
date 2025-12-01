package entity.user;

/**
 * Factory for creating CommonUser objects.
 */
public class UserFactory {

    /**
     * Create a new user.
     * 
     * @param name     username for the user
     * @param password password for the user
     * @return the new user
     */
    public User create(String name, String password) {
        return new User(name, password);
    }
}
