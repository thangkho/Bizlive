package tuannt.bizlive.model;

import java.io.Serializable;

/**
 * Created by Tranhoa on 4/4/2016.
 */
public class DetailPost implements Serializable{
    String post_id;
    String title;
    String avatar;
    String avatardescription;
    String description;
    String link;
    String author;
    String published_time;
    String category;
    String category_name;
    String level;
    String content;
    String num_view;
    String num_like;
    String tag;
    String link_speech_from_text;
    String link_speech_from_title_des;

    public String getLink_speech_from_title_des() {
        return link_speech_from_title_des;
    }

    public void setLink_speech_from_title_des(String link_speech_from_title_des) {
        this.link_speech_from_title_des = link_speech_from_title_des;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublished_time() {
        return published_time;
    }

    public void setPublished_time(String published_time) {
        this.published_time = published_time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNum_view() {
        return num_view;
    }

    public void setNum_view(String num_view) {
        this.num_view = num_view;
    }

    public String getNum_like() {
        return num_like;
    }

    public void setNum_like(String num_like) {
        this.num_like = num_like;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLink_speech_from_text() {
        return link_speech_from_text;
    }

    public void setLink_speech_from_text(String link_speech_from_text) {
        this.link_speech_from_text = link_speech_from_text;
    }
}
