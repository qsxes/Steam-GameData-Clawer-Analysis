package control.select;

import control.BaseControl;
import control.Control;
import control.ControlEnum;
import dao.SteamApiDao;
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
 * @time : 8:37
 * @project_name : SteamSQL
 */
public class SelectGameByMonth extends BaseControl implements Control {
    private GamesTableService service = new GamesTableService(new StreamApiDaoImpl(),new SteamApiService());
    private Logger logger = LogManager.getLogger();
    private static final String MONTH_INPUT = "请输入月份：";
    private static final String YEAR_INPUT = "请输入年份：";
    private static final String CURR_PAGE = "共{}页,当前第{}页";
    private static final String OPTION="1.下一页 2.上一页 3.查看游戏详情 4.输入跳转页面 0.返回上一级";
    private static final String HEAD="到头了，无法继续向前翻页";
    private static final String TAIL="到尾了，无法继续向后翻页";
    private int month = 0;
    private int currPage = 1;
    private int totalPage = 0;
    private int totalCount = 0;
    private int year = 0;

    @Override
    public Control execute() {
        if (month==0){
            logger.info(MONTH_INPUT);
            month = input.nextInt();
        }
        if (year==0){
            logger.info(YEAR_INPUT);
            year = input.nextInt();
        }
        totalCount = service.getGamesCountByMonth(month);
        totalPage = (totalCount+10-1)/10;
        List<GameShop> gamesByMonth = service.getGamesByMonth(month,year,(currPage-1)*10);
        printGames(gamesByMonth,currPage);
        logger.info(CURR_PAGE,totalPage,currPage);
        logger.info(OPTION);
        int option = input.nextInt();
        switch (option){
            case 1:
                if (currPage<totalPage){
                    currPage++;
                    return this;
                }else {
                    logger.info(TAIL);
                    return this;
                }
            case 2:
                if (currPage>1){
                    currPage--;
                    return this;
                }else {
                    logger.info(HEAD);
                    return this;
                }
            case 3:
                logger.info("请输入要查看的游戏序号（当前页范围内）：");
                int num = input.nextInt();
                // 计算在 gamesByMonth 列表中的索引
                int index = num - (currPage - 1) * 10 - 1;
                if (index >= 0 && index < gamesByMonth.size()) {
                    GameShop g = gamesByMonth.get(index);
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
            case 4:
                logger.info("请输入要跳转的页面");
                while (true){
                    int page = input.nextInt();
                    if (page<=0 || page>totalPage){
                        logger.error("输入值超过页面数量！请重新输入");
                        continue;
                    }
                    currPage=page;
                    return this;

                }
            case 0:
                return ControlEnum.SELECT.getControl();
            default:
                return this;
        }
    }


    private void printGames(List<GameShop> games, int currentPage) {
        if (games == null || games.isEmpty()) {
            logger.info("暂无数据");
            return;
        }
        int startIndex = (currentPage - 1) * 10 + 1;
        logger.info("序号 | 游戏名 | 开发商 | 价格(元) | 好评率 | game/dlc");
        int i = startIndex;
        for (GameShop g : games) {
            String rate = (g.getPositiveRatePercent() > 0) ?
                    g.getPositiveRatePercent().toString() : "N/A";
            String name = g.getName() == null ? "" : g.getName();
            String dev = g.getDeveloper() == null ? "" : g.getDeveloper();
            String price = g.getPrice() == null ? "暂无价格" : g.getPrice().toString();
            String type  = g.getType() == null ? "" : g.getType();
            logger.info("{} | {} | {} | {}￥ | {}% | {}", i++, name, dev, price, rate,type);
        }
    }

    public static void main(String[] args) {
        SelectGameByMonth selectGameByMonth = new SelectGameByMonth();
        Control execute = selectGameByMonth.execute();
        while (execute!=null){
            execute = execute.execute();
        }
    }
}

