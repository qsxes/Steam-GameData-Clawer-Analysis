import control.Control;
import control.StartControl;
import control.select.SelectGameByTop10;

/**
 * @author: 向珅葳
 * @date : 2026/7/1
 * @time : 12:01
 * @project_name : SteamSQL
 */
public class FullTest {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.stdout.encoding", "UTF-8");
        System.setProperty("sun.stderr.encoding", "UTF-8");
        Control control = new StartControl();
        while (control!=null){
            control = control.execute();
        }
    }

    public static void run(){
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.stdout.encoding", "UTF-8");
        System.setProperty("sun.stderr.encoding", "UTF-8");
        Control control = new StartControl();
        while (control!=null){
            control = control.execute();
        }
    }
}
