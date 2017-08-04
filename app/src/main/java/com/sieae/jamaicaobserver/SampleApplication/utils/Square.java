/*===============================================================================
Copyright (c) 2015 PTC Inc. All Rights Reserved.


Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.sieae.jamaicaobserver.SampleApplication.utils;

import java.nio.Buffer;


public class Square extends MeshObject
{

    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;

    private int indicesNumber = 0;
    private int verticesNumber = 0;


    public Square()
    {
        setVerts();
        setTexCoords();
        setNorms();
        setIndices();
    }
    
    
    private void setVerts()
    {
        double[] TEAPOT_VERTS = { -0.5f, -0.5f, 0.0f, //bottom-left corner
                0.5f, -0.5f, 0.0f, //bottom-right corner
                0.5f, 0.5f, 0.0f, //top-right corner
                -0.5f, 0.5f, 0.0f };
        mVertBuff = fillBuffer(TEAPOT_VERTS);
        verticesNumber = TEAPOT_VERTS.length / 3;
    }
    
    
    private void setTexCoords()
    {
        double[] TEAPOT_TEX_COORDS = { 0.0f, 0.0f, //tex-coords at bottom-left corner
                1.0f, 0.0f, //tex-coords at bottom-right corner
                1.0f, 1.0f, //tex-coords at top-right corner
                0.0f, 1.0f };
        mTexCoordBuff = fillBuffer(TEAPOT_TEX_COORDS);
        
    }
    
    
    private void setNorms()
    {
        double[] TEAPOT_NORMS = { 0.0f, 0.0f, 1.0f, //normal at bottom-left corner
                0.0f, 0.0f, 1.0f, //normal at bottom-right corner
                0.0f, 0.0f, 1.0f, //normal at top-right corner
                0.0f, 0.0f, 1.0f };
        mNormBuff = fillBuffer(TEAPOT_NORMS);
    }
    
    
    private void setIndices()
    {
        short[] TEAPOT_INDICES = { 0,1,2, //triangle 1
                2,3,0  };
        mIndBuff = fillBuffer(TEAPOT_INDICES);
        indicesNumber = TEAPOT_INDICES.length;
    }
    
    
    public int getNumObjectIndex()
    {
        return indicesNumber;
    }
    
    
    @Override
    public int getNumObjectVertex()
    {
        return verticesNumber;
    }
    
    
    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType)
    {
        Buffer result = null;
        switch (bufferType)
        {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
            default:
                break;
        
        }
        
        return result;
    }
    
}
