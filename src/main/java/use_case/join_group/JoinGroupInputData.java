package use_case.join_group;

public class JoinGroupInputData {
    private final String groupCode;

    public JoinGroupInputData(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupCode() {
        return groupCode;
    }
}
