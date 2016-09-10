package tuannt.bizlive.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tranhoa on 4/4/2016.

 */
public class PostSeria implements Serializable {
    private ArrayList<Posts> data;

    public PostSeria(ArrayList<Posts> data) {
        this.data = data;
    }

    public ArrayList<Posts> getData() {
        return data;
    }

    public void setData(ArrayList<Posts> data) {
        this.data = data;
    }
}
