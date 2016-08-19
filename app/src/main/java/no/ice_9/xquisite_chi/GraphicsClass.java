package no.ice_9.xquisite_chi;

import android.app.Activity;
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
    private final MainActivity actContext;

    public XQGLSurfaceView(Context context, DisplayMetrics metrics, GraphicsClass graphics)
    {
        super(context);

        mMetrics=metrics;

        actContext=(MainActivity)context;

        setEGLContextClientVersion(2);

        mRenderer = new XQGLRenderer();

        setRenderer(mRenderer);
    }

    /**
     * Here GL passes touch actions to the activity
     * move, press, release
     * @param e motion event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        float x = (e.getX()/mMetrics.widthPixels*2)-1.0f;
        float y = 1.0f-(e.getY()/mMetrics.heightPixels*2);

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                mRenderer.angx=x;
                mRenderer.angy=y;

                mRenderer.setClick(x,y);

                break;

            case MotionEvent.ACTION_DOWN:
                mRenderer.setClick(x,y);
                break;
            case MotionEvent.ACTION_UP:
                int[] clickres=mRenderer.getClick(x,y);

                actContext.glTouch(clickres);
                break;
        }

        return true;
    }


}