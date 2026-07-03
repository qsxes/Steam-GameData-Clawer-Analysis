package control;

/**
 * @author: 向珅葳
 * @date : 2026/5/29
 * @time : 12:00
 * @project_name : txtReader
 */
public interface Control<T> {
    Control execute();
    void setInputData(T inputData);
}
