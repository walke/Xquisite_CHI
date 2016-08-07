package no.ice_9.xquisite_chi;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    GraphicsClass mGraphics;

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

        //setContentView(R.layout.activity_main);
    }
}
