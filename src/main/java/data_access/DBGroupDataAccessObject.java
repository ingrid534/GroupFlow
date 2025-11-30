package data_access;

import com.mongodb.client.*;
import entity.group.Group;
import entity.group.GroupFactory;
import entity.group.GroupType;
import entity.membership.Membership;
import entity.membership.MembershipFactory;
import entity.user.UserRole;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.creategrouptask.CreateGroupTaskGroupDataAccessInterface;
import use_case.login.LoginGroupsDataAccessInterface;
import org.bson.Document;
import use_case.viewgrouptasks.ViewGroupTasksGroupDataAccessInterface;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * A MongoDB-based implementation of all user-related data access operations.
 *
 * <p>
 * This class handles creating, retrieving, updating, and checking the existence
 * of user records stored inside a MongoDB "users" collection.
 * </p>
 */
public class DBGroupDataAccessObject implements
        CreateGroupDataAccessInterface,
        LoginGroupsDataAccessInterface,
        CreateGroupTaskGroupDataAccessInterface,
        ViewGroupTasksGroupDataAccessInterface {

    private static final String GROUP_NAME = "name";
    private static final String GROUP_CODE = "joinCode";
    private static final String GROUP_TYPE = "type";

    private static final String MEMBERSHIP_GROUP_NAME_FIELD = "group";
    private static final String MEMBERSHIP_USERNAME_FIELD = "user";
    private static final String MEMBERSHIP_ROLE_FIELD = "role";
    private static final String MEMBERSHIP_APPROVED_FIELD = "approved";

    private static final String JOIN_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int JOIN_CODE_LENGTH = 6;

    private final GroupFactory groupFactory;
    private final MembershipFactory membershipFactory;
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
     * @param membershipFactory A factory for creating Membership entities.
     * @param connectionString  The MongoDB connection string.
     * @param dbName            The name of the database to use.*/
    public DBGroupDataAccessObject(GroupFactory groupFactory, MembershipFactory membershipFactory,
                                   String connectionString, String dbName) {
        this.groupFactory = groupFactory;
        this.membershipFactory = membershipFactory;
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(dbName);
        this.groupsCollection = database.getCollection("groups");
        this.membershipsCollection = database.getCollection("memberships");
    }

    /**
     * Saves a new group in the database.
     * A unique 6 character join code is generated and used as the group ID.
     * The group document includes the group name, join code, and type
     * Note: this method only stores the group itself.
     * Any membership creation, including assigning the creator as moderator,
     * is handled by the Create Group use case through the Membership DAO.
     * @param group the Group entity to save
     **/
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
     **/
    @Override
    public List<Group> getGroupsForUser(String username) {
        List<Group> result = new ArrayList<>();

        // For now, this DAO also looks into the memberships collection so we can
        // answer "which groups does this user belong to".
        // If needed, this could be moved into a Membership DAO and composed in an interactor.
        for (Document membershipDoc : membershipsCollection.find(
                and(eq("user", username), eq("approved", true)))) {

            String joinCode = membershipDoc.getString(MEMBERSHIP_GROUP_NAME_FIELD);
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

            result.add(extractGroupFromDocument(groupDoc));
        }

        return result;
    }

    /**
     * Retrieves the Group object associated with the given `groupID`.
     * @param groupID the unique ID of the group to retrieve.
     * @return the Group object in the database with the given `groupID`.
     * @throws RuntimeException if there exists no group with the given ID.
     */
    @Override
    public Group getGroup(String groupID) throws RuntimeException {
        final Document groupDoc = groupsCollection.find(eq(GROUP_CODE, groupID)).first();

        if (groupDoc == null) {
            throw new RuntimeException(String.format("No group with ID [%s] exists.", groupID));
        }

        return extractGroupFromDocument(groupDoc);

    }

    /**
     * Extracts a Group object from a MongoDB document.
     *
     * @param groupDoc The MongoDB document containing group data.
     * @return A Group object created from the document data.
     */
    private Group extractGroupFromDocument(Document groupDoc) {
        String name = groupDoc.getString(GROUP_NAME);
        String joinCode = groupDoc.getString(GROUP_CODE);
        String typeStr = groupDoc.getString(GROUP_TYPE);
        GroupType groupType = GroupType.valueOf(typeStr);

        Group group = groupFactory.create(name, joinCode, groupType);

        FindIterable<Document> membershipDocs = membershipsCollection.find(
                eq(MEMBERSHIP_GROUP_NAME_FIELD, joinCode)
        );

        addMembersToGroup(membershipDocs, group);

        return group;
    }

    /**
     * Adds members to a Group object based on membership documents.
     *
     * @param membershipDocs The MongoDB documents containing membership data.
     * @param group          The Group object to add members to.
     */
    private void addMembersToGroup(FindIterable<Document> membershipDocs, Group group) {
        for (Document md : membershipDocs) {
            if (md == null) {
                continue;
            }

            String username = md.getString(MEMBERSHIP_USERNAME_FIELD);
            String groupID = md.getString(MEMBERSHIP_GROUP_NAME_FIELD);
            UserRole role = UserRole.valueOf(md.getString(MEMBERSHIP_ROLE_FIELD));
            boolean approved = md.getBoolean(MEMBERSHIP_APPROVED_FIELD, false);

            Membership membership = membershipFactory.create(username, groupID, role, approved);

            group.addMembership(membership);
        }
    }

}
