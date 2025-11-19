package entity.user;

/**
 * Factory for creating CommonUser objects.
 */
public class UserFactory {

    // TODO: add String userID to factory once db is implemented.
    public User create(String name, String password) {
        return new User(name, password);
    }
}
