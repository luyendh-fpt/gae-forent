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

public class MemberController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print("Hello Member");
    }
}
