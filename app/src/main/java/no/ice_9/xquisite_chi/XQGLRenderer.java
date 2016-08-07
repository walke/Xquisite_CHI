package no.ice_9.xquisite_chi;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by human on 04.08.16.
 */
public class XQGLRenderer implements GLSurfaceView.Renderer {

    private float mRatio=1f;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    //private final float[] mTranslationMatrix = new float[16];

    int muMVPMatrixHandle;

    DummyGLObject dummyGLObject;

    public float angx=0.0f;
    public float angy=0.0f;

    float ang=0.0f;

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config)
    {
        /*RENDER INIT*/
        GLES20.glClearColor(0.0f, 0.8f, 0.0f, 1.0f);







        dummyGLObject = new DummyGLObject();
        muMVPMatrixHandle = GLES20.glGetUniformLocation(dummyGLObject.mProgram, "uMVPMatrix");

        Matrix.setLookAtM(mMVPMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //mRatio=(float)sx/(float)sy;
    }

    public void onDrawFrame(GL10 unused)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, angx, angy, -3, 0.0f, 0f, 0f, 0f, 1.0f, 0.0f);

        float[] tmp=mMVPMatrix.clone();

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);



        //Matrix.setIdentityM(mTranslationMatrix, 0);//INFOTILE
        //Matrix.perspectiveM(mProjectionMatrix,1,0.3f,1f,0.1f,100f);

        dummyGLObject.draw(mMVPMatrix);

        Matrix.rotateM();

        //Matrix.scaleM(mTranslationMatrix,0,0.1f,0.1f,0f);
        //dummyGLObject.draw(mTranslationMatrix);

        ang+=0.01f;
    }

    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        mRatio=ratio;



        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 0.2f, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
