package sample.scijoker.com.urclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.scijoker.urclient.ErrorHandlerImpl;
import com.scijoker.urclient.OnCancelListener;
import com.scijoker.urclient.OnResponseListener;
import com.scijoker.urclient.Response;
import com.scijoker.urclient.URClient;

import java.util.Map;

public class DemoActivity extends AppCompatActivity {
    private Button btnAuth, btnCancel;
    private WebView webView;
    private URClient.Builder authRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        initUI();
        initListeners();
    }

    private void initUI() {
        btnAuth = (Button) findViewById(R.id.btn_auth);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        webView = (WebView) findViewById(R.id.webview);
    }

    private void initListeners() {
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authRequest = Requestor.makeAuthorization(onAuthResponseListener, onCancelListener);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authRequest != null) {
                    authRequest.cancel();
                }
            }
        });
    }

    private OnResponseListener onAuthResponseListener = new OnResponseListener() {
        @Override
        public void onResponseSuccessful(Response response) {
            if (response.getResponseHeaders() != null) {
                for (Map.Entry<String, String> stringStringEntry : response.getResponseHeaders().entrySet()) {
                    Log.d("onResponseSuccessful", "header: " + stringStringEntry.getValue());
                }
            }
            btnAuth.setVisibility(View.GONE);
            webView.loadDataWithBaseURL("", (String) response.getResponseObj(), "", "utf-8", "");
        }

        @Override
        public void onResponseFailed(int errorCode, String exceptionInfo) {
            webView.loadDataWithBaseURL("", "Error", "", "utf-8", "");
        }
    };
    private OnCancelListener onCancelListener = new OnCancelListener() {
        @Override
        public void onResponseCancelListener() {
            Toast.makeText(getApplicationContext(), "Request canceled", Toast.LENGTH_LONG).show();
        }
    };

    private static void makePostRequest(Class returnObject, Object body, String url, OnResponseListener onResponseListener, ErrorHandlerImpl handler) {
        URClient.create()
                .body(body)
                .responseListener(onResponseListener)
                .errorHandler(handler)
                .send(url, URClient.METHOD.POST, returnObject);
    }
}
