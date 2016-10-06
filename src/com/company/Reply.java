package com.company;

import java.util.ArrayList;

/**
 * Created by michaelplott on 10/6/16.
 */
public class Reply {
    String reply;
    String repAuthor;

    public Reply() {
    }

    public Reply(String reply, String repAuthor) {
        this.reply = reply;
        this.repAuthor = repAuthor;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getRepAuthor() {
        return repAuthor;
    }

    public void setRepAuthor(String repAuthor) {
        this.repAuthor = repAuthor;
    }

    @Override
    public String toString() {
        return reply + " " + repAuthor;
    }
}
