package dao;

import entity.GameShop;
import entity.Tag;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: 向珅葳
 * @date : 2026/6/26
 * @time : 16:36
 * @project_name : SteamSQL
 */
public interface SteamApiDao {
    void insertNewGame(GameShop gameShop, Connection conn)throws SQLException;
    void insertNewTag(Tag tag, Connection conn)throws SQLException;
    void insertNewGame_tag(GameShop gameShop, Connection conn)throws SQLException;
    void updateReviewData(int appId, long positive, long negative, long total, String desc, Connection conn) throws SQLException;
    void insertNewNot_in_china(int appid,Connection conn)throws SQLException;
    List<GameShop> getDesc(Connection conn)throws SQLException;
    void updateDesc(GameShop game,Connection conn)throws SQLException;
    List<GameShop> getGameByMonth(int month,int offset)throws SQLException;
    int getTotalGame()throws SQLException;
    int getTotalGameByMonth(int month)throws SQLException;
    List<GameShop> getGameByFree()throws SQLException;
    List<GameShop> getGameByTop10PositiveRate() throws SQLException;
    int parsedDateTime()throws SQLException;
    GameShop selectGameByAppId(int app_id) throws SQLException;
    public boolean existsGame(int appId, Connection conn) throws SQLException;

}
