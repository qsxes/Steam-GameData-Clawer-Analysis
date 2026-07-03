package translate_enum;

/**
 * @author: 向珅葳
 * @date : 2026/6/29
 * @time : 16:19
 * @project_name : SteamSQL
 */
public enum Translate {
    OVERWHELMINGLY_POSITIVE("好评如潮"),
    VERY_POSITIVE("特别好评"),
    MOSTLY_POSITIVE("多半好评"),
    MIXED("褒贬不一"),
    MOSTLY_NEGATIVE("多半差评"),
    NEGATIVE("差评"),
    OVERWHELMINGLY_NEGATIVE("差评如潮"),
    POSITIVE("好评"),
    NO_USER_REVIEWS("无用户评测"),
    REVIEWS_SHORT("评价不足"),
    UNKNOWN("未知");

    private final String chinese;

    Translate(String chinese) {
        this.chinese = chinese;
    }

    public String getChinese() {
        return chinese;
    }

}
