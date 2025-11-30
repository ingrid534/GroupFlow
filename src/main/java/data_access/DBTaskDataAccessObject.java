package data_access;

import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

import entity.task.Task;
import entity.task.TaskFactory;
import org.bson.types.ObjectId;
import use_case.creategrouptask.CreateGroupTaskDataAccessInterface;
import use_case.editgrouptasks.EditGroupTasksDataAccessInterface;
import use_case.viewgrouptasks.ViewGroupTasksDataAccessInterface;
import use_case.viewtasks.ViewTasksDataAccessInterface;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A data access object (DAO) for managing tasks in a MongoDB database.
 * Implements multiple interfaces to support various task-related use cases.
 */
public class DBTaskDataAccessObject implements ViewTasksDataAccessInterface, ViewGroupTasksDataAccessInterface,
        CreateGroupTaskDataAccessInterface, EditGroupTasksDataAccessInterface {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private final TaskFactory taskFactory;
    private final MongoCollection<Document> taskCollection;

    private static final String TASK_ID_FIELD = "_id";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String GROUP_ID_FIELD = "group";
    private static final String DUE_DATE_FIELD = "deadline";
    private static final String COMPLETED_FIELD = "completed";
    private static final String ASSIGNEES_FIELD = "assignees";

    /**
     * Constructs a new DBTaskDataAccessObject.
     *
     * @param taskFactory      A factory for creating Task objects.
     * @param connectionString The connection string for the MongoDB database.
     * @param dbName           The name of the database to connect to.
     */
    public DBTaskDataAccessObject(TaskFactory taskFactory,
                                  String connectionString,
                                  String dbName) {

        this.taskFactory = taskFactory;
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(dbName);
        this.taskCollection = database.getCollection("tasks");
    }

    /**
     * Retrieves a list of tasks associated with a specific user.
     *
     * @param username The username of the user whose tasks are to be retrieved.
     * @return A list of tasks for the specified user.
     */
    @Override
    public List<Task> getTasksForUser(String username) {
        List<Task> result = new ArrayList<>();

        final FindIterable<Document> taskDocs = taskCollection.find(
                eq(ASSIGNEES_FIELD, username)
        );

        for (Document td : taskDocs) {
            if (td == null) {
                continue;
            }
            result.add(extractTaskFromDocument(td));
        }

        return result;
    }

    /**
     * Retrieves a specific task by its ID.
     *
     * @param taskID The ID of the task to retrieve.
     * @return The task with the specified ID.
     * @throws RuntimeException if no task exists with the specified `TaskID`.
     */
    @Override
    public Task getTask(String taskID) throws RuntimeException {
        // get the ObjectId to properly compare with MongoDB's generated ID
        ObjectId objectTaskID = new ObjectId(taskID);
        Document taskDoc = taskCollection.find(eq(TASK_ID_FIELD, objectTaskID)).first();

        if (taskDoc == null) {
            throw new RuntimeException("No task with this taskID exists.");
        }

        return extractTaskFromDocument(taskDoc);

    }

    /**
     * Retrieves a list of tasks associated with a specific group.
     *
     * @param groupId The ID of the group whose tasks are to be retrieved.
     * @return A list of tasks for the specified group.
     */
    @Override
    public List<Task> getTasksForGroup(String groupId) {
        List<Task> result = new ArrayList<>();

        final FindIterable<Document> taskDocs = taskCollection.find(
                eq(GROUP_ID_FIELD, groupId)
        );

        for (Document td : taskDocs) {
            if (td == null) {
                continue;
            }

            result.add(extractTaskFromDocument(td));
        }

        return result;
    }

    private Task extractTaskFromDocument(Document document) {
        String taskID = document.getObjectId(TASK_ID_FIELD).toHexString();
        String description = document.getString(DESCRIPTION_FIELD);
        String groupID = document.getString(GROUP_ID_FIELD);
        String dueDate = document.getString(DUE_DATE_FIELD);
        boolean isCompleted = document.getBoolean(COMPLETED_FIELD, false);
        List<String> assignees = document.getList(ASSIGNEES_FIELD, String.class);

        if (dueDate == null) {
            return taskFactory.createWithoutDeadline(taskID, description, groupID, isCompleted, assignees);
        }

        return taskFactory.createWithDeadline(taskID, description, groupID,
                isCompleted, assignees, LocalDateTime.parse(dueDate));
    }

    /**
     * Saves a task to the database.
     * If the task has an existing ID, the existing task with that ID is updated in the database.
     * If the task has no ID, we insert it into the database.
     *
     * @param task The task object to be saved.
     */
    @Override
    public void upsertTask(Task task) {
        final Document taskDoc = new Document()
                .append(DESCRIPTION_FIELD, task.getDescription())
                .append(GROUP_ID_FIELD, task.getGroup())
                .append(COMPLETED_FIELD, task.isCompleted())
                .append(ASSIGNEES_FIELD, task.getAssignees());

        if (task.hasDueDate()) {
            taskDoc.append(DUE_DATE_FIELD, task.getDueDate());
        }

        try {
            if (task.getID().isEmpty()) {
                // New task, insert
                InsertOneResult result = taskCollection.insertOne(taskDoc);
                ObjectId objectId = Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue();
                String taskID = objectId.toHexString();
                task.setID(taskID);
            } else {
                // Existing task, update
                ObjectId id = new ObjectId(task.getID());
                taskCollection.replaceOne(eq(TASK_ID_FIELD, id), taskDoc);
            }
        } catch (MongoWriteException mwe) {
            throw new RuntimeException("Failed to save/update task: " + mwe.getMessage(), mwe);
        }
    }

}
