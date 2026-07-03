package control.clawer;

import com.google.gson.JsonObject;
import control.BaseControl;
import control.Control;
import control.ControlEnum;
import dao.impl.StreamApiDaoImpl;
import entity.GameShop;
import service.GamesTableService;
import service.SteamApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: 向珅葳
 * @date : 2026/7/1
 * @time : 8:16
 * @project_name : SteamSQL
 */
public class ClawerByMonthControl extends BaseControl implements Control {
    private static final String INPUT_MONTH = "请输入保存到本地的SteamDB.html地址";
    private static final String PROGRESS = "正在爬取第 {}/{} 个游戏 | app_id:{}";
    private static final String SUCCESS = "游戏 {} 入库成功";
    private static final String FAIL = "游戏 {} 入库失败: {}";
    private static final String COMPLETE = "爬取完成！成功：{}，失败：{}";
    private static final String FILE_READ_PATH = "未解析到任何游戏 ID，请检查文件路径或格式";
    private static final String NOT_IN_CHINA = "游戏 {} 数据为空，可能国区不可用";
    GamesTableService gamesTableService = new GamesTableService(new StreamApiDaoImpl(),new SteamApiService());
    SteamApiService steamApiService = new SteamApiService();
    @Override
    public Control execute() {
        logger.info(INPUT_MONTH);
        String path = readFilePath();
        try {
            Set<String> strIds = steamApiService.fetchAppIdsFromSteamDbCalendar(path);
            if (strIds == null || strIds.isEmpty()) {
                logger.info(FILE_READ_PATH);
                return ControlEnum.START.getControl();
            }


            List<String> idList = new ArrayList<>(strIds);
            List<Integer> failedIds = new ArrayList<>();
            int successCount = 0;

            for (int i = 0; i < idList.size(); i++) {
                String strId = idList.get(i);
                int appId = Integer.parseInt(strId);
                logger.info(PROGRESS, i + 1, idList.size(), appId);

                try {
                    GameShop game = steamApiService.getGameDetail(appId);
                    if (game == null) {
                        logger.info(NOT_IN_CHINA, appId);
                        failedIds.add(appId);
                        continue;
                    }
                    gamesTableService.gameTableTransaction(game);
                    successCount++;
                    logger.info(SUCCESS, appId);
                } catch (Exception e) {
                    failedIds.add(appId);
                    logger.error(FAIL, appId, e.getMessage());
                }

            }

            logger.info(COMPLETE, successCount, failedIds.size());

            if (successCount > 0) {
                gamesTableService.updateAllReviews();
            } else {
                logger.info("没有成功入库的游戏，跳过评论更新。");
            }

        } catch (Exception e) {
            logger.error("爬取过程中出现错误", e);
        }
        return ControlEnum.CLAWER_GAME_SHOP.getControl();
    }
}
