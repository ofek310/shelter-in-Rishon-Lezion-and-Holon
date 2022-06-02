package com.example.shelterholonandrishonlezion;

public class Comment {

    private String id;
    private String comment;
    private String publisher;
    public String publisher_id;

    public Comment() {

    }

    public Comment(String comment, String publisher,String publisher_id,String id) {
        this.id = id;
        this.comment = comment;
        this.publisher = publisher;
        this.publisher_id= publisher_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }
}

