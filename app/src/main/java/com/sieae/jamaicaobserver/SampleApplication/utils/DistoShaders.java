/*===============================================================================
Copyright (c) 2016 PTC, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

package com.sieae.jamaicaobserver.SampleApplication.utils;

public class DistoShaders {
    public static final String DISTORTION_VERTEX_SHADER = " \n"
            + "attribute vec4 vertexPosition; \n"
            + "attribute vec2 vertexTexCoord; \n"
            + "varying vec2 texCoord; \n"

            + "void main() \n"
            + "{ \n"
            + "   gl_Position = vertexPosition; \n"
            + "   texCoord = vertexTexCoord; \n"
            + "} \n";

    public static final String DISTORTION_FRAGMENT_SHADER = " \n"
            + "precision mediump float; \n"
            + "varying vec2 texCoord; \n"
            + "uniform sampler2D texSampler2D; \n"

            + "void main () \n"
            + "{ \n"
            + "    gl_FragColor = texture2D(texSampler2D, texCoord); \n"
            + "} \n";
}
