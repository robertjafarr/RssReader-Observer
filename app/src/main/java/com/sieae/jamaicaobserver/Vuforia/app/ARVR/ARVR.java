/*===============================================================================
Copyright (c) 2016 PTC, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.sieae.jamaicaobserver.Vuforia.app.ARVR;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sieae.jamaicaobserver.R;
import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.Device;
import com.vuforia.DeviceTracker;
import com.vuforia.HandheldTransformModel;
import com.vuforia.HeadTransformModel;
import com.vuforia.ObjectTracker;
import com.vuforia.RotationalDeviceTracker;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.TransformModel;
import com.vuforia.Vuforia;
import com.sieae.jamaicaobserver.SampleApplication.SampleApplicationControl;
import com.sieae.jamaicaobserver.SampleApplication.SampleApplicationException;
import com.sieae.jamaicaobserver.SampleApplication.SampleApplicationSession;
import com.sieae.jamaicaobserver.SampleApplication.utils.LoadingDialogHandler;
import com.sieae.jamaicaobserver.SampleApplication.utils.SampleApplicationGLView;
import com.sieae.jamaicaobserver.SampleApplication.utils.Texture;

import java.util.Vector;


public class ARVR extends Activity implements SampleApplicationControl
{
    private static final String LOGTAG = "ARVR";
    
    SampleApplicationSession vuforiaAppSession;

    private DataSet mDataset;

    // Our OpenGL view:
    private SampleApplicationGLView mGlView;
    
    // Our renderer:
    private ARVRRenderer mRenderer;
    
    private GestureDetector mGestureDetector;
    
    // The textures we will use for rendering:
    private Vector<Texture> mTextures;

    private RelativeLayout mUILayout;
    
    LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);

    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;

    private static boolean OPTIMIZE_VR = true;

    boolean mIsStereo = false;
    boolean mIsVR = false;
    
    // Called when the activity first starts or the user navigates back to an
    // activity.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        vuforiaAppSession = new SampleApplicationSession(this);
        
        startLoadingAnimation();

        vuforiaAppSession
            .initAR(this, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        mGestureDetector = new GestureDetector(this, new GestureListener());
        
        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();
        
    }
    
    public void onWindowFocusChanged(boolean hasFocus)
    {
        // Hide the Android navigation bar (if the device has one), as it gets in the way in stereo mode
        goFullScreen();
    }

    private void goFullScreen()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    // Process Single Tap event to trigger autofocus
    private class GestureListener extends
        GestureDetector.SimpleOnGestureListener
    {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();
        
        
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }
        
        
        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            // Generates a Handler to trigger autofocus
            // after 1 second
            autofocusHandler.postDelayed(new Runnable()
            {
                public void run()
                {
                    boolean result = CameraDevice.getInstance().setFocusMode(
                        CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
                    
                    if (!result)
                        Log.e("SingleTapUp", "Unable to trigger focus");
                }
            }, 1000L);
            
            return true;
        }
    }
    
    
    // We want to load specific textures from the APK, which we will later use
    // for rendering.
    
    private void loadTextures()
    {
    }
    
    
    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume()
    {
        Log.d(LOGTAG, "onResume");
        super.onResume();
        
        try
        {
            vuforiaAppSession.resumeAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        // Resume the GL view:
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
        
    }
    
    
    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(Configuration config)
    {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);
        
        vuforiaAppSession.onConfigurationChanged();
    }
    
    
    // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause()
    {
        Log.d(LOGTAG, "onPause");
        super.onPause();
        
        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
        
        try
        {
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
    }
    
    
    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();
        
        try
        {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        // Unload texture:
        mTextures.clear();
        mTextures = null;
        
        System.gc();
    }
    
    
    // Initializes AR application components.
    private void initApplicationAR()
    {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();
        
        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        Bundle extras = getIntent().getExtras();
        APP_MODE appMode = (APP_MODE) extras.get("APP_MODE");

        mIsStereo = false;
        if(appMode == APP_MODE.VIEWER_AR || appMode == APP_MODE.VIEWER_VR)
            mIsStereo = true;

        int deviceMode = Device.MODE.MODE_AR;
        if(appMode == APP_MODE.HANDHELD_VR || appMode == APP_MODE.VIEWER_VR)
        {
            deviceMode = Device.MODE.MODE_VR;
            mIsVR = true;
        } else {
            mIsVR = false;
        }

        Device device = Device.getInstance();
        device.setViewerActive(mIsStereo); // Indicates if the app will be using a viewer, stereo mode and initializes the rendering primitives
        device.setMode(deviceMode); // Select if we will be in AR or VR mode

        mRenderer = new ARVRRenderer(this, vuforiaAppSession, appMode);
        mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);
        mGlView.setPreserveEGLContextOnPause(true);
        
    }
    
    
    private void startLoadingAnimation()
    {
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay,
            null);
        
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);
        
        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUILayout
            .findViewById(R.id.loading_indicator);
        
        // Shows the loading indicator at start
        loadingDialogHandler
            .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
        
        // Adds the inflated layout to the view
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        
    }
    
    
    // Methods to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData()
    {
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
            .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;
        
        if (mDataset == null)
            mDataset = objectTracker.createDataSet();
        
        if (mDataset == null)
            return false;
        
        if (!mDataset.load("Stones.xml", STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;
        
        if (!objectTracker.activateDataSet(mDataset))
            return false;
        
        int numTrackables = mDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++)
        {
            Trackable trackable = mDataset.getTrackable(count);

            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data "
                + (String) trackable.getUserData());
        }
        
        return true;
    }
    
    
    @Override
    public boolean doUnloadTrackersData()
    {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
            .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;
        
        if (mDataset != null && mDataset.isActive())
        {
            if (objectTracker.getActiveDataSet().equals(mDataset)
                && !objectTracker.deactivateDataSet(mDataset))
            {
                result = false;
            } else if (!objectTracker.destroyDataSet(mDataset))
            {
                result = false;
            }
            
            mDataset = null;
        }
        
        return result;
    }
    
    
    @Override
    public void onInitARDone(SampleApplicationException exception)
    {
        
        if (exception == null)
        {
            initApplicationAR();
            
            mRenderer.mIsActive = true;
            
            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
            
            // Sets the UILayout to be drawn in front of the camera
            mUILayout.bringToFront();
            
            // Sets the layout background to transparent
            mUILayout.setBackgroundColor(Color.TRANSPARENT);
            
            try
            {
                vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
            } catch (SampleApplicationException e)
            {
                Log.e(LOGTAG, e.getString());
            }
            
            boolean result = CameraDevice.getInstance().setFocusMode(
                CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

            if(mIsVR && OPTIMIZE_VR)
            {
                CameraDevice.getInstance().stop(); // In VR we don't need the camera feed since we are not tracking objects
            }
            
        } else
        {
            Log.e(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
    }
    
    
    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message)
    {
        final String errorMessage = message;
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (mErrorDialog != null)
                {
                    mErrorDialog.dismiss();
                }
                
                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                    ARVR.this);
                builder
                    .setMessage(errorMessage)
                    .setTitle(getString(R.string.INIT_ERROR))
                    .setCancelable(false)
                    .setIcon(0)
                    .setPositiveButton(getString(R.string.button_OK),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();
                            }
                        });
                
                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }
    
    
    @Override
    public void onVuforiaUpdate(State state)
    {
    }
    
    
    @Override
    public boolean doInitTrackers()
    {
        // Indicate if the trackers were initialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();

        // Trying to initialize the image tracker
        Tracker objectTracker = tManager.initTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
        {
            Log.e(
                LOGTAG,
                "Object tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else
        {
            Log.i(LOGTAG, "Object tracker successfully initialized");
        }

        RotationalDeviceTracker deviceTracker = (RotationalDeviceTracker) tManager.initTracker(RotationalDeviceTracker.getClassType());
        if (deviceTracker == null)
        {
            Log.e(
                    LOGTAG,
                    "Rotational Device Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        }
        else
        {
            // Set correction model to head if using viewer otherwise handheld
            if (mIsStereo)
                deviceTracker.setModelCorrection(deviceTracker.getDefaultHeadModel());
            else
                deviceTracker.setModelCorrection(deviceTracker.getDefaultHandheldModel());

            TransformModel model = deviceTracker.getModelCorrection();
            if (model == null)
            {
                Log.e(LOGTAG,"Error no transform model");
            }
            else
            {
                // We check which transform model is set for the rotation correction and then we get the pivot used to apply the correction when the device rotates. This is done so we know the model was set properly.

                // This transform model is set when there is a viewer present, we use a head model for this correction
                if (model.getType() == TransformModel.TYPE.TRANSFORM_MODEL_HEAD)
                {
                    Log.i(LOGTAG, "Transform model: Head");
                    HeadTransformModel headModel = (HeadTransformModel) model;
                    Log.i(LOGTAG,"Transform model pivot: " + headModel.getPivotPoint().getData()[0] +","+ headModel.getPivotPoint().getData()[1] +","+ headModel.getPivotPoint().getData()[2] );
                }

                // In this mode we are in full screen mode and the user is holding the device with the hands so we apply a handheld model
                if (model.getType() == TransformModel.TYPE.TRANSFORM_MODEL_HANDHELD)
                {
                    Log.i(LOGTAG, "Transform model: Handheld");
                    HandheldTransformModel handheldModel = (HandheldTransformModel) model;
                    Log.i(LOGTAG,"Transform model pivot: " + handheldModel.getPivotPoint().getData()[0] +","+ handheldModel.getPivotPoint().getData()[1] +","+ handheldModel.getPivotPoint().getData()[2] );
                }
            }

            // Enable pose prediction if in VR mode
            deviceTracker.setPosePrediction(mIsVR);
            Log.i(LOGTAG, "Rotational Device Tracker successfully initialized");
        }
        return result;
    }
    
    
    @Override
    public boolean doStartTrackers()
    {
        // Indicate if the trackers were started correctly
        boolean result = true;

        // We do not start the object tracker if in VR mode since we do not have interaction with any target in the VR scene, this way we save up some processing time
        if(!mIsVR || !OPTIMIZE_VR)
        {
            Tracker objectTracker = TrackerManager.getInstance().getTracker(
                    ObjectTracker.getClassType());
            if (objectTracker != null)
                objectTracker.start();
        }

        // If in VR mode we start the rotational tracker to get the rotation pose in the renderer
        if(mIsVR)
        {
            Tracker deviceTracker = TrackerManager.getInstance().getTracker(
                    RotationalDeviceTracker.getClassType());
            if (deviceTracker != null) {
                if (deviceTracker.start()) {
                    Log.d(LOGTAG, "Successfully started Device Tracker.");
                } else {
                    Log.d(LOGTAG, "Failed to start Device Tracker.");
                }
            }
        }
        
        return result;
    }
    
    
    @Override
    public boolean doStopTrackers()
    {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        
        Tracker objectTracker = TrackerManager.getInstance().getTracker(
            ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();

        Tracker deviceTracker = TrackerManager.getInstance().getTracker(
                DeviceTracker.getClassType());
        if (deviceTracker != null)
            deviceTracker.stop();

        return result;
    }
    
    
    @Override
    public boolean doDeinitTrackers()
    {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        tManager.deinitTracker(DeviceTracker.getClassType());
        
        return result;
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mGestureDetector.onTouchEvent(event);
    }

    private void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
