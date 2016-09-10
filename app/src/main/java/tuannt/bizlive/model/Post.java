package tuannt.bizlive.model;

import java.io.Serializable;

/**
 * Created by Tuan on 10/5/2015.
 */
public class Post implements Serializable{
    private String post_id;
    private String title;
    private String avatar;
    private String author;
    private String date;
    private String content;
    private String category;
    private String link;
    private String avatardescription;
    private String description;



    public String getAvatardescription() {
        return avatardescription;
    }

    public void setAvatardescription(String avatardescription) {
        this.avatardescription = avatardescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

