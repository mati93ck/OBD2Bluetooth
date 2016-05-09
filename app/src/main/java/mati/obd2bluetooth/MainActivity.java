package mati.obd2bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.TemperatureCommand;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    BluetoothClient bc;
    TextView BT_status, rpm_value, speed_value, temperature_value;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bc = new BluetoothClient(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BT_status = (TextView) this.findViewById(R.id.status_connection);
        rpm_value = (TextView) this.findViewById(R.id.RPM_Value);
        speed_value = (TextView) this.findViewById(R.id.Speed_Value);
        temperature_value = (TextView) this.findViewById(R.id.Temperature_Value);
        Button przycisk = (Button) this.findViewById(R.id.button);

        przycisk.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    readRPM();
                }
                catch (IOException ex) {} catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        /*GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);*/


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        if (id == R.id.connect) {
            try {
                bc.findBT();
                if (bc.mBluetoothAdapter.isEnabled()) {
                    bc.openBT();
                    bc.openOBD();
                    BT_status.setText("Połączono");
                    //Toast.makeText(MainActivity.this, "Otwarto Bluetooth", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(MainActivity.this, "Najpierw włącz Bluetooth", Toast.LENGTH_LONG).show();
            } catch (IOException ex) {
            }
        } else if (id == R.id.disconnect) {
            try {
                bc.closeBT();
                BT_status.setText("Rozłączono");
                Toast.makeText(MainActivity.this, "Rozłączono Bluetooth", Toast.LENGTH_LONG).show();
            } catch (IOException ex) {
            } //!!!!!!!!tu dodać obługę przeciwko idiotom (albo wymysliłem, ze można dac okienko wyskakujace informujace o połączeniu)
        }
        return super.onOptionsItemSelected(item);
    }

    void readRPM() throws IOException, InterruptedException {
        RPMCommand engineRpmCommand = new RPMCommand();
        SpeedCommand speedCommand = new SpeedCommand();
        AmbientAirTemperatureCommand temperatureCommand = new AmbientAirTemperatureCommand();
        while (!Thread.currentThread().isInterrupted()) {
            engineRpmCommand.run(bc.mmSocket.getInputStream(), bc.mmSocket.getOutputStream());
            speedCommand.run(bc.mmSocket.getInputStream(), bc.mmSocket.getOutputStream());
            temperatureCommand.run(bc.mmSocket.getInputStream(), bc.mmSocket.getOutputStream());
            // TODO handle commands result
            rpm_value.setText(engineRpmCommand.getFormattedResult());
            speed_value.setText(speedCommand.getFormattedResult());
            temperature_value.setText(temperatureCommand.getFormattedResult());
            //Log.d(TAG, "RPM: " + engineRpmCommand.getFormattedResult());
            //Log.d(TAG, "Speed: " + speedCommand.getFormattedResult());
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://mati.obd2bluetooth/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://mati.obd2bluetooth/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
