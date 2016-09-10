package tuannt.bizlive.model;

import java.io.Serializable;






/**
 * Created by hnc on 10/19/2015.
 */
public class Comment implements Serializable {
    private String comment_id;
    private String content;
    private String user_name;
    private String date;
    private String avatar;
    private String num_like;
    Boolean commented;
    Boolean liked;

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setCommented(Boolean commented) {
        this.commented = commented;
    }

    public Boolean getCommented() {
        return commented;

    }

    public String getComment_id() {
        return comment_id;
    }

    public String getContent() {
        return content;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getDate() {
        return date;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNum_like() {
        return num_like;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setNum_like(String num_like) {
        this.num_like = num_like;
    }
}
