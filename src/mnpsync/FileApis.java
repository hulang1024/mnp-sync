package mnpsync;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.List;
import java.util.Map;

/**
 * 请求文件
 */
public class FileApis {
    private static CloseableHttpClient httpClient = HttpClients.createDefault();
    private static String baseUrl;

    public static void init() {
        baseUrl = Config.c.getString("file_api.base_url");
    }

    public static Integer[] pullFileBytes(String filename) {
        try {
            Map<String, Object> ret = httpGetMap("/pullFile?param=1&file_name=" + filename);
            if ((Integer)ret.get("status") != 0) {
                return null;
            }

            JSONArray buffer = (JSONArray)ret.get("buffer");
            return buffer.toArray(new Integer[buffer.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> pullFileNameList() {
        try {
            Map<String, Object> ret = httpGetMap("/pullFile?param=0");
            if ((Integer)ret.get("status") != 0) {
                return null;
            }
            return ((JSONArray)ret.get("fileList")).toJavaList(String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Object> httpGetMap(String interfaceUrl) throws Exception {
        HttpGet httpGet = new HttpGet(baseUrl + interfaceUrl);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String json = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        return JSONObject.parseObject(json, Map.class);
    }
}
