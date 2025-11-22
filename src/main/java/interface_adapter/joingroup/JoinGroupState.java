package interface_adapter.joingroup;

public class JoinGroupState {

    private String groupCode = "";
    private String groupCodeError;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupCodeError() {
        return groupCodeError;
    }

    public void setGroupCodeError(String groupCodeError) {
        this.groupCodeError = groupCodeError;
    }
}
