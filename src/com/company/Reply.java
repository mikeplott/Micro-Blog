package com.company;

import java.util.ArrayList;

/**
 * Created by michaelplott on 10/6/16.
 */
public class Reply extends Message {

    public Reply() {
    }

    public Reply(String message, String author, boolean isPublic, ArrayList<Reply> replies) {
        super(message, author, isPublic, replies);
    }

}
