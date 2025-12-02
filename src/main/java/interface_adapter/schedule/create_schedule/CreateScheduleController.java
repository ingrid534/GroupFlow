package interface_adapter.schedule.create_schedule;

import use_case.create_schedule.CreateScheduleInputBoundary;
import use_case.create_schedule.CreateScheduleInputData;

public class CreateScheduleController {
    private final CreateScheduleInputBoundary createScheduleInteractor;

    public CreateScheduleController(CreateScheduleInputBoundary createScheduleInteractor) {
        this.createScheduleInteractor = createScheduleInteractor;
    }

    /**
     * Executes the Create Schedule use case.
     * @param availabilityGrid the grid of the user's available time slots.
     */
    public void execute(boolean[][] availabilityGrid) {
        final CreateScheduleInputData createScheduleInputData = 
            new CreateScheduleInputData(availabilityGrid);

        createScheduleInteractor.execute(createScheduleInputData);
    }

    /**
     * Open Create Schedule modal.
     * Triggers the interactor to open the Create Schedule modal.
     */
    public void openScheduleModal() {
        createScheduleInteractor.openCreateScheduleModal();
    }
    
}
