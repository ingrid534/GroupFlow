package send_grid_api;

import java.io.IOException;

public class InMemoryEmailer implements SendEmailInterface {

    /**
     * An InMemoryEmailer used to unit test emails through messages in the console.
     *
     * @param recipient Email of the user to receive the email.
     * @param type      Type of email to be sent to user.
     * @return The status code for the request.
     * @throws IOException in case of JSON Marshall error.
     */
    @Override
    public int sendEmail(String recipient, EmailType type) throws IOException {
        String message = String.format("To: %s \nA user has requested to join your group", recipient);
        System.out.println(message);

        return 202;
    }
}
