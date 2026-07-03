package control.clawer;

import control.BaseControl;
import control.Control;
import control.ControlEnum;
import dao.impl.StreamApiDaoImpl;
import entity.GameShop;
import service.GamesTableService;
import service.SteamApiService;
import util.Clawer;

/**
 * @author: 向珅葳
 * @date : 2026/7/1
 * @time : 8:16
 * @project_name : SteamSQL
 */
public class ClawerSiegelControl extends BaseControl implements Control {
    private static final String INPUT_APPID="请输入要爬取游戏的appIO";
    private static final String CLAWER_SUCCESS = "爬取成功";
    private static final String CLAWER_FAIL = "爬取失败";
    private Clawer clawer = new Clawer();
    private GamesTableService service = new GamesTableService(new StreamApiDaoImpl(),new SteamApiService());
    @Override
    public Control execute() {
        logger.info(INPUT_APPID);
        String appId= input.next();
        GameShop gameShop = clawer.clawerSiegel(appId);
        if (null != gameShop){
            logger.info(CLAWER_SUCCESS);
            try {
                service.gameTableTransaction(gameShop);
            } catch (Exception e) {
                logger.error("存储失败！",e);
            }
        }else {
            logger.info(CLAWER_FAIL);
        }
        return ControlEnum.CLAWER_GAME_SHOP.getControl();
    }

    public static void main(String[] args) {
        ClawerSiegelControl control = new ClawerSiegelControl();
        control.execute();
    }
}
