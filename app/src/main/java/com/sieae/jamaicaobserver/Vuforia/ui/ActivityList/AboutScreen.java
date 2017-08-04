/*===============================================================================
Copyright (c) 2015-2016 PTC Inc. All Rights Reserved.


Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.sieae.jamaicaobserver.Vuforia.ui.ActivityList;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sieae.jamaicaobserver.Vuforia.app.ARVR.APP_MODE;
import com.sieae.jamaicaobserver.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sieae.jamaicaobserver.GlobalVariables.ABOUT_TEXT;
import static com.sieae.jamaicaobserver.GlobalVariables.ABOUT_TEXT_TITLE;
import static com.sieae.jamaicaobserver.GlobalVariables.ACTIVITY_TO_LAUNCH;


public class AboutScreen extends Activity implements OnClickListener
{
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 78;
    private static final String LOGTAG = "AboutScreen";
    
    WebView mAboutWebText;
    Button mARDeviceStartButton;
    Button mARViewerStartButton;
    Button mVRDeviceStartButton;
    Button mVRViewerStartButton;
    TextView mAboutTextTitle;
    String mClassToLaunch;
    String mClassToLaunchPackage;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.about_screen);
//        setPermission();
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
        
        mARDeviceStartButton = (Button) findViewById(R.id.button_start_device_ar);
        mARDeviceStartButton.setOnClickListener(this);

        mARViewerStartButton = (Button) findViewById(R.id.button_start_viewer_ar);
        mARViewerStartButton.setOnClickListener(this);

        mVRDeviceStartButton = (Button) findViewById(R.id.button_start_device_vr);
        mVRDeviceStartButton.setOnClickListener(this);

        mVRViewerStartButton = (Button) findViewById(R.id.button_start_viewer_vr);
        mVRViewerStartButton.setOnClickListener(this);

        mAboutTextTitle = (TextView) findViewById(R.id.about_text_title);
        mAboutTextTitle.setText(extras.getString(ABOUT_TEXT_TITLE));
        
    }
    
    
    // Starts the chosen activity
    private void startARActivity(APP_MODE mode)
    {
        Intent i = new Intent();
        i.setClassName(mClassToLaunchPackage, mClassToLaunch);
        i.putExtra("APP_MODE", mode);
        startActivity(i);
    }
    
    
    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.button_start_device_ar:
                startARActivity(APP_MODE.HANDHELD_AR);
                break;

            case R.id.button_start_viewer_ar:
                startARActivity(APP_MODE.VIEWER_AR);
                break;

            case R.id.button_start_device_vr:
                startARActivity(APP_MODE.HANDHELD_VR);
                break;

            case R.id.button_start_viewer_vr:
                startARActivity(APP_MODE.VIEWER_VR);
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

//    private void setPermission() {
//        List<String> permissionsNeeded = new ArrayList<String>();
//
//        final List<String> permissionsList = new ArrayList<String>();
//        if (!addPermission(permissionsList, Manifest.permission.INTERNET))
//            permissionsNeeded.add("Internet");
//        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
//            permissionsNeeded.add("NETWORK_STATE");
//        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
//            permissionsNeeded.add("Storage");
//        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
//            permissionsNeeded.add("Storage");
//        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
//            permissionsNeeded.add("Camera");
//
//
//        if (permissionsList.size() > 0) {
//            if (permissionsNeeded.size() > 0) {
//                // Need Rationale
//                String message = "You need to grant access to " + permissionsNeeded.get(0);
//                for (int i = 1; i < permissionsNeeded.size(); i++)
//                    message = message + ", " + permissionsNeeded.get(i);
//                showMessageOKCancel(message,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ActivityCompat.requestPermissions(AboutScreen.this, permissionsList.toArray(new String[permissionsList.size()]),
//                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//                            }
//                        });
//                return;
//            }
//            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),
//                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//            return;
//        }
//
////        initStorage();
//    }
//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(AboutScreen.this)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }
//
//    private boolean addPermission(List<String> permissionsList, String permission) {
//        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//            permissionsList.add(permission);
//            // Check for Rationale Option
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
//                return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//
//            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
//            {
//                Map<String, Integer> perms = new HashMap<String, Integer>();
//                // Initial
//                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
//
//
//                // Fill with results
//                for (int i = 0; i < permissions.length; i++)
//                    perms.put(permissions[i], grantResults[i]);
//                // Check for ACCESS_FINE_LOCATION
//                if (perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
//                         ) {
//                    // All Permissions Granted
////                    initStorage();
//
//
//                } else {
//                    // Permission Denied
//                    Toast.makeText(AboutScreen.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//            break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
}
