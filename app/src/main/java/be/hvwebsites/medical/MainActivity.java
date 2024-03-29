package be.hvwebsites.medical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import be.hvwebsites.medical.constants.GlobalConstant;
import be.hvwebsites.medical.entities.Measurement;
import be.hvwebsites.medical.helpers.ReturnInfo;
import be.hvwebsites.medical.services.FileBaseService;
import be.hvwebsites.medical.viewmodels.MeasurementViewModel;

public class MainActivity extends AppCompatActivity {
    private MeasurementViewModel measurementViewModel;
    // Device
    private final String deviceModel = Build.MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView latestMeasurementsView = findViewById(R.id.resumeinfo);

        // Creer een filebase service bepaalt file base directory obv device en context
        FileBaseService fileBaseService = new FileBaseService(deviceModel
                , this
                , getPackageName()
                , getFilesDir().getPath());

        // Basis directory definitie
        String baseDir = fileBaseService.getFileBaseDir();

        // Metingen definities
        Measurement belly = null;
        Measurement upperP = null;
        Measurement lowerP = null;
        Measurement heartbeat = null;

        // Viewmodel definitie
        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);
        // Initialize viewmodel met basis directory (data ophalen in viewmodel)
        List<ReturnInfo> viewModelRetInfo = measurementViewModel.initializeMViewModel(baseDir);
        for (int i = 0; i < viewModelRetInfo.size(); i++) {
            Toast.makeText(MainActivity.this,
                    viewModelRetInfo.get(i).getReturnMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        if (!measurementViewModel.isErrorViewModel()) {
            // Data zit in viewmodel, laatste metingen uit view model halen
            belly = measurementViewModel.getLatestBelly();
            upperP = measurementViewModel.getLatestUpperP();
            lowerP = measurementViewModel.getLatestLowerP();
            heartbeat = measurementViewModel.getLatestHeartbeat();
        } else {
            // Ophalen data in view model mislukt
            Toast.makeText(MainActivity.this,
                    "Loading Measurements failed",
                    Toast.LENGTH_LONG).show();
            //TODO: Wat moet er nu gebeuren ??
        }

        // Data manipuleren om op scherm te zetten
        String latestMText;
        if (belly != null){
            // Er zijn bellies
            latestMText = belly.getDateAndValue();
        }else {
            latestMText = GlobalConstant.EMPTY;
        }
        if (upperP != null && belly == null){
            // Er zijn bloeddruk metingen mr geen bellies
            latestMText = latestMText.concat(upperP.getDateAndValue());
        }else if (upperP != null){
            // Er zijn bloeddruk metingen en bellies
            latestMText = latestMText.concat(" ; ").concat(upperP.getValueAsString());
            latestMText = latestMText.concat(" ; ").concat(lowerP.getValueAsString());
            latestMText = latestMText.concat(" ; ").concat(heartbeat.getValueAsString());
        } else {
            // Er zijn geen bloeddruk metingen
            latestMText = latestMText.concat(" ; ").concat(GlobalConstant.EMPTY);
            latestMText = latestMText.concat(" ; ").concat(GlobalConstant.EMPTY);
            latestMText = latestMText.concat(" ; ").concat(GlobalConstant.EMPTY);
        }
        // Zet samengestelde gegevens op het scherm
        latestMeasurementsView.setText(latestMText);

    }

    // Als op knop Buikomtrek wordt gedrukt
    public void startBellyRadius(View view) {
        // Ongeacht of er measuremts zijn, ga naar MListActivity. Indien er measurements zijn
        // worden ze getoond anders wordt er naar NewMeasurementsActivity gegaan
        Intent intent = new Intent(this, MListActivity.class);
        intent.putExtra(Measurement.EXTRA_INTENT_KEY_TYPE, GlobalConstant.CASE_BELLY);
        startActivity(intent);
    }

    // Als op knop Bloeddruk wordt gedrukt
    public void startBloodPressure(View view) {
        // Ongeacht of er measuremts zijn, ga naar MListActivity. Indien er measurements zijn
        // worden ze getoond anders wordt er naar NewMeasurementsActivity gegaan
        Intent intent = new Intent(this, MListActivity.class);
        intent.putExtra(Measurement.EXTRA_INTENT_KEY_TYPE, GlobalConstant.CASE_BLOOD);
        startActivity(intent);
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

        /** Menu items definieren */
        Intent mainIntent;
        switch (item.getItemId()) {
            case R.id.menu_send_email:
                // ga naar activity Send Email
                mainIntent = new Intent(MainActivity.this, SendEmailActivity.class);
                startActivity(mainIntent);
                return true;
            case R.id.menu_exit:
                // Exit app
                finish();
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }
}