package use_case.join_group;

public class JoinGroupInteractor implements JoinGroupInputBoundary {

    private final JoinGroupOutputBoundary presenter;
    private final JoinGroupUserDataAccessInterface dataAccess;

    public JoinGroupInteractor(JoinGroupOutputBoundary presenter, JoinGroupUserDataAccessInterface dataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    @Override
    public void execute(JoinGroupInputData inputData) {

        String code = inputData.getGroupCode();

        if (code == null || code.isBlank()) {
            presenter.prepareFailView("Group code cannot be empty.");
            return;
        }

        if (!dataAccess.groupCodeExists(code)) {
            presenter.prepareFailView("Invalid Group Code");
            return;
        }

        // TODO: Join Request sent should be added later
        presenter.prepareSuccessView(new JoinGroupOutputData(code));
    }
}