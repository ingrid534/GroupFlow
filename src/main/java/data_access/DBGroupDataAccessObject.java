package data_access;

import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;

import entity.group.Group;
import entity.group.GroupFactory;
import entity.group.GroupType;
import entity.membership.Membership;
import entity.membership.MembershipFactory;
import entity.user.UserRole;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.create_schedule.CreateScheduleGroupDataAccessInterface;
import use_case.creategrouptask.CreateGroupTaskGroupDataAccessInterface;
import use_case.login.LoginGroupsDataAccessInterface;
import org.bson.Document;
import use_case.viewgrouptasks.ViewGroupTasksGroupDataAccessInterface;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

/**
 * A MongoDB-based implementation of all user-related data access operations.
 *
 * <p>
 * This class handles creating, retrieving, updating, and checking the existence
 * of user records stored inside a MongoDB "users" collection.
 * </p>
 */
public class DBGroupDataAccessObject implements CreateGroupDataAccessInterface,
        LoginGroupsDataAccessInterface,
        CreateGroupTaskGroupDataAccessInterface, ViewGroupTasksGroupDataAccessInterface,
        CreateScheduleGroupDataAccessInterface {

    private static final String GROUP_NAME = "name";
    private static final String GROUP_CODE = "joinCode";
    private static final String GROUP_TYPE = "type";
    private static final String SCHEDULE = "groupSchedule";

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

    private String currentGroupID;

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
        List<List<Integer>> dbSchedule = convertScheduleToDB(group.getMasterSchedule());

        Document doc = new Document()
                .append(GROUP_NAME, group.getName())
                .append(GROUP_CODE, joinCode)
                .append(GROUP_TYPE, group.getGroupType().name())
                .append(SCHEDULE, dbSchedule);

        try {
            groupsCollection.insertOne(doc);
        } catch (MongoWriteException mwe) {
            throw new RuntimeException("Failed to save group: " + mwe.getMessage(), mwe);
        }
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

    @Override public void setCurrentGroupID(String groupID) {
        currentGroupID = groupID;
    }

    @Override
    public String getCurrentGroupID() {
        return currentGroupID;
    }

    @Override
    public void saveMasterSchedule(Group group) {
        final int[][] groupSchedule = group.getMasterSchedule();
        final UpdateResult result = groupsCollection.updateOne(
                eq(GROUP_CODE, group.getGroupID()),
                set(SCHEDULE, convertScheduleToDB(groupSchedule)));

        if (result.getMatchedCount() == 0) {
            throw new RuntimeException("Group not found " + group.getGroupID());
        }
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

        // supress unchecked type warning for casting
        @SuppressWarnings("unchecked")
        List<List<Integer>> schedule = (List<List<Integer>>) groupDoc.get(SCHEDULE);

        int[][] masterSchedule = convertToArrayFromDB(schedule);
        Group group = groupFactory.create(name, joinCode, groupType);
        group.setMasterSchedule(masterSchedule);

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

    /**
     * Convert the 2D array to a nested List to be stored in Mongo.
     * @param schedule the schedule to be converted
     * @return the nested list representing group schedule
     */
    private List<List<Integer>> convertScheduleToDB(int[][] schedule) {
        List<List<Integer>> result = new ArrayList<List<Integer>>(schedule.length);
        for (int[] i: schedule) {
            List<Integer> list = new ArrayList<Integer>(i.length);
            for (int j: i) {
                list.add(j);
            }
            result.add(list);
        }

        return result;

    }

    /**
     * Convert db schedule back to 2D array.
     * @param db_schedule the schedule taken from mongo.
     * @return the 2D array representing group schedule.
     */
    private int[][] convertToArrayFromDB(List<List<Integer>> db_schedule) {
        int[][] result = new int[db_schedule.size()][db_schedule.get(0).size()];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = db_schedule.get(i).get(j);
            }
        }

        return result;
    }

}
