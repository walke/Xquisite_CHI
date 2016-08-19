package no.ice_9.xquisite_chi;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by human on 07.08.16.
 */
public class Button2DGLObject {

    private final float[] mDrawMatrix = new float[16];

    public float midx;
    public float midy;
    public float sizx;
    public float sizy;

    public float midOx;
    public float midOy;
    public float sizOx;
    public float sizOy;

    public float midTx;
    public float midTy;
    public float sizTx;
    public float sizTy;

    int[] mButtonFunc;

    boolean isDown=false;
    boolean active=false;

    /**
     * Shader here takes texture of the button and mixes it with dynamical color data
     */
    private final String vertexInfoTileShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix*vPosition;" +
                    "}";

    private final String fragmentInfoTileShaderCode =

            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor =  vColor ;" +
                    "}";

    private final int mProgram;

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    float tileCoords[] = {   // in counterclockwise order:
            -1.0f, -1.0f, 0.1f, // top
            -1.0f,  1.0f, 0.1f, // bottom left
            1.0f, -1.0f, 0.1f, // bottom left
            1.0f,  1.0f, 0.1f  // bottom right
    };

    private short drawOrder[] = { 0, 1, 2, 1, 2, 3 }; // order to draw vertices

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 1.0f, 0.0f, 0.0f, 0.2f };

    public Button2DGLObject(float x, float y, float w, float h, int[] func)
    {
        midOx=x;
        midOy=y;
        sizOx=w;
        sizOy=h;

        mButtonFunc=func;

        midx=midOx;
        midy=midOy;
        sizx=sizOx;
        sizy=sizOy;
        midTx=midOx;
        midTy=midOy;
        sizTx=sizOx;
        sizTy=sizOy;



        int vertexInfoShader = XQGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexInfoTileShaderCode);
        int fragmentInfoShader = XQGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentInfoTileShaderCode);

        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexInfoShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentInfoShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);



        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(tileCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(tileCoords);
        vertexBuffer.position(0);
        byteBuffer = ByteBuffer.allocateDirect(drawOrder.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        indexBuffer = byteBuffer.asShortBuffer();
        indexBuffer.put(drawOrder);
        indexBuffer.position(0);
    }

    /*DRAW*/

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    //handles
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    public void draw()
    {
        if(!active)return;
        if(isDown)color[0]=0;
        else color[0]=1;

        Matrix.setIdentityM(mDrawMatrix, 0);

        Matrix.translateM(mDrawMatrix, 0, midx, midy, 1.0f);
        Matrix.scaleM(mDrawMatrix, 0, sizx, sizy, 0.0f);


        /*float dif=Math.abs(midx-midTx)+Math.abs(midy-midTy)+Math.abs(sizx-sizTx)+Math.abs(sizy-sizTy);
        //Log.d("ASCII","dif"+dif);
        if(dif>0.01)
        {
            midx+=(midTx-midx)/10.0f;
            midy+=(midTy-midy)/10.0f;
            sizx+=(sizTx-sizx)/10.0f;
            sizy+=(sizTy-sizy)/10.0f;
        }*/

        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //XQGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mDrawMatrix, 0);
        //MyGLRenderer.checkGlError("glUniformMatrix4fv");

        //
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        //
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        //GLES20.glEnableVertexAttribArray(mPositionHandle);

        //Draw the shape
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glDisable(GLES20.GL_BLEND);

        //Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void setSpace(float x,float y,float w,float h,int[] func)
    {
        midx=x;
        midy=y;
        sizx=w;
        sizy=h;

        mButtonFunc=func;
        active=true;
    }

    public void deactivate(){active=false;}

    public void setTargetShape(float x,float y,float w,float h)
    {
        midTx=x;
        midTy=y;
        sizTx=w;
        sizTy=h;
    }

    public void setClick(float x, float y)
    {
        if(x<(midx+(sizx)) && x>(midx-(sizx)) && y<(midy+(sizy)) && y>(midy-(sizy)))isDown=true;
        else isDown=false;

    }

    public int[] getClick(float x, float y)
    {
        if(isDown)
        {
            isDown=false;

            return mButtonFunc;

        }
        else
        {
            int[] dum={-1};
            return dum;
        }
    }


}
