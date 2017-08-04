package com.sieae.jamaicaobserver.SampleApplication.utils;

import java.nio.Buffer;

/**
 * Created by Mohammad Arshi Khan on 04-11-2016.
 */

public class Pot extends MeshObject
{

    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;

    private int indicesNumber = 0;
    private int verticesNumber = 0;

    public void setVERTS(double[] VERTS) {
        this.VERTS = VERTS;
    }

    double[] VERTS;
    double[] TEX_COORDS;
    double[] NORMS;
    short[] INDICES;



    public double[] getVERTS() {
        return VERTS;
    }

    public double[] getTEX_COORDS() {
        return TEX_COORDS;
    }

    public double[] getNORMS() {
        return NORMS;
    }

    public short[] getINDICES() {
        return INDICES;
    }

    public void setINDICES(short[] INDICES) {
        this.INDICES = INDICES;
    }

    public void setNORMS(double[] NORMS) {
        this.NORMS = NORMS;
    }

    public void setTEX_COORDS(double[] TEX_COORDS) {
        this.TEX_COORDS = TEX_COORDS;
    }

    public Pot()
    {

    }

    public void init(){
        setVerts();
        setTexCoords();
        setNorms();
        setIndices();
    }

    private void setVerts()
    {
        double[] TEAPOT_VERTS = getVERTS();
        mVertBuff = fillBuffer(TEAPOT_VERTS);
        verticesNumber = TEAPOT_VERTS.length / 3;
    }


    private void setTexCoords()
    {
        double[] TEAPOT_TEX_COORDS = getTEX_COORDS();
        mTexCoordBuff = fillBuffer(TEAPOT_TEX_COORDS);

    }


    private void setNorms()
    {
        double[] TEAPOT_NORMS = getNORMS();
        mNormBuff = fillBuffer(TEAPOT_NORMS);
    }


    private void setIndices()
    {
        short[] TEAPOT_INDICES = getINDICES();
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
