package control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author: 向珅葳
 * @date : 2026/7/2
 * @time : 11:08
 * @project_name : SteamSQL
 */
public class ClawerGameShopControl extends BaseControl implements Control{
    private static final String CLAWER_OPTION="请选择你要进行的爬取方式 1.爬取某一月份的新发布游戏数据 2.爬取单个游戏的游戏发布数据 0.返回上一级";
    @Override
    public Control execute() {
        logger.info(CLAWER_OPTION);
        int option = input.nextInt();
        switch (option){
            case 1:
                return ControlEnum.CLAWER_BY_MONTH.getControl();
            case 2:
                return ControlEnum.CLAWER_SIEGEL.getControl();
            case 0:
                return ControlEnum.START.getControl();
            default:
                logger.info(OUTPUT_TEXT_INVALID_INPUT);
                return this;
        }
    }
}
