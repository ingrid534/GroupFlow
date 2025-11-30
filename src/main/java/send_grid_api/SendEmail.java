package send_grid_api;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class to send an email using SendGrid API.
 */
public class SendEmail implements SendEmailInterface {
    private static String api_key;
    private static final String SENDGRID_URL = "https://api.sendgrid.com/v3/mail/send";
    private static final OkHttpClient CLIENT = new OkHttpClient();
    public static final String FROM_EMAIL = "groupflow.noreply@gmail.com";
    public static final String CONTENT_TYPE = "text/plain";

    /**
     * Initializes API key to key provided in api_key.txt.
     * @throws RuntimeException if key failed to load.
     */
    public SendEmail() {
        try {
            api_key = Files.readString(Path.of("config/api_key.txt")).trim();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load API key", ex);
        }
    }

    /**
     * Creates a Mail object for a task assigned notification.
     * @param recipient The email of the user that was assigned a task.
     * @return Mail object for email to send.
     */
    private Mail createTaskAssignedEmail(String recipient) {
        Email from = new Email(FROM_EMAIL);
        String subject = "New Task Available";
        Email to = new Email(recipient);
        Content content = new Content(CONTENT_TYPE, "You have been assigned a new task. Check your dashboard.");

        return new Mail(from, subject, to, content);
    }

    /**
     * Creates a Mail object for a group invite notification.
     * @param recipient The email of the user that was invited to a group.
     * @return Mail object for email to send.
     */
    private Mail createGroupInviteEmail(String recipient) {
        Email from = new Email(FROM_EMAIL);
        String subject = "New Group Invite";
        Email to = new Email(recipient);
        Content content = new Content(CONTENT_TYPE, "You have been invited to a new group. Check your dashboard.");

        return new Mail(from, subject, to, content);
    }

    /**
     * Choose the type of email to make based on the given email type. 
     * @param recipient The user to send email to.
     * @param type The type of email to make.
     * @return the Mail instance for the email to be sent.
     * @throws IllegalArgumentException if the given email type is undefined.
     */
    private Mail makeEmail(String recipient, EmailType type) {
        Mail mail;

        switch (type) {
            case NEW_TASK:
                mail = createTaskAssignedEmail(recipient);
                break;
            case GROUP_INVITE:
                mail = createGroupInviteEmail(recipient);
                break;
            default:
                throw new IllegalArgumentException("Unknown email type " + type);
        }
        return mail;
    }

    /**
     * Send email to recipient.
     * @param recipient User to receive the email.
     * @param type Type of email notification to send.
     * @return request to be processed.
     * @throws IOException in case of JSON marshall error.
     */
    public Request buildRequest(String recipient, EmailType type) throws IOException {
        Mail mail = makeEmail(recipient, type);

        RequestBody requestBodyJson = RequestBody.create(
            mail.build(),
            null
        );

        final Request request = new Request.Builder()
            .url(SENDGRID_URL)
            .addHeader("Authorization", "Bearer " + api_key)
            .addHeader("Content-Type", "application/json")
            .post(requestBodyJson)
            .build();
        
        return request;
    }
    
    /**
     * Sends email with SendGrid API.
     * @param recipient The user to send email to.
     * @param type The type of email notification to send.
     * @return Status code for client request.
     * @throws IOException in case of JSON marshall error.
     */
    public int sendEmail(String recipient, EmailType type) throws IOException {

        Request request = buildRequest(recipient, type);

        try {
            final Response response = CLIENT.newCall(request).execute();
            int status = response.code();

            if (status == 202) {
                System.out.println("Email should have sent. Check spam if not visible in inbox.");
            } else {
                System.err.println("Email failed to send " + status);
            }
            return status;

        } catch (IOException ex) {
            throw ex;
        }
    }

}
