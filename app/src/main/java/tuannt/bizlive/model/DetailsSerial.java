package tuannt.bizlive.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tranhoa on 4/23/2016.
 */
public class DetailsSerial implements Serializable {
    ArrayList<DetailPost> data;

    public DetailsSerial(ArrayList<DetailPost> data) {
        this.data = data;
    }

    public ArrayList<DetailPost> getData() {
        return data;
    }

    public void setData(ArrayList<DetailPost> data) {
        this.data = data;
    }
}
