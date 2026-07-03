package entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: 向珅葳
 * @date : 2026/6/26
 * @time : 8:18
 * @project_name : SteamSQL
 */
public class GameShop {
    private int appId;                 //主键
    private String name;               //名字
    private String developer;          //开发商
    private String publisher;          //发行商
    private BigDecimal price;          //价格
    private String releaseDate;        //发布日期
    private List<Tag> genres;       //类型数组
    private long positiveReviews;   //好评
    private long negativeReviews;   //恶评
    private long totalReviews;  //总评论数
    private String review_score_desc;   //评价对应描述
    private String type; //game/dlc
    private String tags;
    private Double positiveRatePercent; //百分比好评率

    public Double getPositiveRatePercent() {
        return positiveRatePercent;
    }

    public void setPositiveRatePercent(Double positiveRatePercent) {
        this.positiveRatePercent = positiveRatePercent;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getPositiveReviews() {
        return positiveReviews;
    }

    public void setPositiveReviews(long positiveReviews) {
        this.positiveReviews = positiveReviews;
    }

    public long getNegativeReviews() {
        return negativeReviews;
    }

    public void setNegativeReviews(long negativeReviews) {
        this.negativeReviews = negativeReviews;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public String getReview_score_desc() {
        return review_score_desc;
    }

    public void setReview_score_desc(String review_score_desc) {
        this.review_score_desc = review_score_desc;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }


    public List<Tag> getGenres() {
        return genres;
    }

    public void setGenres(List<Tag> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "GameShop{" +
                "appId=" + appId +
                ", name='" + name + '\'' +
                ", developer='" + developer + '\'' +
                ", publisher='" + publisher + '\'' +
                ", price=" + price +
                ", releaseDate='" + releaseDate + '\'' +
                ", genres=" + genres +
                '}';
    }
}
