package no.ice_9.xquisite_chi;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by human on 08.08.16.
 */
public class RecScreenGLObject {

    float[] mRotationMatrix = new float[16];
    float[] mTranslationMatrix = new float[16];

    private final float[] mDrawMatrix = new float[16];

    private final String vertexInfoTileShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 VidTexCoordIn;" +
                    "varying vec2 VidTexCoordOut;" +
                    "void main() {" +
                    "  VidTexCoordOut = VidTexCoordIn;" +
                    "  gl_Position = uMVPMatrix*vPosition;" +
                    "}";

    private final String fragmentInfoTileShaderCode =
            "#extension GL_OES_EGL_image_external : require \n"+
                    "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform samplerExternalOES VidTexture;" +
                    "varying lowp vec2 VidTexCoordOut;" +
                    "void main() {" +
                    "  gl_FragColor =  vColor * texture2D(VidTexture, VidTexCoordOut); ;" +
                    "}";

    private int vidTextureRef = -1;
    private int vidfsTexture;

    public final int mProgram;

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer vidTextureBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    float tileCoords[] = {   // in counterclockwise order:
            -1.0f, -1.0f, 0.1f, // top
            -1.0f,  1.0f, 0.1f, // bottom left
            1.0f, -1.0f, 0.1f, // bottom left
            1.0f,  1.0f, 0.1f  // bottom right
    };

    private short drawOrder[] = { 0, 1, 2, 1, 2, 3 }; // order to draw vertices

    float[] tileVidTextureCoords =
            {
                    0.0f,0.0f,
                    1.0f,0.0f,
                    0.0f,1.0f,
                    1.0f,1.0f
            };
    //texture coordinates per vertex
    static final int COORDS_PER_TEXTURE = 2;

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    public RecScreenGLObject(int videoTexture)
    {
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

        byteBuffer = ByteBuffer.allocateDirect(tileVidTextureCoords.length*4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vidTextureBuffer = byteBuffer.asFloatBuffer();
        vidTextureBuffer.put(tileVidTextureCoords);
        vidTextureBuffer.position(0);

        vidTextureRef = videoTexture;
    }

    /*DRAW*/

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int textureStride = COORDS_PER_TEXTURE * 4; // 4 bytes per vertex

    //handles
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mVidTextureHandle;

    private float ang=0;

    public void draw(float[] mvpMatrix)
    {
        Matrix.setIdentityM(mTranslationMatrix,0);
        Matrix.translateM(mTranslationMatrix,0,-4,0,-10);
        Matrix.setRotateM(mRotationMatrix,0,90+ang,0,1,0);

        ang+=0.5f;
        //Matrix.translateM(mMVPMatrix,0,ang,ang,ang);
        Matrix.multiplyMM(mDrawMatrix, 0, mvpMatrix, 0, mTranslationMatrix, 0);
        Matrix.multiplyMM(mDrawMatrix, 0, mDrawMatrix, 0, mRotationMatrix, 0);

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

        //get handle to texture coordinate variable
        mVidTextureHandle = GLES20.glGetAttribLocation(mProgram, "VidTexCoordIn");
        //if (mAvalTextureHandle == -1) Log.e("ASCII", "AvalTexCoordIn not found");

        //get handle to shape's texture reference
        vidfsTexture = GLES20.glGetUniformLocation(mProgram, "VidTexture");
        //if (avalfsTexture == -1) Log.e("ASCII", "AvalTexture not found");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mDrawMatrix, 0);
        //MyGLRenderer.checkGlError("glUniformMatrix4fv");

        //


        //
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES20.glVertexAttribPointer(mVidTextureHandle, COORDS_PER_TEXTURE,
                GLES20.GL_FLOAT, false,
                textureStride, vidTextureBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, vidTextureRef);
        GLES20.glUniform1i(vidfsTexture, 2);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mVidTextureHandle);

        //GLES20.glEnableVertexAttribArray(mPositionHandle);

        //Draw the shape
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        //Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mVidTextureHandle);
    }
}
