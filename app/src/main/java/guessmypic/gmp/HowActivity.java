package guessmypic.gmp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

public class HowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_how);
    }

}
