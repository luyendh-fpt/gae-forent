package com.higae.controller;

import com.google.gson.Gson;
import com.googlecode.objectify.ObjectifyService;
import com.higae.entity.CrawlerSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class CrawlerSourceController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CrawlerSourceController.class.getSimpleName());

    static {
        ObjectifyService.register(CrawlerSource.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("Hello Admin");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getParameter("url");
        String titleSelector = req.getParameter("titleSelector");
        String contentSelector = req.getParameter("contentSelector");
        String authorSelector = req.getParameter("authorSelector");
        // validate
        CrawlerSource crawlerSource = new CrawlerSource(url, CrawlerSource.Status.ACTIVE.getValue());
        crawlerSource.setTitleSelector(titleSelector);
        crawlerSource.setContentSelector(contentSelector);
        crawlerSource.setAuthorSelector(authorSelector);
        LOGGER.info(new Gson().toJson(crawlerSource));
        ofy().save().entity(crawlerSource).now();
        resp.getWriter().print("Okie");
    }
}
