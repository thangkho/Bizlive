package tuannt.bizlive.model;

import java.io.Serializable;

/**
 * Created by tuan on 12/10/2015.
 */
public class Zone implements Serializable{
    private String zone_id;
    private String zone_name;
    private String img;

    public String getZone_id() {
        return zone_id;
    }

    public String getZone_name() {
        return zone_name;
    }

    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setZone_id(String zone_id) {

        this.zone_id = zone_id;
    }
}
