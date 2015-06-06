package sample.scijoker.com.urclient.imgur_service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by scijoker on 06.06.15.
 */
public class ImgurApiHelper {
    public static String CLIENT_ID = "c297eecccbcdfe7";
    public static String CLIENT_SECRED = "93c865365ae02f31f7cf79dfa8d92788c1d5e3bc";
    //you can save ACCESS_TOKEN from headers (Auth response)
    public static String ACCESS_TOKEN;

    public static Map<String, String> getImgurRequestHeaders() {
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", CLIENT_ID);
        return map;
    }

    public static String AUTHORIZATION = "https://api.imgur.com/oauth2/authorize?client_id=" + CLIENT_ID + "&response_type=REQUESTED_RESPONSE_TYPE&state=APPLICATION_STATE";
}
