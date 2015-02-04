package cz.kinst.jakub.diploma.offloading.android.demo;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.restlet.data.MediaType;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.kinst.jakub.diploma.deecooffload.demo.R;
import cz.kinst.jakub.diploma.offloading.Frontend;
import cz.kinst.jakub.diploma.offloading.OffloadingManager;
import cz.kinst.jakub.diploma.offloading.OnDeploymentPlanUpdatedListener;
import cz.kinst.jakub.diploma.offloading.android.AndroidLogProvider;
import cz.kinst.jakub.diploma.offloading.android.AndroidUDPBroadcast;
import cz.kinst.jakub.diploma.offloading.deeco.model.BackendDeploymentPlan;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.offloading.resource.MultipartHolder;


public class MainActivity extends ActionBarActivity {

    private static final String HELLO_URI = "/hello";

    @InjectView(R.id.get_hello_button)
    Button mGetHelloButton;
    @InjectView(R.id.current_backend)
    TextView mCurrentBackend;
    @InjectView(R.id.performanceSeekBar)
    SeekBar mPerformanceSeekBar;
    @InjectView(R.id.performance_text)
    TextView mPerformanceText;

    private OffloadingManager mOffloadingManager;
    private HelloResourceImpl mHelloResource;
    private Frontend mUIAppComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
//        UDPBroadcastConfig.DEBUG_MODE = true;
//        OffloadingConfig.JDEECO_LOGGING_LEVEL = Level.ALL;

        // init DEECo
        Logger.setProvider(new AndroidLogProvider());

        try {
            mOffloadingManager = OffloadingManager.create(new AndroidUDPBroadcast(this), "hello");

            mHelloResource = new HelloResourceImpl(HELLO_URI, this);
            mOffloadingManager.attachBackend(mHelloResource);
            mUIAppComponent = new Frontend(mOffloadingManager);
            mUIAppComponent.setOnDeploymentPlanUpdatedListener(new OnDeploymentPlanUpdatedListener() {
                @Override
                public void onDeploymentPlanUpdated(BackendDeploymentPlan plan) {
                    String backendAddress = mUIAppComponent.getActiveBackendAddress(HelloResource.class);
                    mCurrentBackend.setText(backendAddress);
                }
            });

            mOffloadingManager.init();
            mOffloadingManager.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPerformanceText.setText((HelloResourceImpl.getPerformance(this) * 10) + "%");
        mPerformanceSeekBar.setProgress(HelloResourceImpl.getPerformance(this));
        mPerformanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                HelloResourceImpl.setPerformance(MainActivity.this, progress);
                mPerformanceText.setText((HelloResourceImpl.getPerformance(MainActivity.this) * 10) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @OnClick(R.id.get_hello_button)
    void onGetHelloClicked() {
        // Initialize the resource proxy.
        final HelloResource backend = mUIAppComponent.getActiveBackendProxy(HelloResource.class);
        new AsyncTask<Void, Void, Message>() {
            @Override
            protected Message doInBackground(Void... params) {
                try {
                    Message message = backend.getHello(Build.MODEL);
                    return message;
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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick(R.id.get_fileupload_button)
    void onGetFileUploadClicked() {
        // Initialize the resource proxy.
        final HelloResource backend = mUIAppComponent.getActiveBackendProxy(HelloResource.class);

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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    protected void onDestroy() {
        try {
            mOffloadingManager.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }
}
