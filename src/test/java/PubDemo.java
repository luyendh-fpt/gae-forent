import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PubDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        System.out.println("Hello World");
        // topic id, eg. "my-topic"
        String projectId = "backup-server-003";
        String topicId = "messaging";

        ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);

        Publisher publisher = null;

        List<ApiFuture<String>> futures = new ArrayList<>();

        try {
            CredentialsProvider credentialsProvider =
                    FixedCredentialsProvider.create(
                            ServiceAccountCredentials.fromStream(new FileInputStream("key.json")));

            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).setCredentialsProvider(credentialsProvider).build();

            ByteString data = ByteString.copyFromUtf8("Hello World From Code 01");
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(data)
                    .build();

            // Schedule a message to be published. Messages are automatically batched.
            ApiFuture<String> future = publisher.publish(pubsubMessage);
            futures.add(future);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // Wait on any pending requests
            List<String> messageIds = ApiFutures.allAsList(futures).get();

            for (String messageId : messageIds) {
                System.out.println(messageId);
            }

            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
            }
        }
    }
}
