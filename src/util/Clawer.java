package util;

import com.google.gson.JsonObject;
import entity.GameShop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.SteamApiService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 向珅葳
 * @date : 2026/6/26
 * @time : 15:06
 * @project_name : SteamSQL
 */
public class Clawer {
    Logger logger = LogManager.getLogger();
    public GameShop clawerSiegel(String idStr){
        System.out.println("正在爬取 App ID: " + idStr);

        //steam服务对象
        SteamApiService service = new SteamApiService();

        //解析id字符串获取appId
        int appId = Integer.parseInt(idStr);


        try {
            //获取基本信息
            GameShop game = service.getGameDetail(appId);
            if (game == null) {
                System.err.println(appId + " 号基本信息为空");
                return null;
            }

            //获取评论汇总
            JsonObject summary = service.fetchReviewSummary(appId);
            if (summary != null) {
                game.setPositiveReviews(summary.get("total_positive").getAsLong());
                game.setNegativeReviews(summary.get("total_negative").getAsLong());
                game.setTotalReviews(summary.get("total_reviews").getAsLong());
            } else {
                //如果没数据，给默认值
                game.setPositiveReviews(0);
                game.setNegativeReviews(0);
                game.setTotalReviews(0);
            }

            return game;

        } catch (Exception e) {
            System.err.println(appId + " 号爬取失败: " + e.getMessage());
            return null;
        }
    }

    public List<Integer> clawerBatch(List<String> idStrs){
        List<Integer> failAppIds = new ArrayList<>();
        for (String idstr:idStrs){
           GameShop game= clawerSiegel(idstr);
           if (game==null){
               failAppIds.add(Integer.parseInt(idstr));
           }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return failAppIds;

    }
}
