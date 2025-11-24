package data_access;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entity.group.Group;
import entity.group.GroupFactory;
import entity.group.GroupType;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.login.LoginGroupsDataAccessInterface;
import org.bson.Document;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * A MongoDB-based implementation of all user-related data access operations.
 *
 * <p>
 * This class handles creating, retrieving, updating, and checking the existence
 * of user records stored inside a MongoDB "users" collection.
 * </p>
 */
public class DBGroupDataAccessObject implements CreateGroupDataAccessInterface,
        LoginGroupsDataAccessInterface {

    private static final String GROUP_NAME = "name";
    private static final String GROUP_CODE = "joinCode";
    private static final String GROUP_TYPE = "type";

    private static final String JOIN_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int JOIN_CODE_LENGTH = 6;

    private final GroupFactory groupFactory;
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> groupsCollection;

    /**
     * Note: this DAO also reads from the "memberships" collection in order to
     * implement getGroupsForUser. In a more separated design, memberships
     * could be handled by a dedicated Membership DAO instead.
     */
    private final MongoCollection<Document> membershipsCollection;

    private final SecureRandom random = new SecureRandom();

    /**
     * Constructs a DBGroupDataAccessObject and initializes a MongoDB client,
     * database reference, and "groups" collection.
     *
     * @param groupFactory      A factory for creating Group entities.
     * @param connectionString  The MongoDB connection string.
     * @param dbName            The name of the database to use.*/
    public DBGroupDataAccessObject(GroupFactory groupFactory, String connectionString, String dbName) {
        this.groupFactory = groupFactory;
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(dbName);
        this.groupsCollection = database.getCollection("groups");
        this.membershipsCollection = database.getCollection("memberships");
    }

    /**
     * Saves a new group in the database.
	 * A unique 6 character join code is generated and used as the group ID.
	 * The group document includes the group name, join code, and type.
	 * Note: this method only stores the group itself.
	 * Any membership creation, including assigning the creator as moderator,
	 * is handled by the Create Group use case through the Membership DAO.
	 * @param group the Group entity to save
     * */
    @Override
    public void save(Group group) {
        // Generate a unique 6 character join code
        String joinCode = generateUniqueJoinCode();

        // We use the join code as the group identifier in the database.
        group.setGroupId(joinCode);

        Document doc = new Document()
                .append(GROUP_NAME, group.getName())
                .append(GROUP_CODE, joinCode)
                .append(GROUP_TYPE, group.getGroupType().name());

        groupsCollection.insertOne(doc);
    }

    private String generateUniqueJoinCode() {
        String code;
        do {
            code = generateRandomJoinCode();
        } while (joinCodeExists(code));
        return code;
    }

    private String generateRandomJoinCode() {
        StringBuilder sb = new StringBuilder(JOIN_CODE_LENGTH);
        for (int i = 0; i < JOIN_CODE_LENGTH; i++) {
            int idx = random.nextInt(JOIN_CODE_CHARS.length());
            sb.append(JOIN_CODE_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    private boolean joinCodeExists(String code) {
        Document existing = groupsCollection
                .find(eq(GROUP_CODE, code))
                .projection(new Document("_id", 1))
                .first();
        return existing != null;
    }

    /**
     * Retrieves all groups that a given user belongs to.
	 * This method looks up the memberships collection to find all entries
	 * where the membership's "user" field matches the provided username.
	 * For each membership, the corresponding group document is loaded
	 * and converted into a Group entity through the GroupFactory.
	 * @param username the username whose group memberships are requested
	 * @return a list of Group entities the user is a member of
	 */
    @Override
    public List<Group> getGroupsForUser(String username) {
        List<Group> result = new ArrayList<>();

        // For now, this DAO also looks into the memberships collection so we can
        // answer "which groups does this user belong to".
        // If needed, this could be moved into a Membership DAO and composed in an interactor.
        for (Document membershipDoc : membershipsCollection.find(eq("user", username))) {

            String joinCode = membershipDoc.getString("group");
            if (joinCode == null) {
                continue;
            }

            // Look up the group with this join code
            Document groupDoc = groupsCollection
                    .find(eq(GROUP_CODE, joinCode))
                    .first();

            if (groupDoc == null) {
                continue;
            }

            String name = groupDoc.getString(GROUP_NAME);
            String typeStr = groupDoc.getString(GROUP_TYPE);

            GroupType groupType = GroupType.valueOf(typeStr);

            Group group = groupFactory.create(name, joinCode, groupType);

            result.add(group);
        }

        return result;
    }

}
