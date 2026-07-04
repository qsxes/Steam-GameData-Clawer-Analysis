package dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

/**
 * @author: 向珅葳
 * @date : 2026/6/15
 * @time : 17:09
 * @project_name : JDBC
 */
public class BaseDao {
    private static String driver;
    private static String username;
    private static String password;
    private static String url;
    private static Properties params;
    private static Connection conn = null;

    protected static Logger logger = LogManager.getLogger(BaseDao.class.getName());
    static {
        init();
    }

    public static void init() {
        params = new Properties();
        String configFile = "steamApi.properties";
        InputStream is = null;

        try {
            // 1.尝试外部文件
            File externalFile = new File(configFile);
            if (externalFile.exists()) {
                is = new FileInputStream(externalFile);
                System.out.println("从外部文件加载配置: " + externalFile.getAbsolutePath());
            } else {
                // 2.尝试从类路径加载
                is = BaseDao.class.getClassLoader().getResourceAsStream(configFile);
                if (is == null) {
                    throw new RuntimeException("配置文件 steamApi.properties 未找到");
                }
                System.out.println("从类路径加载配置");
            }

            // 3.加载属性
            params.load(is);
            driver = params.getProperty("driver");
            url = params.getProperty("url");
            username = params.getProperty("user");
            password = params.getProperty("password");

            // 4.验证必要配置是否加载成功
            if (driver == null || url == null || username == null || password == null) {
                throw new RuntimeException("配置文件缺少必要字段：driver, url, user, password");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("外部配置文件存在但无法读取", e);
        } catch (IOException e) {
            throw new RuntimeException("加载配置文件失败", e);
        } finally {
            //确保输入流被关闭
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                    //关闭失败不影响程序运行
                }
            }
        }
    }

    public static boolean is_init(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnectionForCheck();
            if (conn == null) {
                System.err.println("数据库连接失败，请检查 steamApi.properties 中的 user 和 password。");
                return false;
            }
            pstmt = conn.prepareStatement(getProperty("isInit"));
            rs = pstmt.executeQuery();
            if (rs.next()){
                int is_init = rs.getInt("is_init");
                if (is_init==1){
                    return true;
                }else {
                    return false;
                }
            }else{
                return false;
            }
        } catch (SQLException e) {
            return false;
        }catch (Exception e) {
            return false;
        }finally {
            closeAll(conn,null,pstmt,rs);
        }
    }

    public static int init_sql(){

        int count = 0;
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = getConnectionForCheck();
            InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("sql/init.sql");
            if (is == null){
                throw new RuntimeException("未找到初始化sql文件，请检查init.sql是否存在于sql目录下");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuffer sqlBuffer = new StringBuffer();
            String line="";
            String sqlStr = "";
            while ((line=reader.readLine())!=null){
                sqlBuffer.append(line).append("\n");
            }
            sqlStr = sqlBuffer.toString();
            String[] sqls = sqlStr.split(";");
            for (String sql : sqls){
                String exeSql = sql.trim();
                if (exeSql.isEmpty()){
                    continue;
                }
                pstmt = conn.prepareStatement(exeSql);
                pstmt.execute();
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException("读取初始化脚本失败",e);
        } catch (SQLException e) {
            throw new RuntimeException("执行初始化脚本失败",e);
        } finally {
            closeAll(conn,null,pstmt,null);
        }
        return count;
    }

    public static String getProperty(String key){
        return params.getProperty(key);
    }

    public static Connection getConn(){
        try {
            if (conn==null || conn.isClosed()){
                Class.forName(driver);
                conn = DriverManager.getConnection(url,username,password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeAll(Connection conn, Statement stmt,PreparedStatement pstmt, ResultSet rs){
        try {
            if (rs !=null){
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (stmt !=null){
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null){
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (conn !=null){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnectionForCheck() {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(getProperty("checkUrl"), username, password);
        } catch (Exception e) {
            return null;
        }
    }
}
