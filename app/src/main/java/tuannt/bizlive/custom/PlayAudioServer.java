package tuannt.bizlive.custom;

import java.util.ArrayList;

import tuannt.bizlive.model.Posts;

/**
 * Created by HoaTran on 7/20/2016.
 */
public interface PlayAudioServer {
    public void PlayAudioContent(ArrayList<Posts> arr,int position);
    public void PlayAudioDes(ArrayList<Posts> arr,int position);
}
