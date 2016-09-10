package tuannt.bizlive.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tuan on 10/9/2015.
 */
public class JSONArrayData implements Serializable{

//    private static final long serialVersionUID = -7060210544600464481L;

    ArrayList<ArrayList<Post>> data = new ArrayList<ArrayList<Post>>();

    ArrayList<ArrayList<Zone>> zoneList = new ArrayList<ArrayList<Zone>>();

    public ArrayList<ArrayList<Zone>> getZoneList() {
        return zoneList;
    }

    public void setZoneList(ArrayList<ArrayList<Zone>> zoneList) {
        this.zoneList = zoneList;
    }

    public ArrayList<ArrayList<Post>> getData() {
        return data;
    }

    public void setData(ArrayList<ArrayList<Post>> data) {
        this.data = data;
    }
}
