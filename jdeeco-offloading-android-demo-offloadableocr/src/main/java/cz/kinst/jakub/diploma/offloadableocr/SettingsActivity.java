package cz.kinst.jakub.diploma.offloadableocr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.kinst.jakub.diploma.offloading.Frontend;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.StateBundle;

/**
 * Created by jakubkinst on 11/02/15.
 */
public class SettingsActivity extends ActionBarActivity {
    public static final String DEFAULT_IMAGE_SIZE = "2500";
    @InjectView(R.id.image_size)
    EditText imageSize;
    @InjectView(R.id.ocr_whitelist)
    EditText ocrWhitelist;
    @InjectView(R.id.ocr_blacklist)
    EditText ocrBlacklist;
    private Frontend mFrontend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);
        mFrontend = MainActivity.getFrontend();

        imageSize.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("image_size", DEFAULT_IMAGE_SIZE));

        StateBundle settings = OffloadingManager.getInstance().getBackendStateData(MainActivity.OCR_URI).getData();
        ocrWhitelist.setText(settings.getString("tessedit_char_whitelist", ""));
        ocrBlacklist.setText(settings.getString("tessedit_char_blacklist", ""));
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSettings() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("image_size", imageSize.getText().toString()).commit();

        StateBundle settings = OffloadingManager.getInstance().getBackendStateData(MainActivity.OCR_URI).getData();
        settings.putString("tessedit_char_whitelist", ocrWhitelist.getText().toString());
        settings.putString("tessedit_char_blacklist", ocrBlacklist.getText().toString());
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void[] params) {
                try {
                    OffloadingManager.getInstance().getBackendStateData(MainActivity.OCR_URI).pushData(mFrontend.getActiveBackendAddress(MainActivity.OCR_URI));
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    finish();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.error_saving_settings), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
