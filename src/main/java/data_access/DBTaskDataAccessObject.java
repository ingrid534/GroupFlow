package data_access;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import entity.group.Group;
import entity.user.User;
import entity.user.UserFactory;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.viewtasks.ViewTasksDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class DBTaskDataAccessObject implements ViewTasksDataAccessInterface {
    // to be implemented
}
