import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Sub2Demo {
    private static final String PROJECT_ID = "";

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {

        String projectId = "backup-server-003";
        String subscriptionId = "lolo";

        CredentialsProvider credentialsProvider =
                FixedCredentialsProvider.create(
                        ServiceAccountCredentials.fromStream(new FileInputStream("key.json")));

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);
// Instantiate an asynchronous message receiver
        MessageReceiver receiver =
                new MessageReceiver() {
                    @Override
                    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
                        System.out.println("Hello messaging....");
                        // handle incoming message, then ack/nack the received message
                        System.out.println("Id : " + message.getMessageId());
                        System.out.println("Data : " + message.getData().toStringUtf8());
                        consumer.ack();
                    }
                };

        Subscriber subscriber = null;
        try {
            // Create a subscriber for "my-subscription-id" bound to the message receiver
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).setCredentialsProvider(credentialsProvider).build();
            subscriber.startAsync();
            // ...
        }catch (Exception ex){
            ex.printStackTrace();
        }
//        finally {
//            // stop receiving messages
//            if (subscriber != null) {
//                subscriber.stopAsync();
//            }
//        }

    }
}
