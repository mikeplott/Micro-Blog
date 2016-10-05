package com.company;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;



public class Main {

    public static void main(String[] args) throws IOException {

        HashMap<String, User> users = jsonReader().getUserWrapper();
        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("uName");
                    User user = users.get(username);
                    HashMap m = new HashMap();
                    if (user != null) {
                        m.put("name", user.name);
                        m.put("messages", user.messages);
                    }

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
                    if (pass.isEmpty()) {
                        response.redirect("/");
                        return null;
                    }

                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name, pass, new ArrayList<Message>());
                        users.put(name, user);
                        jsonWriter(users);
                    } else if (!pass.equals(user.password)) {
                        response.redirect("/");
                        return null;
                    }

                    Session session = request.session();
                    session.attribute("uName", user.name);
                    System.out.println(user.name);
                    System.out.println(user.password);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    jsonWriter(users);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/create-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("uName");
                    User user = users.get(username);
                    String theMessage = request.queryParams("uMessage");
                    Message message = new Message(theMessage);
                    user.messages.add(message);
                    users.put(username, user);
                    jsonWriter(users);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/delete-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("uName");
                    User user = users.get(username);
                    String text = request.queryParams("deleteUM");
                    int index = Integer.parseInt(text) - 1;
                    if (index < 0) {
                        response.redirect("/");
                    }
                    user.messages.remove(index);
                    users.put(username, user);
                    jsonWriter(users);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/edit-message",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("uName");
                    User user = users.get(username);
                    String text = request.queryParams("messageEdit");
                    String number = request.queryParams("theIndex");
                    int index = Integer.parseInt(number) - 1;
                    if (index < 0) {
                        response.redirect("/");
                    }
                    Message m = user.messages.get(index);
                    m.message = text;
                    users.put(username, user);
                    jsonWriter(users);
                    response.redirect("/");
                    return null;
                }
        );
    }
    public static void jsonWriter(HashMap users) throws IOException {
        File file = new File("users.json");
        JsonSerializer serializer = new JsonSerializer();
        MapWrapper mw = new MapWrapper();
        mw.userWrapper = users;
        String json = serializer.deep(true).serialize(mw);
        FileWriter jsonWriter = new FileWriter(file);
        jsonWriter.write(json);
        jsonWriter.close();
    }

    public static MapWrapper jsonReader() throws IOException {
        File file = new File("users.json");
        FileReader fr = new FileReader(file);
        int fileSize = (int) file.length();
        char[] contents = new char[fileSize];
        fr.read(contents, 0, fileSize);
        JsonParser parser = new JsonParser();
        return parser.parse(contents, MapWrapper.class);
    }
}
