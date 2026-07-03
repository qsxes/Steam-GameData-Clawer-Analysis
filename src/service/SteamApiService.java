package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.GameShop;
import entity.Tag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.HttpUtil;

import java.io.File;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @author: 向珅葳
 * @date : 2026/6/26
 * @time : 10:31
 * @project_name : SteamSQL
 */
public class SteamApiService {
    private final Gson gson = new Gson();

    public GameShop getGameDetail(int appId) throws Exception{
        String url = "https://store.steampowered.com/api/appdetails?appids=" + appId + "&cc=cn&l=schinese";
        String json = HttpUtil.get(url);

        System.out.println("AppID " + appId + " 返回的 JSON 开头: " +
                (json.length() > 200 ? json.substring(0, 200) : json));

        //将爬取到的json字符串转为Json对象，root为根节点，appData为id对应的详细信息
        JsonObject root = gson.fromJson(json, JsonObject.class);
        JsonObject gameData = root.getAsJsonObject(String.valueOf(appId));

        if (gameData == null || !gameData.get("success").getAsBoolean()) {
            return null; // 游戏数据不存在
        }

        JsonObject data = gameData.getAsJsonObject("data");
        if (data == null) {
            return null;
        }

        GameShop game = new GameShop();
        game.setAppId(data.get("steam_appid").getAsInt());
        game.setName(data.get("name").getAsString());

        //开发商，检查developers键是否存在，developers的值是否为数组,只取第一个get(0)
        if (data.has("developers") && data.get("developers").isJsonArray()) {

            //循环设置开发者属性
            JsonArray devs = data.getAsJsonArray("developers");
            if (devs.size() > 0) {
                game.setDeveloper(devs.get(0).getAsString());
            }
        }

        //发行商，检查publishers键是否存在，publishers的值是否为数组
        if (data.has("publishers") && data.get("publishers").isJsonArray()) {
            JsonArray pubs = data.getAsJsonArray("publishers");
            if (pubs.size() > 0) {
                game.setPublisher(pubs.get(0).getAsString());
            }
        }

        //如果存在price_overview，获取价格，没有price_overview则判断is_free是否存在且对应值为true
        if (data.has("price_overview")) {
            JsonObject priceObj = data.getAsJsonObject("price_overview");
            int finalCents = priceObj.get("final").getAsInt();
            BigDecimal price = BigDecimal.valueOf(finalCents)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            game.setPrice(price);
        } else if (data.has("is_free") && data.get("is_free").getAsBoolean()) {
            game.setPrice(BigDecimal.ZERO);
        } else {
            game.setPrice(null);
        }

        //发行日期
        if (data.has("release_date")) {
            JsonObject release = data.getAsJsonObject("release_date");
            if (release.has("date")) {
                game.setReleaseDate(release.get("date").getAsString());
            }
        }

        //获取类型
        if (data.has("type")) {
            game.setType(data.get("type").getAsString());
        } else {
            game.setType("game"); // 默认值
        }

        //类型标签（genres）
        List<Tag> genres = new ArrayList<>();
        if (data.has("genres") && data.get("genres").isJsonArray()) {
            JsonArray genresArray = data.getAsJsonArray("genres");
            for (int i = 0; i < genresArray.size(); i++) {
                JsonObject genreObj = genresArray.get(i).getAsJsonObject();
                if (genreObj.has("description")) {
                    genres.add(new Tag(genreObj.get("description").getAsString()));
                }
            }
        }
        game.setGenres(genres);

        return game;
    }

    public JsonObject fetchReviewSummary(int appId) throws Exception {
        String url = "https://store.steampowered.com/appreviews/" + appId + "?json=1&filter=all&language=all&review_type=all&purchase_type=all";
        String json = HttpUtil.get(url);
        JsonObject root = gson.fromJson(json, JsonObject.class);
        return root.getAsJsonObject("query_summary");
    }

    public Set<String> fetchAppIdsFromSteamDbCalendar(String filePath)throws Exception{
        String html = new String(Files.readAllBytes(Paths.get(filePath)),"UTF-8");

        Document parse =Jsoup.parse(html);
        Elements elements = parse.select(".calendar [data-appid]");

        Set<String> appIds = new LinkedHashSet<>();
        for (Element element : elements){
            String appId = element.attr("data-appid");
            if (appId != null && !appId.isEmpty()) {
                appIds.add(appId);
            }
        }

        System.out.println("从 SteamDB 解析到 " + appIds.size() + " 个游戏 ID。");
        return appIds;
    }




}
