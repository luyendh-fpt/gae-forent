package queue;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetQueue {

    public static void main(String[] args) {
        Queue q = QueueFactory.getQueue("my-queue-name");
        List<TaskHandle> tasks = q.leaseTasks(2, TimeUnit.SECONDS, 1);
        
    }
}
