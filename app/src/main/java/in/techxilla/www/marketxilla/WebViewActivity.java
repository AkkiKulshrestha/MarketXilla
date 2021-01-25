package in.techxilla.www.marketxilla;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Picture;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

public class WebViewActivity extends AppCompatActivity {
    TextView tv_title;
    ImageView iv_back,img_download;
    private WebView webView;
    ViewGroup viewGroup;
    String path_link,fileName,mPdf_name, date = "";
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if((getIntent().getStringExtra("path_link"))!=null){
            path_link = getIntent().getStringExtra("path_link");
        }
        viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.back_btn_toolbar);
        img_download =(ImageView) findViewById(R.id.img_download);
        img_download.setVisibility(View.VISIBLE);
        tv_title.setText("Transaction Details");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initFragment();
        img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WebViewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }

                if (ActivityCompat.checkSelfPermission(WebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(WebViewActivity.this, "No Permission", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        new DownloadAsync().execute(path_link);
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
        private void initFragment() {
            webView = findViewById(R.id.webView);

            webView.setWebViewClient(new MyBrowser());
            open();
        }

        public void open() {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                    webView.getSettings().setLoadsImagesAutomatically(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    webView.getSettings().setUseWideViewPort(true);
                    webView.getSettings().setLoadWithOverviewMode(true);
                    webView.setInitialScale(1);
                    webView.getSettings().setBuiltInZoomControls(true);
                    webView.getSettings().setDisplayZoomControls(false);

                  System.out.println("path link"+path_link);
                if (path_link.contains("pdf")) {
                     webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + path_link);
                }else {
                    webView.loadUrl(path_link);
                }


            }
        }

        private class MyBrowser extends WebViewClient {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (this != null) {
                }
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    System.out.println("on shouldOverrideUrlLoading");
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                System.out.println("on onPageFinished");
                webView.scrollTo(0,0);
                Picture picture = webView.capturePicture();
                System.out.println("on picture" +picture);
                if (this != null) {
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                System.out.println("on onReceivedError");
                // webView.clearCache(true);
                webView.loadUrl("about:blank");
                //webView.setVisibility(View.GONE);
            }
        }

    public class DownloadAsync extends AsyncTask<String, Integer, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            CommonMethods.DisplayToast(WebViewActivity.this,"Start download");
            // pogressDialog();
        }

        @Override
        protected String doInBackground(String... url) {


            File mydir =  getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (!mydir.exists()) {
                mydir.mkdirs();
            }
            try
            {
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                String url_link = url[0];
                Uri downloadUri = Uri.parse(url_link);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(String.valueOf(downloadUri));
                // concatinate above fileExtension to fileName
                fileName = "." + fileExtension;
                Log.e("File Extension",""+fileName);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date= dateFormat.format(new Date());

                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("Downloading")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        //.setMimeType(fileExtension)
                        .setDestinationInExternalPublicDir("/MarketXill", date+ fileName);

                manager.enqueue(request);
            }catch(RuntimeException e){
                Log.d("SubscriptionPlan",e.getMessage());
            }
            return mydir.getPath() + File.separator + date +fileName;

        }

        @Override
        public void onPostExecute(String s) {
            super .onPostExecute(s);
            CommonMethods.DisplayToast(WebViewActivity.this,"Successfully completed download. ");

        }
    }

}