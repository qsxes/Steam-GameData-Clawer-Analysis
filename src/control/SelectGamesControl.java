package control;

/**
 * @author: 向珅葳
 * @date : 2026/7/1
 * @time : 8:18
 * @project_name : SteamSQL
 */
public class SelectGamesControl extends BaseControl implements Control{
    private static final String SELECT_TYPE="请选择你的查询要求：1.根据月份查询 2.查询免费游戏 3.查询好评率Top10 0.返回上一级";
    @Override
    public Control execute() {
        logger.info(SELECT_TYPE);
        int type = input.nextInt();
        switch (type){
            case 0:
                return ControlEnum.START.getControl();
            case 1:
                return ControlEnum.SELECT_GAME_BY_MONTH.getControl();
            case 2:
                return ControlEnum.SELECT_GAME_BY_FREE.getControl();
            case 3:
                return ControlEnum.SELECT_GAME_BY_TOP10.getControl();
            default:
                logger.info(OUTPUT_TEXT_INVALID_INPUT);
                return this;
        }
    }
}
