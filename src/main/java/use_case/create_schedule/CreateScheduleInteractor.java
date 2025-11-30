package use_case.create_schedule;

import entity.group.Group;
import entity.schedule.Schedule;
import entity.user.User;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.create_group.CreateGroupOutputBoundary;

import java.util.List;

public class CreateScheduleInteractor implements CreateScheduleInputBoundary {
    private final CreateGroupDataAccessInterface groupDataAccessObject;
    private final CreateScheduleOutputBoundary createSchedulePresenter;

    // TODO: Add constructor with DAI, output boundary
    public CreateScheduleInteractor(CreateGroupDataAccessInterface groupDataAccessObject) {
        this.groupDataAccessObject = groupDataAccessObject;
    }
    
    @Override
    public void execute(CreateScheduleInputData createScheduleInputData) {
        final String userID = createScheduleInputData.getUserID();
        final String groupID = createScheduleInputData.getGroupID();
        final boolean[][] availabilityGrid = createScheduleInputData.getAvailabilityGrid();
        
        Schedule schedule = new Schedule(userID, availabilityGrid);
        // TODO: save this with the schedule data access object.

        // Uncomment once implemented. Save each user's own availability in the db so they can reuse across groups.
        // TODO: fix this method. for now: assuming that a user can just grab their schedule
        // TODO: update user entity with schedule
        // scheduleDataAccessObject.save(schedule); 

        // look through all the users in this group to get all their availabilities
        // TODO: need to implement getUsersForGroup method in group DAO
        List<User> allUsers = groupDataAccessObject.getUsersForGroup(groupID);

        // go through the group schedule array and recalculate availability
        // TODO: need to implement getGroupByID method in group DAO
        Group group = this.groupDataAccessObject.getGroupByID(groupID);

        // TODO: add masterSched to group entity - don't need to do this but keeping as a reminder to 
        // int[][] masterSched = group.getMasterSched();
        
        // update master sched with user schedule
        // add method to get user schedule in user entity
        // add method to update master schedule in group entity
        for (User user: allUsers) {
            boolean[][] userSched = user.getSchedule();

            for (int i = 0; i < userSched.length; i++) {
                for (int j = 0; j < userSched[i].length; j++) {
                    if (userSched[i][j]) {
                        // increment the master schedule by 1 at given day i and hour j
                        group.addToSchedule(i, j);
                    }
                }
            }
        }
        // save the group with the modified schedule in db
        groupDataAccessObject.save(group);

    }

    @Override
    public void openCreateScheduleModal() {
        System.out.println("i'm still working on it....");
        createSchedulePresenter.openCreateScheduleModal();
    }
}
