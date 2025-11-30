package use_case.manage_members.respond_request;

public interface RespondRequestInputBoundary {
    /**
     * Execute the Respond Request Use Case.
     *
     * @param respondRequestInputData the input data for this use case
     */
    void execute(RespondRequestInputData respondRequestInputData);

}
