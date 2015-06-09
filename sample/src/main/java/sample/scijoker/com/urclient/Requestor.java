package sample.scijoker.com.urclient;

import com.scijoker.urclient.OnCancelListener;
import com.scijoker.urclient.OnResponseListener;
import com.scijoker.urclient.URClient;

import sample.scijoker.com.urclient.imgur_service.ImgurApiHelper;

/**
 * Created by scijoker on 06.06.15.
 */
public class Requestor {
    @Deprecated
    public static URClient.Builder makeAuthorization(OnResponseListener onResponseListener, OnCancelListener onCancelListener) {
        return authorizate(onResponseListener, onCancelListener);
    }

    public static URClient.Builder authorizate(OnResponseListener onResponseListener, OnCancelListener onCancelListener) {
        return URClient.create()
                .responseListener(onResponseListener)
                .cancelListener(onCancelListener)
                .send(ImgurApiHelper.AUTHORIZATION, URClient.METHOD.GET, String.class);
    }

}
