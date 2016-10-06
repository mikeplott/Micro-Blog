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
        ArrayList<Message> publicM = new ArrayList<>();

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

        Spark.get(
                "/public",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("uName");
                    User user = users.get(name);
                    HashMap m = new HashMap();
                    if (user != null) {
                        m.put("name", user.name);
                        m.put("public", publicM);
                        for (int i = 0; i < publicM.size(); i++) {
                            m.put("pubRep", publicM.get(i).replies);
                        }
                        System.out.println(m);
                    }
                    return new ModelAndView(m, "public.html");
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
                        user = new User();
                        user.name = name;
                        user.password = pass;
                        user.messages = new ArrayList<>();
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
                    Message message = new Message(theMessage, username, false, new ArrayList<Reply>());
                    String pub = request.queryParams("uPublic");
                    if (pub != null) {
                        message.isPublic = true;
                        publicM.add(message);
                    }
                    for (int i = 0; i < publicM.size(); i++) {
                        Message test = publicM.get(i);
                        System.out.println(test.author + " " + test.message + " " + test.isPublic);
                    }
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
                    for (int i = 0; i < user.messages.size(); i++) {
                        if (user.messages.get(i).message.equals(text)) {
                            if (user.messages.get(i).isPublic = true) {
                                for (int j = 0; i < publicM.size(); j++) {
                                    if (publicM.get(j).message.equals(text)) {
                                        publicM.remove(i);
                                    }
                                }
                            }
                            user.messages.remove(i);
                        }
                    }
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
                    if (index < 0 || index > user.messages.size()) {
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

        Spark.post(
                "/reply",
                (request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("uName");
                    User user = users.get(username);
                    String theMes = request.queryParams("tarMes");
                    System.out.println(theMes);
                    String text = request.queryParams("mReply");
                    for (int i = 0; i < publicM.size(); i++) {
                        if (publicM.get(i).equals(theMes)) {
                            Message rep = publicM.get(i);
                            System.out.println(rep);
                            Reply theR = new Reply(text, user.name, true, new ArrayList<>());
                            System.out.println(theR.message + " " + theR.author);
                            rep.replies.add(theR);
                        }
                    }
                    response.redirect("/public");
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
