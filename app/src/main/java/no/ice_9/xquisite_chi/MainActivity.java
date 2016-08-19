package no.ice_9.xquisite_chi;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {


    GraphicsClass mGraphics;

    VirtScreen mScreen[];
    int mCurrentScreen=0;


    public boolean glTouch(int[] action) {



        //glTouch(event.getAction());
        Log.d("MAIN","touch"+action[0]);

        switch(action[0])
        {
            case 0:
                setScreen(action[1]);
                break;
        }



        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //force screen to be on while app is running unless power button is pressed
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        //GET GLES
        mGraphics=new GraphicsClass(this,"MAIN");
        LinearLayout ll=new LinearLayout(this);

        ll.addView(mGraphics.mGLView);
        ll.setOrientation(LinearLayout.VERTICAL);

        setContentView(ll);

        initVirtualScreens();

        //setContentView(R.layout.activity_main);
    }

    private void initVirtualScreens()
    {
        mScreen=new VirtScreen[4];

        ButSetup[] butset;

        //INIT
        butset=new ButSetup[1];
        butset[0]=new ButSetup(-0.8f,0.8f,0.1f,0.1f,new int[]{0,1});
        mScreen[0]=new VirtScreenInit(this,"INIT");
        mScreen[0].setSpace(0,0,-15, 0,0,0, 0,1,0);
        mScreen[0].setButtons(butset.clone());

        //START
        butset=new ButSetup[2];
        butset[0]=new ButSetup(-0.8f,0f,0.1f,0.1f,new int[]{0,2});
        butset[1]=new ButSetup(0.8f,0f,0.1f,0.1f,new int[]{0,3});
        mScreen[1]=new VirtScreenStart(this,"START");
        mScreen[1].setSpace(0,0,-4, 0,0,0, 0,1,0);
        mScreen[1].setButtons(butset.clone());

        //REC
        butset=new ButSetup[2];
        butset[0]=new ButSetup(0.8f,0.8f,0.1f,0.1f,new int[]{0,1});
        butset[1]=new ButSetup(0.8f,0f,0.1f,0.1f,new int[]{0,3});
        mScreen[2]=new VirtScreenRec(this,"RECORD");
        mScreen[2].setSpace(0,0,-10, 1,0,-10, 0,1,0);
        mScreen[2].setButtons(butset.clone());

        //QUIZ
        butset=new ButSetup[2];
        butset[0]=new ButSetup(0.8f,0.8f,0.1f,0.1f,new int[]{0,1});
        butset[1]=new ButSetup(0.8f,0f,0.1f,0.1f,new int[]{0,3});
        mScreen[3]=new VirtScreenStart(this,"START");
        mScreen[3].setSpace(0,0,-10, -1,0,-10, 0,1,0);
        mScreen[3].setButtons(butset.clone());

        setScreen(0);
    }

    public void setScreen(int screen)
    {
        mCurrentScreen=screen;

        mGraphics.mGLView.mRenderer.setScreen(mScreen[mCurrentScreen]);
    }

    private void exit()
    {
        finish();
    }




}

class VirtScreenInit extends VirtScreen
{
    public VirtScreenInit(MainActivity act,String title)
    {
        tAct=act;
        mTitle=title;
    }

    @Override
    public void action(int e) {
        super.action(e);

        switch(e)
        {
            case 0:
                tAct.setScreen(1);
                break;
        }
    }
}



class VirtScreenStart extends VirtScreen
{
    public VirtScreenStart(MainActivity act,String title)
    {
        tAct=act;
        mTitle=title;
    }

    @Override
    public void action(int e) {
        super.action(e);

        switch(e)
        {
            case 0:
                tAct.setScreen(1);
                break;
        }
    }
}

class VirtScreenRec extends VirtScreen
{
    //ENUMS
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    MediaRecorder mRecorder;
    Camera mCamera;//Deprecated.. don't know yet what to do about it
    Preview mPreview;

    static String fileToUpload;
    static String mFilePath;

    public VirtScreenRec(MainActivity act,String title)
    {
        tAct=act;
        mTitle=title;

        initCamera(1);
    }

    @Override
    public void action(int e) {
        super.action(e);

        switch(e)
        {
            case 0:
                tAct.setScreen(1);
                break;
        }
    }

    @Override
    public void set() {
        super.set();


    }

    //INIT CAMERA
    private boolean initCamera(int camId)
    {
        final boolean result;

        //CHECK IF CAMERA IS INITIALIZED ALREADY
        if(mCamera==null)
        {



            //try to get camera instance
            mCamera=getCameraInstance(camId);//TODO: get real fronfacing camera here or in <-this function
            //mCamera= (CameraManager) tAct.getSystemService(Context.CAMERA_SERVICE);
            if(mCamera==null)
            {
                //mCamera=getCameraInstance(0);

            }

            if(mCamera==null){return false;}
            else
            {
                TimerTask auto= new TimerTask() {
                    @Override
                    public void run() {
                        boolean res=false;
                        if (tAct.mGraphics.mGLView.mRenderer.mSurface==null) return;
                        try{
                            Log.d("RECORDER","CAM TEXTURE ");
                            while(mCamera==null);// || tAct.mGraphics.mGLView.mRenderer.mSurface==null);
                            Log.d("RECORDER","CAM TEXTURE "+mCamera+","+tAct.mGraphics.mGLView.mRenderer.mSurface);
                            mCamera.setPreviewTexture(tAct.mGraphics.mGLView.mRenderer.mSurface);
                            res=true;

                        }catch (IOException ioe)
                        {
                            Log.d("RECORDER","ERROR SETTING TEXTURE");
                            res=false;
                        }
                        Log.d("RECORDER", "got Camera instance");
                        if(res)this.cancel();
                    }};

                new Timer().scheduleAtFixedRate(auto, 0, 40);

            }


            result=true;






        }
        return true;
    }

    /** A safe way to get an instance of the Camera object. */
    private static Camera getCameraInstance(int camId){
        Camera c = null;
        try {
            c = Camera.open(camId); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /*getOutputFile*/
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.


        //File mediaStorageDir = new File("/mnt/sdcard/", "tmp");
        File mediaStorageDir = new File(mFilePath, "tmp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("tmp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
            fileToUpload = "IMG_"+ timeStamp + ".jpg";
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
            fileToUpload = mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4";
            Log.d("RECORDER","fpath:"+fileToUpload);
            //mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            //      "tmp.mp4");
            //    fileToUpload = "tmp.mp4";
        } else {
            return null;
        }

        return mediaFile;
    }

    //RELEASE CAMERA
    private void releaseCamera()
    {

        if (mCamera != null) {
            Log.d("RECORDER", "releasing camera main ");
            mCamera.release();
            Log.d("RECORDER", "camera released ");
            mCamera = null;
        }
    }

    //RELEASE PREVIEW
    private void releasePreview() {

        if (mPreview != null) {
            Log.d("RECORDER", "releasing preview ");
            mPreview = null;
            Log.d("RECORDER", "preview released ");

        }
    }

    //RELEASE MEDIA_RECORDER
    private void releaseMediaRecorder()
    {
        if (mRecorder != null) {
            mRecorder.reset();   // clear recorder configuration
            mRecorder.release(); // release the recorder object
            mRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }
}

class VirtScreen
{
    String mTitle;
    MainActivity tAct;

    public ButSetup buttSet[];

    public final float[] pos = new float[3];
    public final float[] foc = new float[3];
    public final float[] fup = new float[3];


    public void setSpace(float x,float y,float z, float fx, float fy, float fz, float ux, float uy, float uz)
    {
        pos[0]=x;            pos[1]=y;            pos[2]=z;
        foc[0]=fx;           foc[1]=fy;           foc[2]=fz;
        fup[0]=ux;           fup[1]=uy;           fup[2]=uz;
    }

    public void setButtons(ButSetup[] set)
    {
        buttSet=set.clone();
    }

    public void action(int e){}

    public void set(){}
}

class ButSetup
{
    float x;
    float y;
    float w;
    float h;

    int[] mFunc;

    public ButSetup(float ix,float iy,float iw,float ih,int[] func)
    {
        x=ix;
        y=iy;
        w=iw;
        h=ih;
        mFunc=func.clone();
    }
}
