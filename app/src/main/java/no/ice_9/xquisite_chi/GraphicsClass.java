package no.ice_9.xquisite_chi;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by human on 04.08.16.
 */
public class GraphicsClass {


    //SCREEN VARIABLES
    Display display;
    DisplayMetrics displayMetrics;

    //TextView mText;
    XQGLSurfaceView mGLView;

    public GraphicsClass(Context context,String fract)
    {
        display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        displayMetrics=new DisplayMetrics();
        display.getMetrics(displayMetrics);
        mGLView = new XQGLSurfaceView(context,displayMetrics,this);
    }
}

class XQGLSurfaceView extends GLSurfaceView
{
    DisplayMetrics mMetrics;
    public final XQGLRenderer mRenderer;

    public XQGLSurfaceView(Context context, DisplayMetrics metrics, GraphicsClass graphics)
    {
        super(context);

        mMetrics=metrics;

        setEGLContextClientVersion(2);

        mRenderer = new XQGLRenderer();

        setRenderer(mRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        float x = (e.getX()/mMetrics.widthPixels*2)-1.0f;
        float y = 1.0f-(e.getY()/mMetrics.heightPixels*2);

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                mRenderer.angx=x;
                mRenderer.angy=y;

                break;
        }

        return true;
    }
}