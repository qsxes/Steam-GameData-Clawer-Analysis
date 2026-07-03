package entity;

/**
 * @author: 向珅葳
 * @date : 2026/6/26
 * @time : 17:27
 * @project_name : SteamSQL
 */
public class Tag {
    private int tag_id;
    private String tag_name;

    public Tag(){

    };

    public Tag(String tag_name) {
        this.tag_name = tag_name;
    }

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }
}
