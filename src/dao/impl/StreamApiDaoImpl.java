package dao.impl;

import dao.BaseDao;
import dao.SteamApiDao;
import entity.GameShop;
import entity.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: 向珅葳
 * @date : 2026/6/26
 * @time : 17:04
 * @project_name : SteamSQL
 */
public class StreamApiDaoImpl extends BaseDao implements SteamApiDao {
    Logger logger = LogManager.getLogger();
    @Override
    public int insertNewGame(GameShop gameShop, Connection conn)throws SQLException {
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(getProperty("insertNewGame"));
            //INSERT INTO games
            //(app_id, name, developer, publisher,
            // price, release_date, positive_reviews,
            // negative_reviews, total_reviews, review_score_desc)
            //VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            pstmt.setInt(1,gameShop.getAppId());
            pstmt.setString(2,gameShop.getName());
            pstmt.setString(3,gameShop.getDeveloper());
            pstmt.setString(4,gameShop.getPublisher());
            pstmt.setBigDecimal(5,gameShop.getPrice());
            pstmt.setString(6, gameShop.getReleaseDate());
            pstmt.setLong(7,gameShop.getPositiveReviews());
            pstmt.setLong(8,gameShop.getNegativeReviews());
            pstmt.setLong(9,gameShop.getTotalReviews());
            pstmt.setString(10,gameShop.getReview_score_desc());
            pstmt.setString(11,gameShop.getType());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }finally {
            closeAll(null,null,pstmt,null);
        }
    }

    public void insertNewTag(Tag tag, Connection conn)throws SQLException{
        PreparedStatement pstmts = null;
        PreparedStatement pstmtIn = null;
        ResultSet rs = null;
        String tag_name = null;
        try {
            pstmts = conn.prepareStatement(getProperty("selectTagId"));
            pstmts.setString(1,tag.getTag_name());
            rs = pstmts.executeQuery();
            if (rs.next()){
                return;
            }else {
                pstmtIn = conn.prepareStatement(getProperty("insertNewTag"));
                pstmtIn.setString(1,tag.getTag_name());
                pstmtIn.executeUpdate();
            }
//            pstmt = conn.prepareStatement(getProperty("insertNewTag"));
//            pstmt.setString(1,tag.getTag_name());
//            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            closeAll(null,null,pstmts,rs);
            closeAll(null,null,pstmtIn,null);
        }
    }

    public void insertNewGame_tag(GameShop gameShop,Connection conn)throws SQLException{
        PreparedStatement pstmt = null;
        List<Tag> tags = gameShop.getGenres();
        try {
            pstmt = conn.prepareStatement(getProperty("insertNewGame_Tag"));
            for (Tag tag : tags){
                pstmt.setInt(1,gameShop.getAppId());
                pstmt.setString(2,tag.getTag_name());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            closeAll(null,null,pstmt,null);
        }
    }

    public void updateReviewData(int appId, long positive, long negative, long total, String desc, Connection conn) throws SQLException {
        String sql = "UPDATE games SET positive_reviews=?, negative_reviews=?, total_reviews=?,review_score_desc=? WHERE app_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, positive);
            pstmt.setLong(2, negative);
            pstmt.setLong(3, total);
            pstmt.setString(4,desc);
            pstmt.setInt(5, appId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void insertNewNot_in_china(int appid,Connection conn) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement(getProperty("insertNotInCHINA"));
        pstmt.setInt(1,appid);
        pstmt.executeUpdate();
    }


    @Override
    public List<GameShop> getDesc(Connection conn)throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GameShop> gameShops = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(getProperty("selectGamesDesc"));
            rs = pstmt.executeQuery();
            while (rs.next()){
                GameShop gameShop = new GameShop();
                gameShop.setAppId(rs.getInt("app_id"));
                gameShop.setReview_score_desc(rs.getString("review_score_desc"));
                gameShops.add(gameShop);
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            closeAll(null,null,pstmt,rs);
        }
        return gameShops;
    }

    @Override
    public void updateDesc(GameShop game, Connection conn)throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(getProperty("updateGamesDesc"));
            pstmt.setString(1,game.getReview_score_desc());
            pstmt.setInt(2,game.getAppId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            closeAll(null,null,pstmt,null);
        }
    }

    @Override
    public List<GameShop> getGameByMonth(int month,int year,int offset) throws SQLException{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GameShop> gameShops = new ArrayList<>();

        try {
            conn = getConn();
            pstmt = conn.prepareStatement(getProperty("SELECTBYMONTH"));
            pstmt.setInt(1,month);
            pstmt.setInt(2,year);
            pstmt.setInt(3,offset);
            rs = pstmt.executeQuery();
            while (rs.next()){
                GameShop gameShop = new GameShop();
                gameShop.setAppId(rs.getInt("app_id"));
                gameShop.setName(rs.getString("name"));
                gameShop.setDeveloper(rs.getString("developer"));
                gameShop.setPublisher(rs.getString("publisher"));
                gameShop.setPrice(rs.getBigDecimal("price"));
                gameShop.setReleaseDate(rs.getString("release_date_parsed"));
                gameShop.setTags(rs.getString("tags"));
                gameShop.setPositiveReviews(rs.getLong("positive_reviews"));
                gameShop.setNegativeReviews(rs.getLong("negative_reviews"));
                gameShop.setTotalReviews(rs.getLong("total_reviews"));
                gameShop.setReview_score_desc(rs.getString("review_score_desc"));
                gameShop.setType(rs.getString("type"));
                gameShop.setPositiveRatePercent(rs.getDouble("positive_rate_percent"));
                gameShops.add(gameShop);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeAll(conn,null,pstmt,rs);
        }
        return gameShops;
    }

    @Override
    public int getTotalGame() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = getConn();
            pstmt = conn.prepareStatement(getProperty("getTotalGame"));
            rs = pstmt.executeQuery();
            if (rs.next()){
                count = rs.getInt("conut");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeAll(conn,null,pstmt,rs);
        }
        return count;
    }

    @Override
    public int getTotalGameByMonth(int month) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = getConn();
            pstmt = conn.prepareStatement(getProperty("getTotalGameByMonth"));
            pstmt.setInt(1,month);
            rs = pstmt.executeQuery();
            if (rs.next()){
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeAll(conn,null,pstmt,rs);
        }
        return count;
    }

    @Override
    public List<GameShop> getGameByFree() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GameShop> gameShops = new ArrayList<>();

        try {
            conn = getConn();
            pstmt = conn.prepareStatement(getProperty("getGameByFree"));
            rs = pstmt.executeQuery();
            while (rs.next()){
                GameShop gameShop = new GameShop();
                gameShop.setAppId(rs.getInt("app_id"));
                gameShop.setName(rs.getString("name"));
                gameShop.setDeveloper(rs.getString("developer"));
                gameShop.setPublisher(rs.getString("publisher"));
                gameShop.setPrice(rs.getBigDecimal("price"));
                gameShop.setReleaseDate(rs.getString("release_date_parsed"));
                gameShop.setTags(rs.getString("tags"));
                gameShop.setPositiveReviews(rs.getLong("positive_reviews"));
                gameShop.setNegativeReviews(rs.getLong("negative_reviews"));
                gameShop.setTotalReviews(rs.getLong("total_reviews"));
                gameShop.setReview_score_desc(rs.getString("review_score_desc"));
                gameShop.setType(rs.getString("type"));
                gameShop.setPositiveRatePercent(rs.getDouble("positive_rate_percent"));
                gameShops.add(gameShop);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeAll(conn,null,pstmt,rs);
        }
        return gameShops;
    }

    @Override
    public List<GameShop> getGameByTop10PositiveRate() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<GameShop> gameShops = new ArrayList<>();

        try {
            conn = getConn();
            pstmt = conn.prepareStatement(getProperty("getGameByPositive"));
            rs = pstmt.executeQuery();
            while (rs.next()){
                GameShop gameShop = new GameShop();
                gameShop.setAppId(rs.getInt("app_id"));
                gameShop.setName(rs.getString("name"));
                gameShop.setDeveloper(rs.getString("developer"));
                gameShop.setPublisher(rs.getString("publisher"));
                gameShop.setPrice(rs.getBigDecimal("price"));
                gameShop.setReleaseDate(rs.getString("release_date_parsed"));
                gameShop.setTags(rs.getString("tags"));
                gameShop.setPositiveReviews(rs.getLong("positive_reviews"));
                gameShop.setNegativeReviews(rs.getLong("negative_reviews"));
                gameShop.setTotalReviews(rs.getLong("total_reviews"));
                gameShop.setReview_score_desc(rs.getString("review_score_desc"));
                gameShop.setType(rs.getString("type"));
                gameShop.setPositiveRatePercent(rs.getDouble("positive_rate_percent"));
                gameShops.add(gameShop);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeAll(conn,null,pstmt,rs);
        }

        return gameShops;
    }

    @Override
    public int parsedDateTime()throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int i = 0;
        try {
            conn = getConn();
            pstmt = conn.prepareStatement(getProperty("parsedDateTime"));
            i = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            closeAll(conn,null,pstmt,null);
        }
        return i;
    }

    @Override
    public GameShop selectGameByAppId(int app_id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        GameShop gameShop = null;

        conn = getConn();
        pstmt = conn.prepareStatement("selectGameByAppId");
        pstmt.setInt(1,app_id);
        rs = pstmt.executeQuery();

        if (rs.next()){
            gameShop = new GameShop();
            gameShop.setAppId(rs.getInt("app_id"));
            gameShop.setName(rs.getString("name"));
            gameShop.setDeveloper(rs.getString("developer"));
            gameShop.setPublisher(rs.getString("publisher"));
            gameShop.setPrice(rs.getBigDecimal("price"));
            gameShop.setReleaseDate(rs.getString("release_date_parsed"));
            gameShop.setTags(rs.getString("tags"));
            gameShop.setPositiveReviews(rs.getLong("positive_reviews"));
            gameShop.setNegativeReviews(rs.getLong("negative_reviews"));
            gameShop.setTotalReviews(rs.getLong("total_reviews"));
            gameShop.setReview_score_desc(rs.getString("review_score_desc"));
            gameShop.setType(rs.getString("type"));
            gameShop.setPositiveRatePercent(rs.getDouble("positive_rate_percent"));
        }
        return gameShop;
    }

    @Override
    public boolean existsGame(int appId, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(getProperty("isGameExist"));
            pstmt.setInt(1, appId);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw e;
        } finally {
            closeAll(null,null,pstmt,rs);
        }

    }
}
