package data_access;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entity.membership.Membership;
import entity.membership.MembershipFactory;
import entity.user.UserRole;
import org.bson.Document;
import use_case.create_group.CreateGroupMembershipDataAccessInterface;
import use_case.manage_members.remove_member.RemoveMemberDataAccessInterface;
import use_case.manage_members.respond_request.RespondRequestDataAccessInterface;
import use_case.manage_members.update_role.UpdateRoleDataAccessInterface;
import use_case.manage_members.view_members.ViewMembersMembershipDataAccessInterface;
import use_case.manage_members.view_pending.ViewPendingMembershipDataAccessInterface;
import use_case.creategrouptask.CreateGroupTasksMembershipDataAccessInterface;
import use_case.editgrouptasks.EditGroupTasksMembershipDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * A MongoDB backed data access object for membership records.
 * Uses {@link MembershipFactory} to create Membership instances when reading
 * documents from the database.
 * Assumes Membership has a constructor that matches the factory signature used
 * by MembershipFactory (for example create(userID, groupID, role, approved)).
 */
public class DBMembershipDataAccessObject implements
        CreateGroupMembershipDataAccessInterface,
        ViewMembersMembershipDataAccessInterface,
        ViewPendingMembershipDataAccessInterface,
        CreateGroupTasksMembershipDataAccessInterface,
        EditGroupTasksMembershipDataAccessInterface,
        RemoveMemberDataAccessInterface,
        RespondRequestDataAccessInterface,
        UpdateRoleDataAccessInterface {

    private static final String USER_FIELD = "user";
    private static final String GROUP_FIELD = "group";
    private static final String ROLE_FIELD = "role";
    private static final String APPROVED_FIELD = "approved";

    private final MembershipFactory membershipFactory;
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> membershipsCollection;

    /**
     * Constructs a DBMembershipDataAccessObject and initializes the Mongo client,
     * database reference, and memberships collection.
     *
     * @param membershipFactory Factory used to create Membership objects.
     * @param connectionString  MongoDB connection string.
     * @param dbName            Database name.
     */
    public DBMembershipDataAccessObject(MembershipFactory membershipFactory,
                                        String connectionString,
                                        String dbName) {
        this.membershipFactory = membershipFactory;
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(dbName);
        this.membershipsCollection = database.getCollection("memberships");
    }

    /**
     * Saves a new membership document into MongoDB.
     *
     * @param membership The membership to save.
     * @throws RuntimeException If insertion fails (for example duplicate).
     */
    @Override
    public void save(Membership membership) {
        final Document doc = new Document()
                .append(USER_FIELD, membership.getUsername())
                .append(GROUP_FIELD, membership.getGroup())
                // enum stored as String
                .append(ROLE_FIELD, membership.getRole().name())
                // boolean
                .append(APPROVED_FIELD, membership.isApproved());

        try {
            membershipsCollection.insertOne(doc);
        } catch (MongoWriteException mwe) {
            throw new RuntimeException("Failed to save membership: " + mwe.getMessage(), mwe);
        }
    }

    /**
     * Retrieves a membership by user id and group id.
     *
     * @param userID  The user id.
     * @param groupID The group id.
     * @return The Membership object if found.
     * @throws RuntimeException If membership is not found.
     */
    @Override
    public Membership get(String userID, String groupID) {
        final Document doc = membershipsCollection.find(
                and(eq(USER_FIELD, userID), eq(GROUP_FIELD, groupID))
        ).first();

        if (doc == null) {
            throw new RuntimeException("Membership not found for user: " + userID + " group: " + groupID);
        }

        String user = doc.getString(USER_FIELD);
        String group = doc.getString(GROUP_FIELD);
        UserRole role = UserRole.valueOf(doc.getString(ROLE_FIELD));
        boolean approved = doc.getBoolean(APPROVED_FIELD, false);

        return membershipFactory.create(user, group, role, approved);
    }

    /**
     * Retrieves all approved memberships that belong to the specified group.
     *
     * @param groupID the ID of the group whose members should be returned
     * @return a list of Membership objects for that group
     */
    public List<Membership> getMembersForGroup(String groupID) {
        List<Membership> result = new ArrayList<>();

        for (Document doc : membershipsCollection.find(and(eq(GROUP_FIELD, groupID), eq(APPROVED_FIELD, true)))) {
            String user = doc.getString(USER_FIELD);
            String group = doc.getString(GROUP_FIELD);
            UserRole role = UserRole.valueOf(doc.getString(ROLE_FIELD));
            boolean approved = doc.getBoolean(APPROVED_FIELD, false);

            result.add(membershipFactory.create(user, group, role, approved));
        }

        return result;
    }

    /**
     * Retrieves all pending membership requests for the specified group.
     *
     * @param groupID the ID of the group
     * @return a list of pending Memberships for that group
     */
    public List<Membership> getPendingForGroup(String groupID) {
        List<Membership> result = new ArrayList<>();

        for (Document doc : membershipsCollection.find(and(eq(GROUP_FIELD, groupID), eq(APPROVED_FIELD, false)))) {
            String user = doc.getString(USER_FIELD);
            String group = doc.getString(GROUP_FIELD);
            UserRole role = UserRole.valueOf(doc.getString(ROLE_FIELD));
            boolean approved = doc.getBoolean(APPROVED_FIELD, false);

            result.add(membershipFactory.create(user, group, role, approved));
        }

        return result;
    }

    /**
     * Updates the role of an existing membership for the given group and user.
     * If no matching membership is found, the method completes silently.
     *
     * @param groupID  the ID of the group whose membership is being updated
     * @param username the username of the member whose role is being changed
     * @param newRole  the new role to assign to the member
     */
    @Override
    public void updateMembership(String groupID, String username, UserRole newRole) {
        membershipsCollection.updateOne(
                and(
                        eq(USER_FIELD, username),
                        eq(GROUP_FIELD, groupID)
                ),
                new Document("$set", new Document(ROLE_FIELD, newRole.name()))
        );
    }

    /**
     * Updates a pending membership request for the given group and user.
     * If {@code accepted} is true, the user's membership record is updated to an
     * accepted/active state. If {@code accepted} is false, the user's pending
     * membership record is removed entirely.
     *
     * @param groupID  the ID of the group whose membership request is being updated
     * @param username the username of the member whose request is being processed
     * @param accepted true to accept the request, false to decline and remove it
     */
    @Override
    public void updateMembership(String groupID, String username, boolean accepted) {
        if (accepted) {
            membershipsCollection.updateOne(
                    and(
                            eq(USER_FIELD, username),
                            eq(GROUP_FIELD, groupID),
                            eq(APPROVED_FIELD, false)
                    ),
                    new Document("$set", new Document(APPROVED_FIELD, true))
            );
        } else {
            membershipsCollection.deleteOne(
                    and(
                            eq(USER_FIELD, username),
                            eq(GROUP_FIELD, groupID),
                            eq(APPROVED_FIELD, false)
                    )
            );
        }
    }

    /**
     * Removes the membership record for the specified user in the given group.
     * If no such membership exists, the method completes silently.
     *
     * @param groupID  the ID of the group the user is being removed from
     * @param username the username of the member to remove
     */
    @Override
    public void removeMembership(String groupID, String username) {
        membershipsCollection.deleteOne(
                and(eq(USER_FIELD, username), eq(GROUP_FIELD, groupID))
        );
    }
}
