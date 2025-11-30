package use_case.create_schedule;

import entity.group.Group;
import entity.membership.Membership;
import entity.schedule.Schedule;
import entity.user.User;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.create_group.CreateGroupOutputBoundary;

import java.util.ArrayList;
import java.util.List;

import data_access.DBMembershipDataAccessObject;

public class CreateScheduleInteractor implements CreateScheduleInputBoundary {
    private final CreateScheduleUserDataAccessInterface userDataAccessObject;
    private final CreateScheduleGroupDataAccessInterface groupDataAccessObject;
    private final CreateScheduleOutputBoundary createSchedulePresenter;
    private final DBMembershipDataAccessObject membershipDataAccessObject;

    // TODO: Add constructor with DAI, output boundary
    public CreateScheduleInteractor(CreateScheduleUserDataAccessInterface userDataAccessObject,
                                    CreateScheduleGroupDataAccessInterface groupDataAccessObject,
                                    CreateScheduleOutputBoundary createScheduleOutputBoundary,
                                    DBMembershipDataAccessObject membershipDataAccessObject) {
        this.userDataAccessObject = userDataAccessObject;
        this.groupDataAccessObject = groupDataAccessObject;
        this.createSchedulePresenter = createScheduleOutputBoundary;
        this.membershipDataAccessObject = membershipDataAccessObject;

    }
    
    @Override
    public void execute(CreateScheduleInputData createScheduleInputData) {
        // TODO: make user DAO implement createScheduleUser DAI
        final String userID = userDataAccessObject.get(userDataAccessObject.getCurrentUsername()).getUserID();

        // add method to return current group ID in group DAO
        final String groupID = groupDataAccessObject.getCurrentGroup();

        final boolean[][] availabilityGrid = createScheduleInputData.getAvailabilityGrid();

        // Uncomment once implemented. Save each user's own availability in the db so they can reuse across groups.
        // TODO: update user DAO to save schedule in db
        userDataAccessObject.saveSchedule(availabilityGrid);

        // look through all the users in this group to get all their availabilities
        List<User> allUsers = new ArrayList<>();
        List<Membership> memberships = membershipDataAccessObject.getMembersForGroup(groupID);
        for (Membership m: memberships) {
            User newUser = userDataAccessObject.get(m.getUsername());
            allUsers.add(newUser);
        }

        // go through the group schedule array and recalculate availability
        // TODO: need to implement getGroupByID method in group DAO
        Group group = groupDataAccessObject.getGroup(groupID);
        
        // update master sched with user schedule
        // add method to get user schedule in user entity
        int[][] masterSchedule = new int[7][12];
        for (User user: allUsers) {
            boolean[][] userSched = user.getSchedule();

            for (int i = 0; i < userSched.length; i++) {
                for (int j = 0; j < userSched[i].length; j++) {
                    if (userSched[i][j]) {
                        // increment the master schedule by 1 at given day i and hour j
                        masterSchedule[i][j]++;
                    }
                }
            }
        }

        groupDataAccessObject.saveMasterShedule(masterSchedule);
        final CreateScheduleOutputData createScheduleOutputData = 
            new CreateScheduleOutputData(masterSchedule, group.getSize());

    }

    @Override
    public void openCreateScheduleModal() {
        System.out.println("i'm still working on it....");
        createSchedulePresenter.openCreateScheduleModal();
    }
}
