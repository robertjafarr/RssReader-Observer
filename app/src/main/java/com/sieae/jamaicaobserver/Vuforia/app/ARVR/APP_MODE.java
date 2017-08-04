/*===============================================================================
Copyright (c) 2016 PTC, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

package com.sieae.jamaicaobserver.Vuforia.app.ARVR;

// Enum used to define the actual app mode if we are in augmented or virtual reality mode and
// device if it is full screen or viewer if we are using a viewer as the cardboard.
public enum APP_MODE {
    // This mode enables the camera and the object tracker to use with stones target. It shows the AR scene in full screen and uses a handheld model for rotation.
    HANDHELD_AR,

    // This mode disables the camera and enables the rotational tracker. No AR is present in this mode, there is no interaction with stones target. It shows the VR scene in full screen. It uses a handheld model for rotation.
    HANDHELD_VR,

    // This mode enables the camera and the object tracker to use with stones target. It shows the AR scene in stereo mode to use with a viewer as cardboard. It uses a head model for rotation.
    VIEWER_AR,

    // This mode disables the camera and enables the rotational tracker. No AR is present in this mode, there is no interaction with stones target. It shows the VR scene in stereo mode to use with a viewer. It uses a handheld model for rotation.
    VIEWER_VR
}
