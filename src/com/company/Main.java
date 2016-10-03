package com.company;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;


public class Main {

    static User user;
    static ArrayList<Message> messages = new ArrayList<>();

    public static void main(String[] args) {
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap m = new HashMap();
                    if (user != null) {
                        m.put("name", user.name);
                    }
                    m.put("messages", messages);
                   return new ModelAndView(m, "index.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/login",
                (request, response) -> {
                    return new ModelAndView(null, "login.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("uName");
                    String pass = request.queryParams("pWord");
                    user = new User(name, pass);
                    System.out.println(user.name);
                    System.out.println(user.password);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    user = null;
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/create-message",
                (request, response) -> {
                    String theMessage = request.queryParams("uMessage");
                    Message message = new Message(theMessage);
                    messages.add(message);
                    response.redirect("/");
                    return null;
                }
        );
    }
}
