package com.higae.controller;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PullQueueController extends HttpServlet {

    private static Queue q = QueueFactory.getQueue("my-queue-queue");
    private static final Logger LOGGER = Logger.getLogger(PullQueueController.class.getSimpleName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<TaskHandle> tasks = q.leaseTasks(60, TimeUnit.SECONDS, 1);
        if(tasks.size() > 0){
            String articleId = new String(tasks.get(0).getPayload());
            LOGGER.info("Got task with article id: " + articleId);
            // gọi trong database article với id như trên.
            // gọi lên trang chủ lấy từ source để crawl dữ liêu.
            // lấy xong thì save lại vào database.
            q.deleteTask(tasks.get(0));
            LOGGER.info("Finish task with article id: " + articleId);
        }
    }
}
