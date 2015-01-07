package cz.kinst.jakub.diploma.deecooffload;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    public static final String[] BACKENDS = new String[]{"127.0.0.1", "192.168.0.107", "192.168.0.109"};
    private static final String HELLO_URI = "/hello";
    private static final int PORT = 8182;

    @InjectView(R.id.backend_spinner)
    Spinner mBackendSpinner;
    @InjectView(R.id.get_hello_button)
    Button mGetHelloButton;
    private HelloResourceImpl helloResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // setup Spinner for selecting backend
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, BACKENDS); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBackendSpinner.setAdapter(spinnerArrayAdapter);
        try {
            helloResource = new HelloResourceImpl(HELLO_URI, PORT);
            helloResource.startServing();
            Toast.makeText(this, "Server Started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @OnClick(R.id.get_hello_button)
    void onGetHelloClicked() {
        // Initialize the resource proxy.
        final HelloResource backend = helloResource.getProxy(HelloResource.class, getSelectedBackend());

        new AsyncTask<Void, Void, Message>() {
            @Override
            protected Message doInBackground(Void... params) {
                return backend.getHello(android.os.Build.MODEL);
            }

            @Override
            protected void onPostExecute(Message message) {
                Toast.makeText(MainActivity.this, message.message, Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private String getSelectedBackend() {
        return BACKENDS[mBackendSpinner.getSelectedItemPosition()];
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
}
