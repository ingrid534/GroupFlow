package use_case.create_group;

public interface CreateGroupInputBoundary {

    void execute(CreateGroupInputData createGroupInputData);

    void openCreateGroupModal();

    void switchToDashboardView();
}
