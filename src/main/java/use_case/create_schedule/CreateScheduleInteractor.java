package use_case.create_schedule;

import entity.group.Group;
import entity.membership.Membership;
import entity.user.User;
import interface_adapter.view_group_schedule.GroupSchedulePresenter;

import java.util.ArrayList;
import java.util.List;

import data_access.DBMembershipDataAccessObject;

public class CreateScheduleInteractor implements CreateScheduleInputBoundary {
    private final CreateScheduleUserDataAccessInterface userDataAccessObject;
    private final CreateScheduleGroupDataAccessInterface groupDataAccessObject;
    private final CreateScheduleOutputBoundary createSchedulePresenter;
    private final DBMembershipDataAccessObject membershipDataAccessObject;
    private final GroupSchedulePresenter groupSchedulePresenter;

    public CreateScheduleInteractor(CreateScheduleUserDataAccessInterface userDataAccessObject,
                                    CreateScheduleGroupDataAccessInterface groupDataAccessObject,
                                    CreateScheduleOutputBoundary createScheduleOutputBoundary,
                                    DBMembershipDataAccessObject membershipDataAccessObject,
                                    GroupSchedulePresenter groupSchedulePresenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.groupDataAccessObject = groupDataAccessObject;
        this.createSchedulePresenter = createScheduleOutputBoundary;
        this.membershipDataAccessObject = membershipDataAccessObject;
        this.groupSchedulePresenter = groupSchedulePresenter;

    }
    
    @Override
    public void execute(CreateScheduleInputData createScheduleInputData) {
        final User user = userDataAccessObject.get(userDataAccessObject.getCurrentUsername());

        // get id of current group
        final String groupID = groupDataAccessObject.getCurrentGroupID();

        // save user schedule in db
        final boolean[][] availabilityGrid = createScheduleInputData.getAvailabilityGrid();

        user.setSchedule(availabilityGrid);
        userDataAccessObject.saveSchedule(user);

        // look through all the users in this group to get all their availabilities
        List<User> allUsers = new ArrayList<>();
        List<Membership> memberships = membershipDataAccessObject.getMembersForGroup(groupID);
        for (Membership m: memberships) {
            User newUser = userDataAccessObject.get(m.getUsername());
            allUsers.add(newUser);
        }

        Group group = groupDataAccessObject.getGroup(groupID);
        
        // update master sched with user schedule
        // add method to get user schedule in user entity
        int[][] masterSchedule = new int[7][12];
        for (User member: allUsers) {
            boolean[][] userSched = member.getSchedule();

            for (int i = 0; i < userSched.length; i++) {
                for (int j = 0; j < userSched[i].length; j++) {
                    if (userSched[i][j]) {
                        // increment the master schedule by 1 at given day i and hour j
                        masterSchedule[i][j]++;
                    }
                }
            }
        }

        group.setMasterSchedule(masterSchedule);
        groupDataAccessObject.saveMasterSchedule(group);
        final CreateScheduleOutputData createScheduleOutputData = 
            new CreateScheduleOutputData(masterSchedule, group.getSize());
        createSchedulePresenter.prepareSuccessView(createScheduleOutputData);
        groupSchedulePresenter.prepareSuccessView(createScheduleOutputData);

    }

    @Override
    public void openCreateScheduleModal() {
        createSchedulePresenter.openCreateScheduleModal();
    }
}
