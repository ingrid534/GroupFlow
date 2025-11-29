package send_grid_api;

import java.io.IOException;

public interface SendEmailInterface {
    
    enum EmailType {
        NEW_TASK,
        GROUP_INVITE
    }

    /**
     * Send the email of the given type to the given user.
     * @param recipient Email of the user to receive the email.
     * @param type Type of email to be sent to user.
     * @return The status code for the request.
     * @throws IOException in case of JSON Marshall error.
     */
    int sendEmail(String recipient, EmailType type) throws IOException;
}
