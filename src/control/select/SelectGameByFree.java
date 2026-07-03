package control.select;

import control.BaseControl;
import control.Control;
import control.ControlEnum;
import dao.BaseDao;
import dao.impl.StreamApiDaoImpl;
import entity.GameShop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.GamesTableService;
import service.SteamApiService;

import java.util.List;

/**
 * @author: 向珅葳
 * @date : 2026/7/1
 * @time : 10:56
 * @project_name : SteamSQL
 */
public class SelectGameByFree extends BaseControl implements Control {
    private GamesTableService service = new GamesTableService(new StreamApiDaoImpl(),new SteamApiService());
    private Logger logger = LogManager.getLogger();
    private static final String INPUT="1.查看详情 0.返回上一级";
    private static final String INPUT_NUM = "请输入序号";


    private void printGames(List<GameShop> games) {
        if (games == null || games.isEmpty()) {
            logger.info("暂无数据");
            return;
        }
        int i = 1;
        logger.info("序号 | 游戏名 | 开发商 | 价格(元) | 好评率 | game/dlc");
        for (GameShop g : games) {
            String rate = (g.getPositiveRatePercent() > 0) ?
                    g.getPositiveRatePercent().toString() : "N/A";
            String name = g.getName() == null ? "" : g.getName();
            String dev = g.getDeveloper() == null ? "" : g.getDeveloper();
            String price = g.getPrice() == null ? "暂无价格" : g.getPrice().toString();
            String type  = g.getType() == null ? "" : g.getType();
            logger.info("{} | {} | {} | {} | {} | {}", i++, name, dev, price, rate,type);
        }
    }

    @Override
    public Control execute() {
        List<GameShop> gameByFree = service.getGameByFree();
        printGames(gameByFree);
        logger.info(INPUT);
        int choice = input.nextInt();
        switch (choice){
            case 1:
                logger.info(INPUT_NUM);
                int index = input.nextInt();
                if (index >= 0 && index < gameByFree.size()) {
                    GameShop g = gameByFree.get(index-1);
                    logger.info("========== 游戏详情 ==========");
                    logger.info("AppID: {}", g.getAppId());
                    logger.info("名称: {}", g.getName());
                    logger.info("开发商: {}", g.getDeveloper());
                    logger.info("发行商: {}", g.getPublisher());
                    logger.info("价格: {}", g.getPrice() != null ? g.getPrice() : "免费");
                    logger.info("发行日期: {}", g.getReleaseDate());
                    logger.info("好评数: {}", g.getPositiveReviews());
                    logger.info("差评数: {}", g.getNegativeReviews());
                    logger.info("总评价数: {}", g.getTotalReviews());
                    logger.info("评分描述: {}", g.getReview_score_desc());
                    logger.info("类型: {}", g.getType());
                    logger.info("好评率: {}%", g.getPositiveRatePercent() != null ? g.getPositiveRatePercent() : "N/A");
                    logger.info("标签: {}", g.getTags());
                    logger.info("==============================");
                } else {
                    logger.info("无效序号，请输入当前页显示的序号。");
                }
                return this;
            case 0:
                return ControlEnum.SELECT.getControl();
            default:
                return this;
        }
    }

    public static void main(String[] args) {
        SelectGameByFree selectGameByFree = new SelectGameByFree();
        Control execute = selectGameByFree.execute();
        while (execute!=null){
            execute = execute.execute();
        }
    }
}
