package no.ice_9.xquisite_chi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by human on 04.08.16.
 */
public class XQGLRenderer implements GLSurfaceView.Renderer {

    //TEXTURES
    public int[] textures = new int[5];
    public SurfaceTexture mSurface;

    //GL OBJECTS
    DummyGLObject dummyGLObject;
    RecScreenGLObject recScreen;
    Button2DGLObject[] mStaticButton;

    //ANIMS
    public boolean upAval;
    public boolean upVid;

    private float mRatio=1f;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mTranslationMatrix = new float[16];

    public  float[] cpos = new float[]{0,0,0};
    public  float[] cfoc = new float[]{0,0,0};
    public  float[] cfup = new float[]{0,0,0};
    public  float[] tpos = new float[]{0,0,0};
    public  float[] tfoc = new float[]{0,0,0};
    public  float[] tfup = new float[]{0,0,0};

    int muMVPMatrixHandle;

    boolean screenSet=false;
    boolean initDone=false;

    public float angx=0.0f;
    public float angy=0.0f;

    float ang=0.0f;

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config)
    {
        /*RENDER INIT*/
        GLES20.glClearColor(0.0f, 0.2f, 0.0f, 1.0f);

        /*TEXTURES INIT*/
        initTextures();





        dummyGLObject = new DummyGLObject();
        recScreen = new RecScreenGLObject(textures[2]);
        //muMVPMatrixHandle = GLES20.glGetUniformLocation(dummyGLObject.mProgram, "uMVPMatrix");

        mStaticButton = new Button2DGLObject[5];
        int funcset[]={1,1};
        mStaticButton[0] = new Button2DGLObject(-0.8f,0.8f,0.1f,0.1f,funcset.clone());


        mStaticButton[1] = new Button2DGLObject(0.5f,0.5f,0.1f,0.1f,funcset.clone());
        mStaticButton[2] = new Button2DGLObject(0.5f,-0.5f,0.1f,0.1f,funcset.clone());
        mStaticButton[3] = new Button2DGLObject(-0.5f,0.5f,0.1f,0.1f,funcset.clone());
        mStaticButton[4] = new Button2DGLObject(-0.5f,-0.5f,0.1f,0.1f,funcset.clone());

        Matrix.setLookAtM(mMVPMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        initDone=true;
        //mRatio=(float)sx/(float)sy;
    }

    public void onDrawFrame(GL10 unused)
    {
        updateSpace();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, cpos[0], cpos[1], cpos[2], cfoc[0], cfoc[1], cfoc[2], cfup[0], cfup[1], cfup[2]);

        float[] tmp=mMVPMatrix.clone();

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);




        //Matrix.perspectiveM(mProjectionMatrix,1,0.3f,1f,0.1f,100f);

        //dummyGLObject.draw(mMVPMatrix);
        float[] mRotationMatrix = new float[16];
        float[] mTrMatrix = new float[16];
        float[] scr = new float[16];

        Matrix.setIdentityM(mTrMatrix,0);
        Matrix.translateM(mTrMatrix,0,4,0,-10);
        Matrix.setRotateM(mRotationMatrix,0,90,0,1,0);


        //Matrix.translateM(mMVPMatrix,0,ang,ang,ang);
        Matrix.multiplyMM(scr, 0, mMVPMatrix, 0, mTrMatrix, 0);
        Matrix.multiplyMM(scr, 0, scr, 0, mRotationMatrix, 0);



        dummyGLObject.draw(scr);
        recScreen.draw(mMVPMatrix);


        for(int i=0;i<mStaticButton.length;i++)
        {
            //Log.d("GL","screena"+mStaticButton[0].midx);
            mStaticButton[i].draw();
        }


        //Matrix.scaleM(mTranslationMatrix,0,0.1f,0.1f,0f);
        //dummyGLObject.draw(mTranslationMatrix);

        ang+=0.01f;
    }

    public void updateAval()
    {
        //mSurface.updateTexImage();
        //mSurface.
        if(upVid)
        {
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[2]);
            mSurface.updateTexImage();
            //TODO:mSurface.getTransformMatrix(mtx);

            //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 1);
        }

    }

    public void updateSpace()
    {
        for(int i=0;i<3;i++)
        {
            if(cpos[i]!=tpos[i])
            {
                cpos[i]+=(tpos[i]-cpos[i])/10.0f;
            }
            if(cfoc[i]!=tfoc[i])
            {
                cfoc[i]+=(tfoc[i]-cfoc[i])/10.0f;
            }
            if(cfup[i]!=tfup[i])
            {
                cfup[i]+=(tfup[i]-cfup[i])/10.0f;
            }
        }
    }

    public void setClick(float x, float y)
    {
        for(int i=0;i<mStaticButton.length;i++)
        {
            mStaticButton[i].setClick(x,y);
        }
    }

    public int[] getClick(float x, float y)
    {
        int[] res={-1};
        for(int i=0;i<mStaticButton.length;i++)
        {
            res=mStaticButton[i].getClick(x,y);
            if(res[0]!=-1)break;
        }
        return res;
    }

    public void setScreen(final VirtScreen screen)
    {
        TimerTask auto= new TimerTask() {
            @Override
            public void run() {
                if(!initDone)return;
                screenSet=false;

                Log.d("GL","screen"+screen.buttSet.length);
                Log.d("GL","screen"+screen.buttSet[0].w);
                tpos=screen.pos.clone();
                tfoc=screen.foc.clone();
                tfup=screen.fup.clone();

                //mStaticButton=new Button2DGLObject[screen.buttSet.length];
                for(int i=0;i<mStaticButton.length;i++)
                {
                    if((i+1)>screen.buttSet.length)
                    {
                        mStaticButton[i].deactivate();
                        continue;
                    }
                    mStaticButton[i].setSpace(screen.buttSet[i].x,screen.buttSet[i].y,screen.buttSet[i].w,screen.buttSet[i].h,screen.buttSet[i].mFunc.clone());
                }
                Log.d("GL","screena"+mStaticButton.length);
                Log.d("GL","screena"+mStaticButton[0].sizx);
                screenSet=true;

                this.cancel();
                return;
            }};

        new Timer().scheduleAtFixedRate(auto, 0, 40);

    }

    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        mRatio=ratio;



        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3f, 20);
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

    private void initTextures()
    {
        upAval=true;
        upAval=false;

        //TEXTURE 0
        /*mBitmap = Bitmap.createBitmap(asciicols,asciirows, Bitmap.Config.ARGB_8888 );
        mCleanBitmap= Bitmap.createBitmap(asciicols,asciirows, Bitmap.Config.ARGB_8888 );

        mCountDownBitmap[0]= BitmapFactory.decodeResource(actContext.getResources(),R.drawable.bc1);
        mCountDownBitmap[1]=BitmapFactory.decodeResource(actContext.getResources(),R.drawable.bc2);
        mCountDownBitmap[2]=BitmapFactory.decodeResource(actContext.getResources(),R.drawable.bc3);

        mIdleBitmap=BitmapFactory.decodeResource(actContext.getResources(),R.drawable.idle);

        mLoadingBitmap=BitmapFactory.decodeResource(actContext.getResources(),R.drawable.load);

        int textGridTex=loadTexture(actContext,R.drawable.textgrid);
        textures[0]=textGridTex;

        mMesTime= Calendar.getInstance().getTimeInMillis();
        Log.d("TIME","  GLREND TX0 INIT "+(mMesTime-mLasTime)+"ms");
        mLasTime=mMesTime;

        Log.d("ASCII", "tex" + textGridTex);



        //GLES20.glGenTextures(1, textures, 1);
        //mBitmap = Bitmap.createBitmap(asciicols,asciirows, Bitmap.Config.ARGB_8888 );

        //GLES20.glGenTextures(2, textures, 2);

        //Random r= new Random();

        for(int i=0;i<asciicols;i++)
        {
            for(int j=0;j<asciirows;j++)
            {
                //mBitmap.setPixel(i,j, Color.argb(r.nextInt(256), r.nextInt(256),r.nextInt(256),r.nextInt(256)));
                mBitmap.setPixel(i, j, Color.argb(0, 0, 0, 0));
                //Log.d("GL","PX:"+mBitmap.getPixel(i,j));
            }
        }*/





        //TEXTURE 2
        GLES20.glGenTextures(1, textures, 2);
        //mSurface.setUseExternalTextureID();
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[2]);

        // Set filtering
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        //GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);




        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);



        mSurface = new SurfaceTexture(textures[2]);
        mSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                //surfaceTexture.updateTexImage();


                upVid = true;
            }
        });

        int videoTex=textures[2];

        //TEXTURE 3
        /*mBitmap = Bitmap.createBitmap(asciicols,asciirows, Bitmap.Config.ARGB_8888 );


        int butMaskTex=loadTexture(actContext,R.drawable.continuebutfull);
        textures[3]=butMaskTex;

        //TEXTURE 4
        mBitmap = Bitmap.createBitmap(asciicols,asciirows, Bitmap.Config.ARGB_8888 );


        int SliderMaskTex=loadTexture(actContext,R.drawable.slider);
        textures[4]=SliderMaskTex;*/










    }

    public static int loadTexture(Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

}
