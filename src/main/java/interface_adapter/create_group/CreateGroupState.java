package interface_adapter.create_group;

import entity.group.GroupType;

/**
 * The state for the Create Group View Model.
 */
public class CreateGroupState {

    private String groupName = "";
    private GroupType groupType;
    private boolean openModal;
    private String error = "";
    private String successMessage = "";
    private boolean isSuccess;

    public String getGroupName() {
        return groupName;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public void setOpenModal(boolean modalState) {
        this.openModal = modalState;
    }

    public boolean getOpenModal() {
        return openModal;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return successMessage;
    }

    public void setMessage(String message) {
        this.successMessage = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
