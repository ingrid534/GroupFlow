package use_case.join_group;

public interface JoinGroupUserDataAccessInterface {
    /**
     * Checks if the given groupCode exists.
     *
     * @param groupCode the groupCode to look for
     * @return true if a group with the given groupCode exists; false otherwise
     */
    boolean groupCodeExists(String groupCode);
}
