package util;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author: 向珅葳
 * @date : 2026/6/26
 * @time : 8:21
 * @project_name : SteamSQL
 */
public class HttpUtil {
    public static String get(String urlString) throws Exception{

        //创建网址对象获取发送请求的地址
        URL url = new URL(urlString);

        //创建http连接对象，通过网址获取http连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        //设置超时时间
        connection.setConnectTimeout(10000);//建立连接超时
        connection.setReadTimeout(10000);//获取信息超时

        //配置翻墙代理
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7897");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7897");

        //设置请求头
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("Referer", "https://steamdb.info/");  // 来源页

        //获取请求结果码并判断
        int responseCode = connection.getResponseCode();
        System.out.println("状态码"+responseCode);
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP 请求失败，状态码: " + responseCode);
        }

        //创建读取对象，将连接获得的steam流放入bufferReader中等待读取
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        //准备总体的请求结果字符串与读取行
        StringBuffer response = new StringBuffer();
        String line;

        //当行不为空时，将读取行拼接到请求结果字符串中
        while ((line=reader.readLine())!=null){
            response.append(line);
        }

        //关闭读取对象
        reader.close();

        //关闭连接
        connection.disconnect();

        //返回请求结果字符串
        return response.toString();
    }
}
