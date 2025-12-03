package use_case.create_schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import data_access.InMemoryGroupDataAccessObject;
import data_access.InMemoryMembershipDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entity.group.Group;
import entity.group.GroupFactory;
import entity.group.GroupType;
import entity.membership.Membership;
import entity.membership.MembershipFactory;
import entity.user.User;
import entity.user.UserFactory;
import entity.user.UserRole;
import interface_adapter.schedule.view_schedule.ScheduleTabPresenter;
import interface_adapter.schedule.view_schedule.ScheduleTabViewModel;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.create_group.CreateGroupMembershipDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

public class CreateScheduleInteractorTest {
    
    @Test
    void successTestOneUser() {
        // Ingrid's availability
        boolean[][] ingridAvailability = new boolean[12][7];
        ingridAvailability[0][0] = true;  
        ingridAvailability[1][0] = true;  
        ingridAvailability[0][2] = true;  
        ingridAvailability[1][2] = true;  
        ingridAvailability[0][4] = true;  
        ingridAvailability[1][4] = true;  
        
        CreateScheduleInputData inputData = new CreateScheduleInputData("1234", ingridAvailability);

        // Create shared in-memory data store
        InMemoryUserDataAccessObject userDataAccess = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDataAccess = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDataAccess = new InMemoryMembershipDataAccessObject();

        // Use interfaces for clean architecture
        SignupUserDataAccessInterface userSetup = userDataAccess;
        LoginUserDataAccessInterface loginSetup = userDataAccess;
        CreateGroupDataAccessInterface groupSetup = groupDataAccess;
        CreateGroupMembershipDataAccessInterface membershipSetup = membershipDataAccess;
        
        CreateScheduleUserDataAccessInterface userRepository = userDataAccess;
        CreateScheduleGroupDataAccessInterface groupRepository = groupDataAccess;
        CreateScheduleMembershipDataAccessInterface membershipRepository = membershipDataAccess;

        // Set up users for test
        UserFactory userFactory = new UserFactory();
        User user1 = userFactory.create("ingrid", "ingrid@gmail.com", "password123");
        
        userSetup.save(user1);
        loginSetup.setCurrentUsername("ingrid");  // Ingrid is the logged-in user

        GroupFactory groupFactory = new GroupFactory();
        Group group = groupFactory.create("Ingrid's Group", "1234", GroupType.STUDY);
        
        MembershipFactory membershipFactory = new MembershipFactory();
        Membership membership1 = membershipFactory.create("ingrid", "1234", UserRole.MODERATOR, true);
        
        // Add membership to the group object
        group.addMembership(membership1);
        
        groupSetup.save(group);
        membershipSetup.save(membership1);

        // Expected output for master schedule
        int[][] expectedSchedule = new int[12][7];
        expectedSchedule[0][0] = 1; 
        expectedSchedule[1][0] = 1;  
        expectedSchedule[0][2] = 1;  
        expectedSchedule[1][2] = 1;  
        expectedSchedule[0][4] = 1;  
        expectedSchedule[1][4] = 1;  

        CreateScheduleOutputBoundary successPresenter = new CreateScheduleOutputBoundary() {
            @Override
            public void prepareSuccessView(CreateScheduleOutputData outputData) {
                int[][] actualSchedule = outputData.getMasterSchedule();
                assertEquals(1, outputData.getGroupSize());
                
                // Check all the time slots match
                for (int i = 0; i < 12; i++) {
                    for (int j = 0; j < 7; j++) {
                        assertEquals(expectedSchedule[i][j], actualSchedule[i][j],
                            "Mismatch at hour " + i + ", day " + j);
                    }
                }
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure is unexpected");
            }

            @Override
            public void openCreateScheduleModal() {
                // For UI purposes only
            }
        };

        // Create ViewModel and Presenter for UI updates
        ScheduleTabViewModel scheduleTabViewModel = new ScheduleTabViewModel();
        ScheduleTabPresenter groupSchedulePresenter = new ScheduleTabPresenter(scheduleTabViewModel);

        CreateScheduleInputBoundary interactor = new CreateScheduleInteractor(
            userRepository,
            groupRepository,
            successPresenter,
            membershipRepository,
            groupSchedulePresenter
        );

        interactor.execute(inputData);
    }

    @Test
    void successTestTwoUsers() {
        // Ingrid's availability
        boolean[][] ingridAvailability = new boolean[12][7];
        ingridAvailability[0][0] = true;  
        ingridAvailability[1][0] = true;  
        ingridAvailability[0][2] = true;  
        ingridAvailability[1][2] = true;  
        ingridAvailability[0][4] = true;  
        ingridAvailability[1][4] = true;

        // Alisa's availability
        boolean[][] alisaAvailability = new boolean[12][7];
        alisaAvailability[0][0] = true;  // (overlap with Ingrid)
        alisaAvailability[1][0] = true;  // (overlap with Ingrid)
        alisaAvailability[2][0] = true;  
        alisaAvailability[0][1] = true;  
        alisaAvailability[1][1] = true;  
        alisaAvailability[2][1] = true;
        
        CreateScheduleInputData inputData = new CreateScheduleInputData("1234", alisaAvailability);

        // Create shared in-memory data store
        InMemoryUserDataAccessObject userDataAccess = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDataAccess = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDataAccess = new InMemoryMembershipDataAccessObject();

        // Use interfaces for clean architecture
        SignupUserDataAccessInterface userSetup = userDataAccess;
        LoginUserDataAccessInterface loginSetup = userDataAccess;
        CreateGroupDataAccessInterface groupSetup = groupDataAccess;
        CreateGroupMembershipDataAccessInterface membershipSetup = membershipDataAccess;
        
        CreateScheduleUserDataAccessInterface userRepository = userDataAccess;
        CreateScheduleGroupDataAccessInterface groupRepository = groupDataAccess;
        CreateScheduleMembershipDataAccessInterface membershipRepository = membershipDataAccess;

        // Set up BOTH users
        UserFactory userFactory = new UserFactory();
        User user1 = userFactory.create("ingrid", "ingrid@gmail.com", "password123");
        User user2 = userFactory.create("alisa", "alisa@gmail.com", "password456");
        
        // Ingrid already submitted her schedule
        user1.setSchedule(ingridAvailability);
        userSetup.save(user1);
        userSetup.save(user2);
        
        // switch to Alisa as user
        loginSetup.setCurrentUsername("alisa");

        GroupFactory groupFactory = new GroupFactory();
        Group group = groupFactory.create("Ingrid's Group", "1234", GroupType.STUDY);

        MembershipFactory membershipFactory = new MembershipFactory();
        Membership membership1 = membershipFactory.create("ingrid", "1234", UserRole.MODERATOR, true);
        Membership membership2 = membershipFactory.create("alisa", "1234", UserRole.MEMBER, true);
        
        // Add memberships to the group object
        group.addMembership(membership1);
        group.addMembership(membership2);
        
        groupSetup.save(group);
        membershipSetup.save(membership1);
        membershipSetup.save(membership2);

        // Expected output: masterSchedule counts both users' availability
        int[][] expectedSchedule = new int[12][7];
        expectedSchedule[0][0] = 2;  
        expectedSchedule[1][0] = 2;
        expectedSchedule[2][0] = 1;
        expectedSchedule[0][1] = 1;
        expectedSchedule[1][1] = 1;
        expectedSchedule[2][1] = 1;
        expectedSchedule[0][2] = 1;
        expectedSchedule[1][2] = 1;
        expectedSchedule[0][4] = 1;
        expectedSchedule[1][4] = 1;

        CreateScheduleOutputBoundary successPresenter = new CreateScheduleOutputBoundary() {
            @Override
            public void prepareSuccessView(CreateScheduleOutputData outputData) {
                int[][] actualSchedule = outputData.getMasterSchedule();
                assertEquals(2, outputData.getGroupSize());
                
                // Check all the time slots match
                for (int i = 0; i < 12; i++) {
                    for (int j = 0; j < 7; j++) {
                        assertEquals(expectedSchedule[i][j], actualSchedule[i][j],
                            "Mismatch at hour " + i + ", day " + j);
                    }
                }
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure is unexpected");
            }

            @Override
            public void openCreateScheduleModal() {
                // For UI purposes only
            }
        };

        // Create ViewModel and Presenter for UI updates
        ScheduleTabViewModel scheduleTabViewModel = new ScheduleTabViewModel();
        ScheduleTabPresenter groupSchedulePresenter = new ScheduleTabPresenter(scheduleTabViewModel);

        CreateScheduleInputBoundary interactor = new CreateScheduleInteractor(
            userRepository,
            groupRepository,
            successPresenter,
            membershipRepository,
            groupSchedulePresenter
        );

        interactor.execute(inputData);
    }

    @Test
    void testLoadSchedule() {
        // Set up a group that already has a master schedule saved
        InMemoryUserDataAccessObject userDataAccess = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDataAccess = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDataAccess = new InMemoryMembershipDataAccessObject();

        CreateGroupDataAccessInterface groupSetup = groupDataAccess;
        CreateScheduleUserDataAccessInterface userRepository = userDataAccess;
        CreateScheduleGroupDataAccessInterface groupRepository = groupDataAccess;
        CreateScheduleMembershipDataAccessInterface membershipRepository = membershipDataAccess;

        // Create a group with an existing master schedule
        GroupFactory groupFactory = new GroupFactory();
        Group group = groupFactory.create("Test Group", "5678", GroupType.STUDY);
        
        // Set up a pre-existing master schedule
        int[][] existingSchedule = new int[12][7];
        existingSchedule[0][0] = 2;
        existingSchedule[1][0] = 2;
        existingSchedule[0][1] = 1;
        existingSchedule[2][2] = 3;
        
        group.setMasterSchedule(existingSchedule);
        
        // Add some memberships so group size is correct
        MembershipFactory membershipFactory = new MembershipFactory();
        Membership m1 = membershipFactory.create("user1", "5678", UserRole.MODERATOR, true);
        Membership m2 = membershipFactory.create("user2", "5678", UserRole.MEMBER, true);
        Membership m3 = membershipFactory.create("user3", "5678", UserRole.MEMBER, true);
        
        group.addMembership(m1);
        group.addMembership(m2);
        group.addMembership(m3);
        
        groupSetup.save(group);

        // Create a ScheduleTabPresenter that will receive the loaded schedule
        ScheduleTabViewModel scheduleTabViewModel = new ScheduleTabViewModel();
        ScheduleTabPresenter groupSchedulePresenter = new ScheduleTabPresenter(scheduleTabViewModel) {
            @Override
            public void prepareSuccessView(CreateScheduleOutputData outputData) {
                // Verify the loaded schedule matches the existing one
                int[][] loadedSchedule = outputData.getMasterSchedule();
                assertEquals(3, outputData.getGroupSize());
                
                // Check that the loaded schedule matches what we saved
                assertEquals(2, loadedSchedule[0][0], "Slot (0, 0) should have 2 people");
                assertEquals(2, loadedSchedule[1][0], "Slot (1, 0) should have 2 people");
                assertEquals(1, loadedSchedule[0][1], "Slot (0, 1) should have 1 person");
                assertEquals(3, loadedSchedule[2][2], "Slot (2, 2) should have 3 people");
                
                // Verify other slots are 0
                assertEquals(0, loadedSchedule[3][3], "Random slot should be 0");
                assertEquals(0, loadedSchedule[5][5], "Random slot should be 0");
            }
        };

        // Create a dummy CreateSchedulePresenter (not used for loadSchedule)
        CreateScheduleOutputBoundary createSchedulePresenter = new CreateScheduleOutputBoundary() {
            @Override
            public void prepareSuccessView(CreateScheduleOutputData outputData) {
                fail("This presenter should not be called during loadSchedule");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure is unexpected");
            }

            @Override
            public void openCreateScheduleModal() {
                // Not used
            }
        };

        CreateScheduleInputBoundary interactor = new CreateScheduleInteractor(
            userRepository,
            groupRepository,
            createSchedulePresenter,
            membershipRepository,
            groupSchedulePresenter
        );

        // Call loadSchedule - this should retrieve and present the existing schedule
        interactor.loadSchedule("5678");
    }

    @Test
    void testOpenCreateScheduleModal() {
        // Create shared in-memory data store
        InMemoryUserDataAccessObject userDataAccess = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDataAccess = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDataAccess = new InMemoryMembershipDataAccessObject();

        CreateScheduleUserDataAccessInterface userRepository = userDataAccess;
        CreateScheduleGroupDataAccessInterface groupRepository = groupDataAccess;
        CreateScheduleMembershipDataAccessInterface membershipRepository = membershipDataAccess;

        // Track if presenter method was called
        final boolean[] modalOpened = {false};

        CreateScheduleOutputBoundary presenter = new CreateScheduleOutputBoundary() {
            @Override
            public void prepareSuccessView(CreateScheduleOutputData outputData) {
            }

            @Override
            public void prepareFailView(String error) {
            }

            @Override
            public void openCreateScheduleModal() {
                modalOpened[0] = true;
            }
        };

        // Create ViewModel and Presenter for UI updates
        ScheduleTabViewModel scheduleTabViewModel = new ScheduleTabViewModel();
        ScheduleTabPresenter groupSchedulePresenter = new ScheduleTabPresenter(scheduleTabViewModel);

        CreateScheduleInteractor interactor = new CreateScheduleInteractor(
            userRepository,
            groupRepository,
            presenter,
            membershipRepository,
            groupSchedulePresenter
        );

        // Call openCreateScheduleModal
        interactor.openCreateScheduleModal();

        // Verify presenter method was called
        assertEquals(true, modalOpened[0]);
    }
}
