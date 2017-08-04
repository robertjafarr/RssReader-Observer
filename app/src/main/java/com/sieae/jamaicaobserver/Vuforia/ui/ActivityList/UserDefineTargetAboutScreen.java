package com.sieae.jamaicaobserver.Vuforia.ui.ActivityList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.sieae.jamaicaobserver.R;

import static com.sieae.jamaicaobserver.GlobalVariables.ABOUT_TEXT;
import static com.sieae.jamaicaobserver.GlobalVariables.ABOUT_TEXT_TITLE;
import static com.sieae.jamaicaobserver.GlobalVariables.ACTIVITY_TO_LAUNCH;
import static com.sieae.jamaicaobserver.GlobalVariables.CLASS_TO_LAUNCH;
import static com.sieae.jamaicaobserver.GlobalVariables.CLASS_TO_LAUNCH_PKG;

/**
 * Created by Mohammad Arshi Khan on 02-11-2016.
 */

public class UserDefineTargetAboutScreen extends Activity implements View.OnClickListener
{
    private static final String LOGTAG = "AboutScreen";

    private WebView mAboutWebText;
    private Button mStartButton;
    private TextView mAboutTextTitle;
    private String mClassToLaunch;
    private String mClassToLaunchPackage;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.userdefine_about_screen);

        Bundle extras = getIntent().getExtras();
        String webText = extras.getString(ABOUT_TEXT);
        mClassToLaunchPackage = getPackageName();
        mClassToLaunch = mClassToLaunchPackage + "."
                + extras.getString(ACTIVITY_TO_LAUNCH);

        mAboutWebText = (WebView) findViewById(R.id.about_html_text);

        AboutWebViewClient aboutWebClient = new AboutWebViewClient();
        mAboutWebText.setWebViewClient(aboutWebClient);

        String aboutText = "";
        try
        {
            InputStream is = getAssets().open(webText);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null)
            {
                aboutText += line;
            }
        } catch (IOException e)
        {
            Log.e(LOGTAG, "About html loading failed");
        }

        mAboutWebText.loadData(aboutText, "text/html", "UTF-8");

        mStartButton = (Button) findViewById(R.id.button_start);
        mStartButton.setOnClickListener(this);

        mAboutTextTitle = (TextView) findViewById(R.id.about_text_title);
        mAboutTextTitle.setText(extras.getString(ABOUT_TEXT_TITLE));

    }


    // Starts the chosen activity
    private void startARActivity()
    {
//        Intent intent = new Intent(UserDefineTargetAboutScreen.this,
//                ProductListActivity.class);
//        intent.putExtra(CLASS_TO_LAUNCH,mClassToLaunch);
//        intent.putExtra(CLASS_TO_LAUNCH_PKG, mClassToLaunchPackage);
//        startActivity(intent);

        Intent i = new Intent();
        i.setClassName(mClassToLaunchPackage, mClassToLaunch);
        startActivity(i);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_start:
                startARActivity();
                break;
        }
    }

    private class AboutWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}
