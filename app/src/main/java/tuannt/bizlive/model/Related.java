package tuannt.bizlive.model;

/**
 * Created by Administrator on 11/24/2015.
 */
public class Related {
    String avatar;
    String avatardescription;
    String  description;
    String post_id;
    String title;

    public Related(String avatar, String post_id, String avatardescription, String description, String title) {
        this.avatar = avatar;
        this.post_id = post_id;
        this.avatardescription = avatardescription;
        this.description = description;
        this.title = title;
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

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatardescription() {
        return avatardescription;
    }

    public void setAvatardescription(String avatardescription) {
        this.avatardescription = avatardescription;
    }
}