package service;

import com.google.gson.JsonObject;
import dao.BaseDao;
import dao.SteamApiDao;
import entity.GameShop;
import entity.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import translate_enum.Translate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: 向珅葳
 * @date : 2026/6/29
 * @time : 8:37
 * @project_name : SteamSQL
 */
public class GamesTableService {
    private SteamApiDao steamDao;
    private SteamApiService steamService;
    private Logger logger = LogManager.getLogger();

    private static final String UPDATE_REVIEW = "游戏 {} 的评论数据更新成功";
    private static final String UPDATE_PROCESS = "进度: {}/{} | 成功: {} | 失败: {}";
    private static final String UPDATE_REVIEW_FINAL="========== 评论更新完成 ==========\n总游戏数: {}\n成功更新: {}\n失败: {}";

    public GamesTableService(SteamApiDao steamDao,SteamApiService steamService){
        this.steamDao=steamDao;
        this.steamService=steamService;
    }
    private Connection getConn(){
        return BaseDao.getConn();
    }

    public void gameTableTransaction(GameShop game)throws Exception{
        Connection conn = null;
        try {
            conn = getConn();
            conn.setAutoCommit(false);
            int rows = steamDao.insertNewGame(game, conn);
            if (rows == 1) {
                logger.info("游戏 {} 新增入库", game.getAppId());
            } else if (rows == 2) {
                logger.info("游戏 {} 已存在，数据已更新", game.getAppId());
            } else {
                logger.info("游戏 {} 未发生变化，跳过", game.getAppId());
            }
            for (Tag tag : game.getGenres()) {
                steamDao.insertNewTag(tag, conn);
            }
            steamDao.insertNewGame_tag(game,conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null){
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("回滚失败！",ex);

                }
            }
            logger.error("保存游戏 {} 失败", game.getAppId(), e);
            throw e;
        } finally {
            BaseDao.closeAll(conn,null,null,null);
        }
    }

    public void updateAllReviews() {
        Connection conn = null;
        PreparedStatement countStmt = null;
        PreparedStatement queryStmt = null;
        ResultSet countRs = null;
        ResultSet queryRs = null;
        int total = 0;
        int success = 0;
        int fail = 0;

        try {
            conn = BaseDao.getConn();

            //查询总记录数
            String countSql = "SELECT COUNT(*) FROM games";
            countStmt = conn.prepareStatement(countSql);
            countRs = countStmt.executeQuery();
            if (countRs.next()) {
                total = countRs.getInt(1);
            }

            if (total == 0) {
                logger.info("游戏表为空，无需更新评论。");
                return;
            }

            //查询所有 app_id
            String querySql = "SELECT app_id FROM games";
            queryStmt = conn.prepareStatement(querySql);
            queryRs = queryStmt.executeQuery();

            int processed = 0;
            while (queryRs.next()) {
                int appId = queryRs.getInt("app_id");
                processed++;

                try {
                    JsonObject summary = steamService.fetchReviewSummary(appId);
                    long positive = 0, negative = 0, totalReviews = 0;
                    String desc = null;
                    if (summary != null) {
                        positive = summary.get("total_positive").getAsLong();
                        negative = summary.get("total_negative").getAsLong();
                        totalReviews = summary.get("total_reviews").getAsLong();
                        desc = summary.get("review_score_desc").getAsString();
                    }
                    steamDao.updateReviewData(appId, positive, negative, totalReviews, desc, conn);
                    success++;
                    logger.info(UPDATE_REVIEW,appId);
                    //每处理10条打印一次进度
                    if (processed % 10 == 0 || processed == total) {
                        logger.info(UPDATE_PROCESS, processed, total, success, fail);
                    }
                } catch (Exception e) {
                    fail++;
                    logger.error("更新 {} 评论失败: {}", appId, e.getMessage());
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("线程被中断，停止更新评论");
                    break;
                }
            }

            logger.info(UPDATE_REVIEW_FINAL,total,success,fail);

        } catch (SQLException e) {
            logger.error("更新评论过程中发生数据库错误", e);
        } finally {
            // 手动关闭资源（顺序：ResultSet → PreparedStatement → Connection）
            BaseDao.closeAll(conn, null, countStmt, countRs);
            BaseDao.closeAll(null, null, queryStmt, queryRs);
        }
    }

    public void insertNewNot_in_china(int appid){
        Connection conn = getConn();
        try {
            steamDao.insertNewNot_in_china(appid,conn);
        } catch (SQLException e) {
            logger.error("插入非国区appid错误",e);
        }finally {
            BaseDao.closeAll(conn,null,null,null);
        }
    }

    public int translateDesc(){
        Connection conn = null;
        try {
            conn = BaseDao.getConn();
            List<GameShop> games = steamDao.getDesc(conn);

            int count =0;
            for (GameShop game : games) {
                String original = game.getReview_score_desc();
                String translated = "";
                if (game.getReview_score_desc()==null){
                    translated = Translate.NO_USER_REVIEWS.getChinese();
                }
                switch (game.getReview_score_desc()){
                    case "Overwhelmingly Positive":
                        translated = Translate.OVERWHELMINGLY_POSITIVE.getChinese();
                        break;
                    case "Very Positive":
                        translated = Translate.VERY_POSITIVE.getChinese();
                        break;
                    case "Mostly Positive":
                        translated = Translate.MOSTLY_POSITIVE.getChinese();
                        break;
                    case "Mixed":
                        translated = Translate.MIXED.getChinese();
                        break;
                    case "Mostly Negative":
                        translated = Translate.MOSTLY_NEGATIVE.getChinese();
                        break;
                    case "Negative":
                        translated = Translate.NEGATIVE.getChinese();
                        break;
                    case "Overwhelmingly Negative":
                        translated = Translate.OVERWHELMINGLY_NEGATIVE.getChinese();
                        break;
                    case "Positive":
                        translated = Translate.POSITIVE.getChinese();
                        break;
                    case "No user reviews":
                        translated = Translate.NO_USER_REVIEWS.getChinese();
                        break;
                    default:
                        translated = game.getReview_score_desc();
                }
                if (!translated.equals(original)) {
                    game.setReview_score_desc(translated);
                    steamDao.updateDesc(game, conn);
                    count++;  // 只有实际更新时才计数
                }
                game.setReview_score_desc(translated);
            }
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            BaseDao.closeAll(conn,null,null,null);
        }
    }

    public List<GameShop> getGamesByMonth(int month,int year,int offset){
        List<GameShop> gameShops = null;
        try {
           gameShops= steamDao.getGameByMonth(month,year,offset);
        } catch (SQLException e) {
            logger.error("根据月份获取游戏数据失败",e);
        }
        return gameShops;
    }

    public int getGamesCount(){
        int count = 0;
        try {
          count= steamDao.getTotalGame();
        } catch (SQLException e) {
            logger.error(e);
        }
        return count;
    }

    public int getGamesCountByMonth(int month){
        int count = 0;
        try {
            count= steamDao.getTotalGameByMonth(month);
        } catch (SQLException e) {
            logger.error("获取记录数量失败！",e);
        }
        return count;
    }

    public List<GameShop> getGameByFree(){
        List<GameShop> gameShops = null;
        try {
            gameShops = steamDao.getGameByFree();
        } catch (SQLException e) {
            logger.error("获取免费游戏信息失败",e);
        }
        return gameShops;
    }

    public List<GameShop> getGameByTop10PositiveRate(){
        List<GameShop> gameShops = null;
        try {
            gameShops = steamDao.getGameByTop10PositiveRate();
        } catch (SQLException e) {
            logger.error("获取Top10游戏信息失败",e);
        }
        return gameShops;
    }

    public int parsedDateTime(){
        int i = 0;
        try {
            i = steamDao.parsedDateTime();
        } catch (SQLException e) {
            logger.error("转换日期失败！",e);
        }
        return i;
    }
}
