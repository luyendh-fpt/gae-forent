package com.higae.controller;

import com.googlecode.objectify.ObjectifyService;
import com.higae.entity.Article;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class HelloController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectifyService.register(Article.class);
        Article article = new Article();
        article.setId(Calendar.getInstance().getTimeInMillis());
        article.setTitle("Bao moi");
        article.setContent("Bao moi hom nay.");
        ofy().save().entity(article).now();
        resp.getWriter().print("Hello World");
    }
}
