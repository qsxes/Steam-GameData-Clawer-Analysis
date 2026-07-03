package control;


import control.clawer.ClawerByMonthControl;
import control.clawer.ClawerSiegelControl;
import control.select.SelectGameByFree;
import control.select.SelectGameByMonth;
import control.select.SelectGameByTop10;

/**
 * @author: 向珅葳
 * @date : 2026/5/29
 * @time : 16:04
 * @project_name : txtReader
 */
public enum ControlEnum {
    // ================== 顶层入口 ==================
    START {
        @Override
        public Control getControl() {
            return new StartControl();
        }
    },
    SELECT{
        @Override
        public Control getControl() {
            return new SelectGamesControl();
        }
    },
    CLAWER_SIEGEL{
        @Override
        public Control getControl() {
            return new ClawerSiegelControl();
        }
    },
    CLAWER_BY_MONTH{
        @Override
        public Control getControl() {
            return new ClawerByMonthControl();
        }
    },
    SELECT_GAME_BY_MONTH{
        @Override
        public Control getControl() {
            return new SelectGameByMonth();
        }
    },
    SELECT_GAME_BY_FREE{
        @Override
        public Control getControl() {
            return new SelectGameByFree();
        }
    },
    SELECT_GAME_BY_TOP10{
        @Override
        public Control getControl() {
            return new SelectGameByTop10();
        }
    },CLAWER_GAME_SHOP{
        @Override
        public Control getControl() {
            return new ClawerGameShopControl();
        }
    };


    public abstract Control getControl();

}
