import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
public class SmsService {

    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";
    private static final String FROM_NUMBER = ""; 

    static {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        } catch (Exception e) {
            System.out.println("Error initializing Twilio: " + e.getMessage());
        }
    }

    public void sendSms(String to, String body) {
        try {
            Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(FROM_NUMBER),
                body
            ).create();
            
            System.out.println("SMS sent successfully! SID: " + message.getSid());
            
        } catch (Exception e) {
            System.out.println("Error sending SMS to " + to + ": " + e.getMessage());
        }
    }
}