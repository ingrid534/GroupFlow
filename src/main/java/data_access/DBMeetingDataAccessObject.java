package data_access;

import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

import entity.meeting.Meeting;
import entity.meeting.MeetingFactory;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * A data access object (DAO) for managing meetings in a MongoDB database.
 * Implements meeting persistence for various meeting-related use cases.
 */
public class DBMeetingDataAccessObject {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MeetingFactory meetingFactory;
    private final MongoCollection meetingCollection;
    private static final String MEETING_ID_FIELD = "_id";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String GROUP_ID_FIELD = "group";

    /**
     * Constructs a new DBMeetingDataAccessObject.
     *
     * @param meetingFactory A factory for creating Meeting objects.
     * @param connectionString The connection string for the MongoDB database.
     * @param dbName The name of the database to connect to.
     */
    public DBMeetingDataAccessObject(MeetingFactory meetingFactory,
                                     String connectionString,
                                     String dbName) {
        this.meetingFactory = meetingFactory;
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(dbName);
        this.meetingCollection = database.getCollection("meetings");
    }

    /**
     * Retrieves a specific meeting by its ID.
     *
     * @param meetingID The ID of the meeting to retrieve.
     * @return The meeting with the specified ID.
     * @throws RuntimeException if no meeting exists with the specified ID.
     */
    public Meeting getMeeting(String meetingID) {
        // get the ObjectId to properly compare with MongoDB's generated ID
        ObjectId objectMeetingID = new ObjectId(meetingID);
        Document meetingDoc = (Document) meetingCollection.find(eq(MEETING_ID_FIELD, objectMeetingID)).first();

        if (meetingDoc == null) {
            throw new RuntimeException("No meeting with this meetingID exists.");
        }

        return extractMeetingFromDocument(meetingDoc);
    }

    /**
     * Retrieves a list of meetings associated with a specific group.
     *
     * @param groupId The ID of the group whose meetings are to be retrieved.
     * @return A list of meetings for the specified group.
     */
    public List getMeetingsForGroup(String groupId) {
        List result = new ArrayList<>();
        final FindIterable meetingDocs = meetingCollection.find(
                eq(GROUP_ID_FIELD, groupId)
        );

        for (Object md : meetingDocs) {
            if (md == null) {
                continue;
            }

            result.add(extractMeetingFromDocument((Document) md));
        }

        return result;
    }

    /**
     * Saves a meeting to the database.
     * If the meeting has an existing ID, the existing meeting with that ID is updated in the database.
     * If the meeting has no ID, we insert it into the database.
     *
     * @param meeting The meeting object to be saved.
     */
    public void upsertMeeting(Meeting meeting) {
        final Document meetingDoc = new Document()
                .append(DESCRIPTION_FIELD, meeting.getDescription())
                .append(GROUP_ID_FIELD, meeting.getGroup());

        try {
            if (meeting.getID().isEmpty()) {
                InsertOneResult result = meetingCollection.insertOne(meetingDoc);
                ObjectId objectId = Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue();
                String meetingID = objectId.toHexString();
                meeting.setID(meetingID);
            } else {
                Document updateDoc = new Document("$set", meetingDoc);
                ObjectId id = new ObjectId(meeting.getID());
                meetingCollection.updateOne(eq(MEETING_ID_FIELD, id), updateDoc);
            }
        } catch (MongoWriteException mwe) {
            throw new RuntimeException("Failed to save/update meeting: " + mwe.getMessage(), mwe);
        }
    }

    /**
     * Extracts a Meeting object from a MongoDB Document.
     *
     * @param document The MongoDB document containing meeting data.
     * @return A Meeting object.
     */
    private Meeting extractMeetingFromDocument(Document document) {
        String meetingID = document.getObjectId(MEETING_ID_FIELD).toHexString();
        String description = document.getString(DESCRIPTION_FIELD);
        String groupID = document.getString(GROUP_ID_FIELD);

        return meetingFactory.createWithDeadline(meetingID, description, groupID);
    }
}
