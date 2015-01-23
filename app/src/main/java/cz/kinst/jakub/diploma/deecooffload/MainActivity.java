package cz.kinst.jakub.diploma.deecooffload;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.restlet.data.MediaType;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.kinst.jakub.diploma.deecooffload.deeco.AndroidLogProvider;
import cz.kinst.jakub.diploma.deecooffload.deeco.AndroidUDPBroadcast;
import cz.kinst.jakub.offloading.OffloadingManager;
import cz.kinst.jakub.offloading.deeco.DEECoManager;
import cz.kinst.jakub.offloading.logger.Logger;
import cz.kinst.jakub.offloading.resource.MultipartHolder;


public class MainActivity extends ActionBarActivity {

    public static final String[] BACKENDS = new String[]{"192.168.0.107", "192.168.0.109"};
    private static final String HELLO_URI = "/hello";
    public static final String PREF_OFFLOAD = "offload";

    @InjectView(R.id.get_hello_button)
    Button mGetHelloButton;
    @InjectView(R.id.offloading_switch)
    Switch mOffloadingSwitch;

    private OffloadingManager mOffloadingManager;
    private HelloResourceImpl mHelloResource;
    private DEECoManager mDeecoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        // init DEECo
        Logger.setProvider(new AndroidLogProvider());

        mOffloadingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean(PREF_OFFLOAD, isChecked).commit();
            }
        });
        mOffloadingSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_OFFLOAD, false));

        try {
            mOffloadingManager = OffloadingManager.create(new AndroidUDPBroadcast(this), "hello");
            mHelloResource = new HelloResourceImpl(HELLO_URI);
            mOffloadingManager.attachResource(mHelloResource);
            Toast.makeText(this, "Server Started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mOffloadingManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.get_hello_button)
    void onGetHelloClicked() {

        // Initialize the resource proxy.
        final HelloResource backend = mOffloadingSwitch.isChecked() ? mOffloadingManager.getResourceProxy(HelloResource.class, getSelectedBackend()) : mHelloResource;

        new AsyncTask<Void, Void, Message>() {
            @Override
            protected Message doInBackground(Void... params) {
                try {
                    return backend.getHello(Build.MODEL);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Message message) {
                if (message != null)
                    Toast.makeText(MainActivity.this, message.message, Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(MainActivity.this, getString(R.string.backend_unavailable), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @OnClick(R.id.get_fileupload_button)
    void onGetFileUploadClicked() {
        // Initialize the resource proxy.
        final HelloResource backend = mOffloadingManager.getResourceProxy(HelloResource.class, mOffloadingManager.)

        new AsyncTask<Void, Void, Message>() {
            @Override
            protected Message doInBackground(Void... params) {
                try {
                    ArrayList<File> files = new ArrayList<File>();
                    files.add(new File(Environment.getExternalStorageDirectory() + File.separator + "test.jpg"));
                    Message message = new Message("Sent message", new Date().getTime());
                    return backend.testFile(new MultipartHolder<Message>(files, MediaType.IMAGE_JPEG, message).getForm());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Message message) {
                if (message != null)
                    Toast.makeText(MainActivity.this, message.message, Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(MainActivity.this, getString(R.string.backend_unavailable), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        try {
            mOffloadingManager.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }
}
