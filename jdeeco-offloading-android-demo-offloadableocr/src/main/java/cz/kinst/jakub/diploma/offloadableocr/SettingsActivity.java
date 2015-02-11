package cz.kinst.jakub.diploma.offloadableocr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by jakubkinst on 11/02/15.
 */
public class SettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }
}
