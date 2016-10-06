package com.company;

import java.util.ArrayList;

/**
 * Created by michaelplott on 10/3/16.
 */
public class Message {
    String message;
    String author;
    boolean isPublic = false;
    ArrayList<Reply> replies = new ArrayList<>();


    public Message() {
    }

    public Message(String message, String author, boolean isPublic, ArrayList<Reply> replies) {
        this.message = message;
        this.author = author;
        this.isPublic = isPublic;
        this.replies = replies;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public ArrayList<Reply> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<Reply> replies) {
        this.replies = replies;
    }
}
