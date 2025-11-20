package data_access;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import entity.user.User;
import entity.user.UserFactory;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.create_group.LoggedInDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

/**
 * A MongoDB-based implementation of all user-related data access operations.
 * This class handles creating, retrieving, updating, and checking the existence
 * of user records stored inside a MongoDB "users" collection.
 */
public class DBUserDataAccessObject implements SignupUserDataAccessInterface,
        LoginUserDataAccessInterface,
        ChangePasswordUserDataAccessInterface,
        LogoutUserDataAccessInterface,
        LoggedInDataAccessInterface {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private final UserFactory userFactory;
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> usersCollection;

    private String currentUsername;

    /**
     * Constructs a {@code DBUserDataAccessObject} and initializes a MongoDB client,
     * database reference, and "users" collection.
     *
     * @param userFactory      A factory for creating {@link User} entities.
     * @param connectionString The MongoDB connection string for the cluster.
     * @param dbName           The name of the database to use.
     */
    public DBUserDataAccessObject(UserFactory userFactory, String connectionString, String dbName) {
        this.userFactory = userFactory;
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(dbName);
        this.usersCollection = database.getCollection("users");
    }

    /**
     * Retrieves a user by username from MongoDB.
     *
     * @param username The username of the user to retrieve.
     * @return A {@link User} object if found.
     * @throws RuntimeException If no user with the given username exists.
     */
    @Override
    public User get(String username) {
        final Document doc = usersCollection.find(eq(USERNAME, username)).first();
        if (doc == null) {
            throw new RuntimeException("User not found: " + username);
        }

        final String name = doc.getString(USERNAME);
        final String password = doc.getString(PASSWORD);

        return userFactory.create(name, password);
    }

    /**
     * Stores the currently logged-in username.
     *
     * @param name The username of the current user.
     */
    @Override
    public void setCurrentUsername(String name) {
        currentUsername = name;
    }

    /**
     * Returns the username of the currently logged-in user.
     *
     * @return The current username, or null if no user is logged in.
     */
    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Checks whether a user with the given username exists in MongoDB.
     *
     * @param username The username to check.
     * @return True if the user exists, false otherwise.
     */
    @Override
    public boolean existsByName(String username) {
        final Document doc = usersCollection.find(eq(USERNAME, username))
                .projection(new Document("_id", 1))
                .first();
        return doc != null;
    }

    /**
     * Saves a new user into MongoDB
     * Inserts a new document with "username" and "password" fields.
     *
     * @param user The user to save.
     * @throws RuntimeException If insertion fails (e.g., duplicate username).
     */
    @Override
    public void save(User user) {
        final Document newUser = new Document()
                .append(USERNAME, user.getName())
                .append(PASSWORD, user.getPassword());

        try {
            usersCollection.insertOne(newUser);
        } catch (MongoWriteException mwe) {
            throw new RuntimeException("Failed to save user: " + mwe.getMessage(), mwe);
        }
    }

    /**
     * Updates a user's password in MongoDB.
     *
     * @param user The user with the updated password.
     * @throws RuntimeException If the user does not exist.
     */
    @Override
    public void changePassword(User user) {
        final UpdateResult result = usersCollection.updateOne(
                eq(USERNAME, user.getName()),
                set(PASSWORD, user.getPassword()));

        if (result.getMatchedCount() == 0) {
            throw new RuntimeException("User not found: " + user.getName());
        }
    }
}
