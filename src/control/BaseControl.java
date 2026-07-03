package control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * @author: 向珅葳
 * @date : 2026/5/29
 * @time : 12:03
 * @project_name : txtReader
 */
abstract public class BaseControl<T> implements Control<T> {
    public T inputData;
    protected Logger logger;
    protected ControlEnum targetEnum;

    protected static String OUTPUT_TEXT_SERVERERROR="未知异常,请重试！";
    protected static String OUTPUT_TEXT_INVALID_INPUT="你的输入有问题，请重新输入";
    protected static int STOP_MUSIC = -2;
    protected static String Wait="暂未实现";
    protected static final String SESSION_BUG = "你遇到了一个未知会话bug！请重新登录";
    protected static Scanner input = new Scanner(System.in);

    public BaseControl(){
        logger = LogManager.getLogger(getClass());
    }

    public T getInputData() {
        return inputData;
    }

    @Override
    public void setInputData(T inputData) {
        this.inputData = inputData;
    }

    public ControlEnum getTargetEnum() {
        return targetEnum;
    }

    public void setTargetEnum(ControlEnum targetEnum) {
        this.targetEnum = targetEnum;
    }
    protected int readInt() {
        while (true) {
            String line = input.nextLine().trim();
            if (line.isEmpty()) {
                logger.info(OUTPUT_TEXT_INVALID_INPUT);
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                logger.info(OUTPUT_TEXT_INVALID_INPUT);
            }
        }
    }

    protected double readDouble() {
        while (true) {
            String line = input.nextLine().trim();
            if (line.isEmpty()) {
                logger.info(OUTPUT_TEXT_INVALID_INPUT);
                continue;
            }
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                logger.info(OUTPUT_TEXT_INVALID_INPUT);
            }
        }
    }

    protected String readFilePath() {
        String line = input.nextLine().trim();
        while (line.isEmpty()) {
            logger.info("路径不能为空，请重新输入：");
            line = input.nextLine().trim();
        }
        return line;
    }
}
