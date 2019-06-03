package com.higae.cronjob;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.googlecode.objectify.ObjectifyService;
import com.higae.config.HiGAEConstant;
import com.higae.entity.Article;
import com.higae.entity.CrawlerSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class CrawlerController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CrawlerController.class.getName());

    static {
        ObjectifyService.register(Article.class);
        ObjectifyService.register(CrawlerSource.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.severe("Crawler job. Started at: " + Calendar.getInstance().getTime());
        try {
            List<CrawlerSource> listSouce = ofy().load().type(CrawlerSource.class).list();
            for (CrawlerSource source :
                    listSouce) {
                Document document = Jsoup.connect(source.getUrl()).get();
                Elements els = document.select(source.getLinkSelector());
                HashSet<String> uniqueLinks = new HashSet<>();
                for (Element el :
                        els) {
                    uniqueLinks.add(el.attr("href"));
                }
                for (String link :
                        uniqueLinks) {
                    Article article = new Article(link, Article.Status.PENDING);
                    article.setSource(source.getUrl());
                    ofy().save().entity(article).now();
                    publicLink(link);
                }
            }

        } catch (Exception ex) {
            LOGGER.severe(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void publicLink(String link) {
        try {
            ProjectTopicName topicName = ProjectTopicName.of(
                    HiGAEConstant.GAE_PROJECT_ID,
                    HiGAEConstant.GAE_PUBSUB_CRAWLER_TOPIC);
            Publisher publisher = null;
            List<ApiFuture<String>> futures = new ArrayList<>();
            try {
                CredentialsProvider credentialsProvider =
                        FixedCredentialsProvider.create(
                                ServiceAccountCredentials.fromStream(new FileInputStream(HiGAEConstant.GAE_PUBSUB_KEY_PATH)));
                // Create a publisher instance with default settings bound to the topic
                publisher = Publisher.newBuilder(topicName).setCredentialsProvider(credentialsProvider).build();
                ByteString data = ByteString.copyFromUtf8(link);
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                        .setData(data)
                        .build();
                // Schedule a message to be published. Messages are automatically batched.
                ApiFuture<String> future = publisher.publish(pubsubMessage);
                futures.add(future);
            } catch (Exception ex) {
                LOGGER.severe(ex.getMessage());
                ex.printStackTrace();
            } finally {
                List<String> messageIds = ApiFutures.allAsList(futures).get();

                for (String messageId : messageIds) {
                    System.out.println(messageId);
                }

                if (publisher != null) {
                    // When finished with the publisher, shutdown to free up resources.
                    publisher.shutdown();
                }
            }
        } catch (Exception ex) {
            LOGGER.severe(ex.getMessage());
        }
    }
}
