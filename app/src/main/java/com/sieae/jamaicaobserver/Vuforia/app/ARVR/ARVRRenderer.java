/*===============================================================================
Copyright (c) 2016 PTC, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.sieae.jamaicaobserver.Vuforia.app.ARVR;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.vuforia.COORDINATE_SYSTEM_TYPE;
import com.vuforia.CameraDevice;
import com.vuforia.Device;
import com.vuforia.DeviceTrackableResult;
import com.vuforia.GLTextureUnit;
import com.vuforia.ImageTargetResult;
import com.vuforia.Matrix34F;
import com.vuforia.Matrix44F;
import com.vuforia.Mesh;
import com.vuforia.Renderer;
import com.vuforia.RenderingPrimitives;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.TrackerManager;
import com.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.vuforia.VIEW;
import com.vuforia.Vec2F;
import com.vuforia.Vec2I;
import com.vuforia.Vec4F;
import com.vuforia.Vec4I;
import com.vuforia.ViewList;
import com.vuforia.Vuforia;
import com.sieae.jamaicaobserver.SampleApplication.SampleApplicationSession;
import com.sieae.jamaicaobserver.SampleApplication.utils.BackgroundShader;
import com.sieae.jamaicaobserver.SampleApplication.utils.CubeShaders;
import com.sieae.jamaicaobserver.SampleApplication.utils.DistoShaders;
import com.sieae.jamaicaobserver.SampleApplication.utils.LoadingDialogHandler;
import com.sieae.jamaicaobserver.SampleApplication.utils.SampleApplicationV3DModel;
import com.sieae.jamaicaobserver.SampleApplication.utils.SampleMath;
import com.sieae.jamaicaobserver.SampleApplication.utils.SampleUtils;
import com.sieae.jamaicaobserver.SampleApplication.utils.Texture;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


// The renderer class for the ARVR sample.
public class ARVRRenderer implements GLSurfaceView.Renderer
{
    private static final String LOGTAG = "ARVRRenderer";

    private SampleApplicationSession vuforiaAppSession;
    private ARVR mActivity;
    private RenderingPrimitives renderingPrimitives;

    private Vector<Texture> mTextures;

    int shaderProgramID;

    // Textures to render the video and augmentation properly in the FBO
    Vec2I viewerDistortionTextureSize = new Vec2I(0,0);
    private int viewerDistortionColourID[] = new int[1];
    private int viewerDistortionDepthID[]  = new int[1];

    // FBO to render off screen for the stereo mode
    private int viewerDistortionFboID[] = new int[1];

    // Shader used to render the distortion for stereo mode
    private int distoShaderProgramID = 0;
    private int distoVertexHandle         = 0;
    private int distoTexCoordHandle       = 0;
    private int distoTexSampler2DHandle   = 0;

    // Shader user to render the video background on AR mode
    private int vbShaderProgramID;
    private int vbTexSampler2DHandle;
    private int vbVertexHandle            = 0;
    private int vbTexCoordHandle          = 0;
    private int vbProjectionMatrixHandle;

    private SampleApplicationV3DModel mMountainModelVR;
    private SampleApplicationV3DModel mMountainModelAR;

    private Renderer mRenderer;

    private boolean isARObjectVisible = false;

    boolean mIsActive = false;

    boolean mIsVR = false;

    // View matrix corresponding to device pose in the virtual world:
    Matrix34F deviceViewMatrix;

    // View matrix for the interaction target, stones in this sample
    Matrix44F interactionViewMatrix;

    // Sky color of the VR world
    float skyColor[] = {0.4f, 0.5f, 0.6f, 1.0f};

    private static final float AR_OBJECT_SCALE_FLOAT = 0.025f;


    public ARVRRenderer(ARVR activity,
        SampleApplicationSession session, APP_MODE appMode)
    {
        mActivity = activity;
        vuforiaAppSession = session;

        interactionViewMatrix = SampleMath.Matrix44FIdentity();
        deviceViewMatrix = new Matrix34F();

        if(appMode == APP_MODE.VIEWER_VR || appMode == APP_MODE.HANDHELD_VR) {
            mIsVR = true;
        }
    }


    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;

        // Call our function to render content
        renderFrame();
    }


    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

        initRendering();

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();
    }


    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);

        // Update the rendering primitives used to draw on the display according to the new surface size
        updateRenderingPrimitives();
    }


    public synchronized void updateRenderingPrimitives()
    {
        renderingPrimitives = Device.getInstance().getRenderingPrimitives();
    }


    // Function for initializing the renderer.
    private void initRendering()
    {
        mRenderer = Renderer.getInstance();

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);

        for (Texture t : mTextures)
        {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE, t.mData);
        }

        shaderProgramID = SampleUtils.createProgramFromShaderSrc(
                CubeShaders.CUBE_MESH_VERTEX_SHADER,
                CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

        vbShaderProgramID = SampleUtils.createProgramFromShaderSrc(BackgroundShader.VB_VERTEX_SHADER,
                BackgroundShader.VB_FRAGMENT_SHADER);

        // create textureID for viewer distortion
        GLES20.glGenTextures(1, viewerDistortionColourID, 0);
        GLES20.glGenTextures(1, viewerDistortionDepthID, 0);

        // create FBO ID for viewer distortion
        GLES20.glGenFramebuffers(1, viewerDistortionFboID, 0);

        distoShaderProgramID = SampleUtils.createProgramFromShaderSrc(DistoShaders.DISTORTION_VERTEX_SHADER,
                DistoShaders.DISTORTION_FRAGMENT_SHADER);

        // Rendering configuration for the stereo distortion
        if(distoShaderProgramID > 0)
        {
            distoVertexHandle = GLES20.glGetAttribLocation(distoShaderProgramID,
                    "vertexPosition");
            distoTexCoordHandle = GLES20.glGetAttribLocation(distoShaderProgramID,
                    "vertexTexCoord");
            distoTexSampler2DHandle = GLES20.glGetUniformLocation(distoShaderProgramID,
                    "texSampler2D");
        }

        // Rendering configuration for video background
        if (vbShaderProgramID > 0)
        {
            // Activate shader:
            GLES20.glUseProgram(vbShaderProgramID);

            // Retrieve handler for texture sampler shader uniform variable:
            vbTexSampler2DHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "texSampler2D");

            // Retrieve handler for projection matrix shader uniform variable:
            vbProjectionMatrixHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "projectionMatrix");

            vbVertexHandle = GLES20.glGetAttribLocation(vbShaderProgramID, "vertexPosition");
            vbTexCoordHandle = GLES20.glGetAttribLocation(vbShaderProgramID, "vertexTexCoord");
            vbProjectionMatrixHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "projectionMatrix");
            vbTexSampler2DHandle = GLES20.glGetUniformLocation(vbShaderProgramID, "texSampler2D");

            // Stop using the program
            GLES20.glUseProgram(0);
        }

        try
        {
            mMountainModelVR = new SampleApplicationV3DModel();
            mMountainModelVR.loadModel(mActivity.getResources().getAssets(),
                    "ARVR/Mountain_VR.v3d");

            mMountainModelAR = new SampleApplicationV3DModel();
            mMountainModelAR.loadModel(mActivity.getResources().getAssets(),
                    "ARVR/Mountain_AR.v3d");
        } catch (IOException e)
        {
            Log.e(LOGTAG, "Unable to load models");
        }

        // Hide the Loading Dialog
        mActivity.loadingDialogHandler
            .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

    }

    // The render function.
    private synchronized void renderFrame()
    {
        State state = TrackerManager.getInstance().getStateUpdater().updateState();
        mRenderer.begin(state);

        ViewList viewList = renderingPrimitives.getRenderingViews();

        // did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            TrackableResult result = state.getTrackableResult(tIdx);
            Matrix44F modelViewMatrix_Vuforia = Tool
                    .convertPose2GLMatrix(result.getPose());

            // retrieve device trackable pose
            if (result.isOfType(DeviceTrackableResult.getClassType())) {
                deviceViewMatrix = result.getPose();
            } else if (result.isOfType(ImageTargetResult.getClassType())) {
                isARObjectVisible = true;
                interactionViewMatrix = modelViewMatrix_Vuforia;
            }
        }

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        if(mIsVR && viewList.contains(VIEW.VIEW_SINGULAR))
            GLES20.glClearColor(skyColor[0], skyColor[1], skyColor[2], skyColor[3]);
        else
            GLES20.glClearColor(0, 0, 0, 1);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        boolean distortForViewer = false;

        // The 'postprocess' view is a special one that indicates that a distortion postprocess is required
        // If this is present, then we need to prepare an off-screen buffer to support the distortion
        if (viewList.contains(VIEW.VIEW_POSTPROCESS))
        {
            Vec2I textureSize = renderingPrimitives.getDistortionTextureSize(VIEW.VIEW_POSTPROCESS);
            distortForViewer = prepareViewerDistortion(textureSize);
        }

        // clear the offscreen texture
        if (mIsVR)
            GLES20.glClearColor(skyColor[0], skyColor[1], skyColor[2], skyColor[3]);
        else
            GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // stereo rendering
        for (int v = 0; v < viewList.getNumViews(); v++)
        {

            int viewID = viewList.getView(v);

            // Any post processing is a special case that will be completed after
            // the main render loop
            if (viewID != VIEW.VIEW_POSTPROCESS)
            {

                Vec4I viewport;
                if (distortForViewer)
                {
                    // We're doing distortion via an off-screen buffer, so the viewport is relative to that buffer
                    viewport = renderingPrimitives.getDistortionTextureViewport(viewID);
                }
                else
                {
                    // We're writing directly to the screen, so the viewport is relative to the screen
                    viewport = renderingPrimitives.getViewport(viewID);
                }

                //set viewport for each view left/right
                GLES20.glViewport(viewport.getData()[0], viewport.getData()[1], viewport.getData()[2], viewport.getData()[3]);

                //set scissor
                GLES20.glScissor(viewport.getData()[0], viewport.getData()[1], viewport.getData()[2], viewport.getData()[3]);

                float projectionMatrix[] = new float[16];

                if (mIsVR)
                {
                    Matrix34F projMatrix = renderingPrimitives.getProjectionMatrix(viewID, COORDINATE_SYSTEM_TYPE.COORDINATE_SYSTEM_WORLD);

                    float rawProjectionMatrixGL[] = Tool.convertPerspectiveProjection2GLMatrix(
                            projMatrix,
                            SampleApplicationSession.NEAR_PLANE,
                            SampleApplicationSession.FAR_PLANE)
                            .getData();

                    // Apply the appropriate eye adjustment to the raw projection matrix, and assign to the global variable
                    float eyeAdjustmentGL[] = Tool.convert2GLMatrix(renderingPrimitives
                            .getEyeDisplayAdjustmentMatrix(viewID)).getData();

                    Matrix.multiplyMM(projectionMatrix, 0, rawProjectionMatrixGL, 0, eyeAdjustmentGL, 0);

                    Matrix44F devicePose = Tool
                            .convertPose2GLMatrix(deviceViewMatrix);

                    // transform device pose transformation to a view matrix
                    Matrix44F deviceViewPose = SampleMath.Matrix44FTranspose(SampleMath.Matrix44FInverse(devicePose));

                    renderVRWorld(deviceViewPose.getData(), projectionMatrix);
                }
                else
                {
                    Matrix34F projMatrix = renderingPrimitives.getProjectionMatrix(viewID, COORDINATE_SYSTEM_TYPE.COORDINATE_SYSTEM_CAMERA);

                    float rawProjectionMatrixGL[] = Tool.convertPerspectiveProjection2GLMatrix(
                            projMatrix,
                            SampleApplicationSession.NEAR_PLANE,
                            SampleApplicationSession.FAR_PLANE)
                            .getData();

                    // Apply the appropriate eye adjustment to the raw projection matrix, and assign to the global variable
                    float eyeAdjustmentGL[] = Tool.convert2GLMatrix(renderingPrimitives
                            .getEyeDisplayAdjustmentMatrix(viewID)).getData();

                    Matrix.multiplyMM(projectionMatrix, 0, rawProjectionMatrixGL, 0, eyeAdjustmentGL, 0);

                    renderARWorld(interactionViewMatrix.getData(), projectionMatrix, viewID, viewport.getData(), isARObjectVisible);
                }

                // disable scissor test
                GLES20.glDisable(GLES20.GL_SCISSOR_TEST);

            }
        }

        // As a final step, perform the viewer distortion if required
        if (distortForViewer)
        {
            Vec4I screenViewport = renderingPrimitives.getViewport(VIEW.VIEW_POSTPROCESS);
            Mesh distoMesh = renderingPrimitives.getDistortionTextureMesh(VIEW.VIEW_POSTPROCESS);
            performViewerDistortion(screenViewport, distoMesh);
        }

        isARObjectVisible = false;
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        mRenderer.end();
    }


    boolean prepareViewerDistortion(Vec2I textureSize)
    {
        // Check if the texture size is valid; if not, the configuration doesn't support distortion
        if (textureSize.getData()[0] == 0 || textureSize.getData()[1] == 0)
        {
            // Log a warning
            Log.i(LOGTAG, "Viewer distortion is not supported in this configuration");

            // Tidy up and return without setting anything up
            viewerDistortionTextureSize = textureSize;

            return false;
        }

        // If the texture has changed size, then regenerate the off-screen frame buffer
        // (the default texture is (0,0), so this will happen on the first frame)
        if ((textureSize.getData()[0] != viewerDistortionTextureSize.getData()[0]) ||
                (textureSize.getData()[1] != viewerDistortionTextureSize.getData()[1]))
        {
            // bind the texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, viewerDistortionColourID[0]);

            // Initialize texture size, format, pixel size. This texture is used for off-screen rendering, this will be used as color attachment.
            // The selected format should be dependent on the application, in this case RGB was used.
            // Rendering into a smaller texture is also possible to improve rendering time along with the selected interpolation method when configuring the texture.
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
                    textureSize.getData()[0], textureSize.getData()[1],
                    0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);

            // configure texture parameters
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // bind the depth texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, viewerDistortionDepthID[0]);

            // Initialize texture size, format, pixel size. This texture is used as depth attachment for the content to be rendered properly.
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT,
                    textureSize.getData()[0], textureSize.getData()[1],
                    0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_INT, null);

            // remember this texture size for next time round
            viewerDistortionTextureSize = textureSize;
        }
        // route all drawing commands to the off-screen frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, viewerDistortionFboID[0]);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Attach the distortion textures to the off-screen frame buffer
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, viewerDistortionColourID[0], 0);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, viewerDistortionDepthID[0], 0);

        return true;
    }


    void performViewerDistortion(Vec4I screenViewport, Mesh distoMesh)
    {
        // Render the off-screen buffer to the screen using the texture
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(screenViewport.getData()[0], screenViewport.getData()[1], screenViewport.getData()[2], screenViewport.getData()[3]);

        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);

        // Disable depth testing
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // setup the shader
        GLES20.glUseProgram(distoShaderProgramID);

        // pass our FBO texture to the shader
        GLES20.glUniform1i(distoTexSampler2DHandle, 0);

        // activate texture unit
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // bind the texture with the stereo rendering
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, viewerDistortionColourID[0]);

        // Enable vertex and texture coordinate vertex attribute arrays:
        GLES20.glEnableVertexAttribArray(distoVertexHandle);
        GLES20.glEnableVertexAttribArray(distoTexCoordHandle);
        // Draw geometry:
        int numIndices = distoMesh.getNumTriangles() * 3;

        GLES20.glVertexAttribPointer(distoVertexHandle, 3, GLES20.GL_FLOAT, false, 0, distoMesh.getPositions());
        GLES20.glVertexAttribPointer(distoTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, distoMesh.getUVs());
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, distoMesh.getTriangles());

        // Disable vertex and texture coordinate vertex attribute arrays again:
        GLES20.glDisableVertexAttribArray(distoVertexHandle);
        GLES20.glDisableVertexAttribArray(distoTexCoordHandle);
    }


    private void renderARWorld(float[] targetPose, float[] projectionMatrix, int viewId, int[] viewport, boolean isARObjectVisible) {
        int vbVideoTextureUnit = 0;
        // Bind the video bg texture and get the Texture ID from Vuforia
        GLTextureUnit tex = new GLTextureUnit();
        tex.setTextureUnit(vbVideoTextureUnit);
        if (viewId != VIEW.VIEW_RIGHTEYE )
        {
            if (!Renderer.getInstance().updateVideoBackgroundTexture(tex))
            {
                Log.e(LOGTAG, "Unable to bind video background texture");
                return;
            }
        }

        renderVideoBackground(viewId, viewport, vbVideoTextureUnit);

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        // Check if the viewer is active and set a scissor test to clip the augmentation to be constrained inside of the video background
        if(Device.getInstance().isViewerActive()) {
            float sceneScaleFactor = getSceneScaleFactor(viewId);
            Matrix.scaleM(projectionMatrix, 0, sceneScaleFactor, sceneScaleFactor, 1.0f);
        }

        // If target detected render the augmentation
        if (isARObjectVisible) {
            if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
                GLES20.glFrontFace(GLES20.GL_CW); // Front camera
            else
                GLES20.glFrontFace(GLES20.GL_CCW); // Back camera

            float[] modelViewProjection = new float[16];

            // deal with the modelview and projection matrices
            Matrix.rotateM(targetPose, 0, 90, 1.0f, 0.0f, 0.0f);
            Matrix.translateM(targetPose, 0, 0.0f, 0.0f,
                    AR_OBJECT_SCALE_FLOAT);
            Matrix.scaleM(targetPose, 0, AR_OBJECT_SCALE_FLOAT,
                    AR_OBJECT_SCALE_FLOAT, AR_OBJECT_SCALE_FLOAT);

            Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, targetPose, 0);

            mMountainModelAR.render(targetPose, modelViewProjection);

        }


        if(Device.getInstance().isViewerActive())
        {
            GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        }

    }


    private void renderVRWorld(float[] devicePose, float[] projectionMatrix)
    {
        float modelViewMatrix[]  = new float[16];
        float worldInitialPositionMatrix[] = new float[16];
        float mvpMatrix[] = new float[16];

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        Matrix.setIdentityM(worldInitialPositionMatrix, 0);
        Matrix.translateM(worldInitialPositionMatrix, 0, 0, -1.7f, 0); // Translate 1.7 meters since it is the average human height
        Matrix.rotateM(worldInitialPositionMatrix, 0, -90, 1, 0, 0);
        Matrix.scaleM(worldInitialPositionMatrix,0,.5f,.5f,.5f);

        Matrix.multiplyMM(modelViewMatrix, 0, devicePose, 0, worldInitialPositionMatrix,0);

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
        mMountainModelVR.render(modelViewMatrix, mvpMatrix);

    }

    private void renderVideoBackground(int viewId, int[] viewport, int vbVideoTextureUnit)
    {
        float[] vbProjectionMatrix = Tool.convert2GLMatrix(
            renderingPrimitives.getVideoBackgroundProjectionMatrix(viewId, COORDINATE_SYSTEM_TYPE.COORDINATE_SYSTEM_CAMERA)).getData();

        // Apply the scene scale on video see-through eyewear, to scale the video background and augmentation
        // so that the display lines up with the real world
        // This should not be applied on optical see-through devices, as there is no video background,
        // and the calibration ensures that the augmentation matches the real world
        if (Device.getInstance().isViewerActive())
        {
            float sceneScaleFactor = getSceneScaleFactor(viewId);
            Matrix.scaleM(vbProjectionMatrix, 0, sceneScaleFactor, sceneScaleFactor, 1.0f);

            // Apply a scissor around the video background, so that the augmentation doesn't 'bleed' outside it
            int [] scissorRect = SampleUtils.getScissorRect(vbProjectionMatrix, viewport);

            GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
            GLES20.glScissor(scissorRect[0], scissorRect[1], scissorRect[2], scissorRect[3]);
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        Mesh vbMesh = renderingPrimitives.getVideoBackgroundMesh(viewId);
        // Load the shader and upload the vertex/texcoord/index data
        GLES20.glUseProgram(vbShaderProgramID);
        GLES20.glVertexAttribPointer(vbVertexHandle, 3, GLES20.GL_FLOAT, false, 0, vbMesh.getPositions().asFloatBuffer());
        GLES20.glVertexAttribPointer(vbTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, vbMesh.getUVs().asFloatBuffer());

        GLES20.glUniform1i(vbTexSampler2DHandle, vbVideoTextureUnit);

        // Render the video background with the custom shader
        // First, we enable the vertex arrays
        GLES20.glEnableVertexAttribArray(vbVertexHandle);
        GLES20.glEnableVertexAttribArray(vbTexCoordHandle);

        // Pass the projection matrix to OpenGL
        GLES20.glUniformMatrix4fv(vbProjectionMatrixHandle, 1, false, vbProjectionMatrix, 0);

        // Then, we issue the render call
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vbMesh.getNumTriangles() * 3, GLES20.GL_UNSIGNED_SHORT,
                vbMesh.getTriangles().asShortBuffer());

        // Finally, we disable the vertex arrays
        GLES20.glDisableVertexAttribArray(vbVertexHandle);
        GLES20.glDisableVertexAttribArray(vbTexCoordHandle);

        SampleUtils.checkGLError("Rendering of the video background failed");
    }

    float getSceneScaleFactor(int viewId)
    {
        // Get the y-dimension of the physical camera field of view
        Vec2F fovVector = CameraDevice.getInstance().getCameraCalibration().getFieldOfViewRads();
        float cameraFovYRads = fovVector.getData()[1];

        // Get the y-dimension of the virtual camera field of view
        Vec4F virtualFovVector = renderingPrimitives.getEffectiveFov(viewId); // {left, right, bottom, top}
        float virtualFovYRads = virtualFovVector.getData()[2] + virtualFovVector.getData()[3];


        // The scene-scale factor represents the proportion of the viewport that is filled by
        // the video background when projected onto the same plane.
        // In order to calculate this, let 'd' be the distance between the cameras and the plane.
        // The height of the projected image 'h' on this plane can then be calculated:
        //   tan(fov/2) = h/2d
        // which rearranges to:
        //   2d = h/tan(fov/2)
        // Since 'd' is the same for both cameras, we can combine the equations for the two cameras:
        //   hPhysical/tan(fovPhysical/2) = hVirtual/tan(fovVirtual/2)
        // Which rearranges to:
        //   hPhysical/hVirtual = tan(fovPhysical/2)/tan(fovVirtual/2)
        // ... which is the scene-scale factor
        return (float) (Math.tan(cameraFovYRads / 2) / Math.tan(virtualFovYRads / 2));
    }


    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;

    }

}
