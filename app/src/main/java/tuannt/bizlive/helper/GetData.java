package tuannt.bizlive.helper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tuannt.bizlive.model.Comment;
import tuannt.bizlive.model.DetailPost;
import tuannt.bizlive.model.InfoApp;
import tuannt.bizlive.model.Post;
import tuannt.bizlive.model.Posts;
import tuannt.bizlive.model.Related;
import tuannt.bizlive.model.Zone;

/**
 * Created by hnc on 10/19/2015.
 */
public class GetData {
    public static ArrayList<Comment> getListComment(String hightLighJson) {
        ArrayList<Comment> listComment = new ArrayList<Comment>();
        if (hightLighJson != null) {
            try {
                JSONObject data = new JSONObject(hightLighJson);
                JSONArray info = data.getJSONArray("info");
                int leng = info.length();
                for (int i = 0; i < leng; i++) {

                    Comment comment = new Comment();
                    JSONObject jsonComment = info.getJSONObject(i);
                    if (!jsonComment.isNull("comment_id")) {
                        comment.setComment_id(jsonComment.getString("comment_id"));

                    }

                    if (!jsonComment.isNull("content")) {
                        comment.setContent(jsonComment.getString("content"));
                    }
                    if (!jsonComment.isNull("num_like")) {
                        comment.setNum_like(jsonComment.getString("num_like"));
                    }

                    if (!jsonComment.isNull("date")) {

                        comment.setDate(jsonComment.getString("date"));
                    }
                    if (!jsonComment.isNull("user_name")) {

                        comment.setUser_name(jsonComment.getString("user_name"));
                    }
                    if (!jsonComment.isNull("avatar")) {

                        comment.setAvatar(jsonComment.getString("avatar"));
                    }
                    if (!jsonComment.isNull("liked")) {
                        comment.setLiked(Boolean.valueOf(jsonComment.getString("liked")));
                    }

                    listComment.add(comment);

                }


            } catch (JSONException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return listComment;
    }

    public static String getFollow(String json) {
        String follow = "";
        if (json != null) {
            try {
                JSONObject data = new JSONObject(json);
                if (!data.isNull("follow")) {
                    follow = data.getString("follow");
                }
            } catch (JSONException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return follow;
    }

    public static ArrayList<Related> getListRelated(String hightLighJson) {
        ArrayList<Related> listRelated = new ArrayList<Related>();
        if (hightLighJson != null) {
            try {
                JSONObject data = new JSONObject(hightLighJson);
                JSONArray info = data.getJSONArray("info");
                int leng = info.length();
                for (int i = 0; i < leng; i++) {
                    String avatar = "";
                    String avatardescription = "";
                    String description = "";
                    String post_id = "";
                    String title = "";

                    JSONObject jsonComment = info.getJSONObject(i);
                    if (!jsonComment.isNull("avatar")) {
                        avatar = jsonComment.getString("avatar");

                    }


                    if (!jsonComment.isNull("avatardescription")) {
                        avatardescription = jsonComment.getString("avatardescription");
                    }

                    if (!jsonComment.isNull("description")) {

                        description = jsonComment.getString("description");
                    }
                    if (!jsonComment.isNull("post_id")) {
                        post_id = jsonComment.getString("post_id");
                    }
                    if (!jsonComment.isNull("title")) {

                        title = jsonComment.getString("title");
                    }
                    listRelated.add(new Related(avatar, avatardescription, description, post_id, title));

                }


            } catch (JSONException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return listRelated;
    }

    public static ArrayList<Post> getListPost(String jsonArrayString) {
        ArrayList<Post> listPost = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            int leng = jsonArray.length();
            for (int i = 0; i < leng; i++) {
                JSONObject postJson = jsonArray.getJSONObject(i);
                Post post = new Post();
                if (!postJson.isNull("avatardescription")) {
                    post.setAvatardescription(postJson.getString("avatardescription"));

                }
                if (!postJson.isNull("description")) {
                    post.setDescription(postJson.getString("description"));

                }
                if (!postJson.isNull("post_id")) {
                    post.setPost_id(postJson.getString("post_id"));

                }
                if (!postJson.isNull("link")) {
                    post.setLink(postJson.getString("link"));

                }
                if (!postJson.isNull("category")) {
                    post.setCategory(postJson.getString("category"));

                }
                if (!postJson.isNull("avatar")) {
                    post.setAvatar(postJson.getString("avatar"));

                }
                if (!postJson.isNull("content")) {
                    post.setContent(postJson.getString("content"));

                }
                if (!postJson.isNull("date")) {
                    post.setDate(postJson.getString("date"));

                }
                if (!postJson.isNull("author")) {
                    post.setAuthor(postJson.getString("author"));

                }
                if (!postJson.isNull("title")) {
                    post.setTitle(postJson.getString("title"));

                }
                listPost.add(post);

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return listPost;
    }

    public static ArrayList<Posts> getCategory(final String json) {
        ArrayList<Posts> arrData = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(json);
            JSONArray info = data.getJSONArray("data");
            int leng = info.length();
            Log.d("Lengt", "" + leng);
            for (int i = 0; i < leng; i++) {
                Posts obJect = new Posts();
                JSONObject jsonSugget = info.getJSONObject(i);
                if (!jsonSugget.isNull("post_id")) {
                    obJect.setPost_id(jsonSugget.getString("post_id"));
                }
                if (!jsonSugget.isNull("title")) {
                    obJect.setTitle(jsonSugget.getString("title"));
                }
                if (!jsonSugget.isNull("avatar")) {
                    obJect.setAvatar(jsonSugget.getString("avatar"));
                }
                if (!jsonSugget.isNull("avatar_medium")) {
                    obJect.setAvatar_medium(jsonSugget.getString("avatar_medium"));
                }
                if (!jsonSugget.isNull("avatardescription")) {
                    obJect.setAvatardescription(jsonSugget.getString("avatardescription"));
                }
                if (!jsonSugget.isNull("description")) {
                    obJect.setDescription(jsonSugget.getString("description"));
                }
                if (!jsonSugget.isNull("author")) {
                    obJect.setAuthor(jsonSugget.getString("author"));
                }
                if (!jsonSugget.isNull("link")) {
                    obJect.setLink(jsonSugget.getString("link"));
                }
                if (!jsonSugget.isNull("published_time")) {
                    obJect.setPublished_time(jsonSugget.getString("published_time"));
                }
                if (!jsonSugget.isNull("category_name")) {
                    obJect.setCategory_name(jsonSugget.getString("category_name"));
                }
                if (!jsonSugget.isNull("level")) {
                    obJect.setLevel(jsonSugget.getString("level"));
                }
                if (!jsonSugget.isNull("num_like")) {
                    obJect.setNum_like(jsonSugget.getString("num_like"));
                }
                if (!jsonSugget.isNull("num_view")) {
                    obJect.setNum_view(jsonSugget.getString("num_view"));
                }
                if (!jsonSugget.isNull("link_speech_from_title_des")) {
                    obJect.setLink_speech_from_title_des(jsonSugget.getString("link_speech_from_title_des"));
                } else {
                    obJect.setLink_speech_from_title_des("");
                }
                if (!jsonSugget.isNull("link_speech_from_text")) {
                    obJect.setLink_speech_from_text(jsonSugget.getString("link_speech_from_text"));
                } else {
                    obJect.setLink_speech_from_text("");
                }

                arrData.add(obJect);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrData;
    }

    public static DetailPost getDetailPost(final String json) {
        DetailPost obJect = new DetailPost();
        try {
            JSONObject data = new JSONObject(json);
            JSONObject jsonSugget = data.getJSONObject("data");
            if (!jsonSugget.isNull("post_id")) {
                obJect.setPost_id(jsonSugget.getString("post_id"));
            }
            if (!jsonSugget.isNull("title")) {
                obJect.setTitle(jsonSugget.getString("title"));
            }
            if (!jsonSugget.isNull("avatar")) {
                obJect.setAvatar(jsonSugget.getString("avatar"));
            }
            if (!jsonSugget.isNull("avatardescription")) {
                obJect.setAvatardescription(jsonSugget.getString("avatardescription"));
            }
            if (!jsonSugget.isNull("description")) {
                obJect.setDescription(jsonSugget.getString("description"));
            }
            if (!jsonSugget.isNull("link")) {
                obJect.setLink(jsonSugget.getString("link"));
            }
            if (!jsonSugget.isNull("author")) {
                obJect.setAuthor(jsonSugget.getString("author"));
            }
            if (!jsonSugget.isNull("published_time")) {
                obJect.setPublished_time(jsonSugget.getString("published_time"));
            }
            if (!jsonSugget.isNull("category")) {
                obJect.setCategory(jsonSugget.getString("category"));
            }
            if (!jsonSugget.isNull("category_name")) {
                obJect.setCategory_name(jsonSugget.getString("category_name"));
            }
            if (!jsonSugget.isNull("level")) {
                obJect.setLevel(jsonSugget.getString("level"));
            }
            if (!jsonSugget.isNull("content")) {
                obJect.setContent(jsonSugget.getString("content"));
            }
            if (!jsonSugget.isNull("num_like")) {
                obJect.setNum_like(jsonSugget.getString("num_like"));
            }
            if (!jsonSugget.isNull("num_view")) {
                obJect.setNum_view(jsonSugget.getString("num_view"));
            }
            if (!jsonSugget.isNull("tag")) {
                obJect.setTag(jsonSugget.getString("tag"));
            }
            if (!jsonSugget.isNull("link_speech_from_text")) {
                obJect.setLink_speech_from_text(jsonSugget.getString("link_speech_from_text"));
            } else {
                obJect.setLink_speech_from_text("");
            }
            if (!jsonSugget.isNull("link_speech_from_text")) {
                obJect.setLink_speech_from_title_des(jsonSugget.getString("link_speech_from_title_des"));
            } else {
                obJect.setLink_speech_from_title_des("");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obJect;
    }

    public static ArrayList<Zone> getGridCategory(final String json) {
        ArrayList<Zone> arrData = new ArrayList<>();
        try {
            JSONObject data = new JSONObject(json);
            JSONArray info = data.getJSONArray("data");
            int leng = info.length();
            Log.d("Lengt", "" + leng);
            for (int i = 0; i < leng; i++) {
                Zone obJect = new Zone();
                JSONObject jsonSugget = info.getJSONObject(i);
                if (!jsonSugget.isNull("id")) {
                    obJect.setZone_id(jsonSugget.getString("id"));
                }
                if (!jsonSugget.isNull("name")) {
                    obJect.setZone_name(jsonSugget.getString("name"));
                }
                if (!jsonSugget.isNull("avatar")) {
                    obJect.setImg(jsonSugget.getString("avatar"));
                }
                arrData.add(obJect);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrData;
    }

    public static InfoApp getInfoApp(final String json) {
        InfoApp obJect = new InfoApp();
        try {
            JSONObject data = new JSONObject(json);
            JSONObject jsonSugget = data.getJSONObject("data");




            if (!jsonSugget.isNull("app_name")) {
                obJect.setApp_name(jsonSugget.getString("app_name"));
            } else {
                obJect.setApp_name("");
            }
            if (!jsonSugget.isNull("web")) {
                obJect.setWeb(jsonSugget.getString("web"));
            } else {
                obJect.setWeb("");
            }
            if (!jsonSugget.isNull("icon_app")) {
                obJect.setIcon_app(jsonSugget.getString("icon_app"));
            } else {
                obJect.setIcon_app("");
            }
            if (!jsonSugget.isNull("vertical_poster")) {
                obJect.setVertical_poster(jsonSugget.getString("vertical_poster"));
            } else {
                obJect.setVertical_poster("");
            }

            if (!jsonSugget.isNull("horizontal_poster")) {
                obJect.setHorizontal_poster(jsonSugget.getString("horizontal_poster"));
            } else {
                obJect.setHorizontal_poster("");
            }
            if (!jsonSugget.isNull("link_share_app")) {
                obJect.setLink_share_app(jsonSugget.getString("link_share_app"));
            } else {
                obJect.setLink_share_app("");
            }
            if (!jsonSugget.isNull("policy")) {
                obJect.setPolicy(jsonSugget.getString("policy"));
            } else {
                obJect.setPolicy("");
            }
            if (!jsonSugget.isNull("description")) {
                obJect.setDescription(jsonSugget.getString("description"));
            } else {
                obJect.setDescription("");
            }
            if (!jsonSugget.isNull("s_android")) {
                obJect.setS_android(jsonSugget.getString("s_android"));
            } else {
                obJect.setHorizontal_poster("");
            }
            if (!jsonSugget.isNull("s_ios")) {
                obJect.setS_ios(jsonSugget.getString("s_ios"));
            } else {
                obJect.setS_ios("");
            }

            if (!jsonSugget.isNull("s_wp")) {
                obJect.setS_wp(jsonSugget.getString("s_wp"));
            } else {
                obJect.setS_wp("");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obJect;
    }
}
