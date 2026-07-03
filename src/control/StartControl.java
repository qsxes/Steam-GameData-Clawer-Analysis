package control;


import dao.BaseDao;
import dao.impl.StreamApiDaoImpl;
import service.GamesTableService;
import service.SteamApiService;

/**
 * @author: 向珅葳
 * @date : 2026/6/21
 * @time : 10:08
 * @project_name : JDBC
 */
public class StartControl extends BaseControl{
    private static final String START_MENU="请选择功能\n1.爬取游戏商店数据\t2.查看游戏数据\n3.修复日期字段\t4.初始化数据库(当前状态：{})\n5.翻译评论描述字段\n0.退出:";
    private static final String PARSED_DATETIME="已更新 {} 条记录的日期字段";
    private static final String INIT_EFFECT = "执行了{}条建表语句";
    private static final String TRANSLATE_REVIEW_DESC="翻译完成，共更新 {} 条记录";
    private GamesTableService service = new GamesTableService(new StreamApiDaoImpl(),new SteamApiService());

    @Override
    public Control execute() {
        logger.info(START_MENU, BaseDao.is_init()?"已初始化":"未初始化");
        int type = readInt();
        switch (type){
            case 1:
                if (!BaseDao.is_init()) {
                    logger.warn("数据库未初始化，请先执行【4. 初始化数据库】");
                    return this;
                }
                return ControlEnum.CLAWER_GAME_SHOP.getControl();
            case 2:
                if (!BaseDao.is_init()) {
                    logger.warn("数据库未初始化，请先执行【4. 初始化数据库】");
                    return this;
                }
                return ControlEnum.SELECT.getControl();
            case 3:
                if (!BaseDao.is_init()) {
                    logger.warn("数据库未初始化，请先执行【4. 初始化数据库】");
                    return this;
                }
                int updated = service.parsedDateTime();
                logger.info(PARSED_DATETIME, updated);
                return this;
            case 4:
                int i = BaseDao.init_sql();
                logger.info(INIT_EFFECT,i);
                return this;
            case 5:
                if (!BaseDao.is_init()) {
                    logger.warn("数据库未初始化，请先执行【4. 初始化数据库】");
                    return this;
                }
                int count = service.translateDesc();
                logger.info(TRANSLATE_REVIEW_DESC,count);
                return this;
            case 0:
                return null;
            default:
                logger.info(OUTPUT_TEXT_INVALID_INPUT);
                return this;
        }
    }
}
