package sample.scijoker.com.urclient;

import com.scijoker.urclient.OnResponseListener;
import com.scijoker.urclient.URClient;

import sample.scijoker.com.urclient.imgur_service.ImgurApiHelper;

/**
 * Created by scijoker on 06.06.15.
 */
public class Requestor {
    public static void makeAuthorization(OnResponseListener onResponseListener) {
        URClient.createRequest()
                .setOnResponseListener(onResponseListener)
                .saveOnDevice("password_for_encode_your_response_on_device")
                .sendGET(ImgurApiHelper.AUTHORIZATION, String.class);
    }
}
