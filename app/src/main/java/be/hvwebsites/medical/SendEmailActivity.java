package be.hvwebsites.medical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import be.hvwebsites.medical.fragments.DatePickerFragment;
import be.hvwebsites.medical.helpers.DateString;
import be.hvwebsites.medical.helpers.ReturnInfo;
import be.hvwebsites.medical.interfaces.NewDatePickerInterface;
import be.hvwebsites.medical.mailing.MailSender;
import be.hvwebsites.medical.mailing.model.Attachment;
import be.hvwebsites.medical.mailing.model.Mail;
import be.hvwebsites.medical.mailing.model.Recipient;
import be.hvwebsites.medical.repositories.Cookie;
import be.hvwebsites.medical.repositories.CookieRepository;
import be.hvwebsites.medical.services.FileBaseService;
import be.hvwebsites.medical.viewmodels.MeasurementViewModel;

public class SendEmailActivity extends AppCompatActivity implements NewDatePickerInterface {
    private MeasurementViewModel measurementViewModel;
    private EditText minimumDateView;
    private String minimumDateStr;
    private CheckBox alreadySentView;
    private boolean alreadySent;
    private EditText emailAdresView;
    private String emailadresStr;

    // Device
    private final String deviceModel = Build.MODEL;

    // Constants
    private static final String EMAIL_ADRES_KEY = "emailadres";
    private static final String EMAIL_ADRES_SENDER = "jstes0089@gmail.com";
    private static final String EMAIL_ADRES_RECIPIENT = "mj842580@gmail.com";
    private static final String EMAIL_ADRES_SUBJECT = "Bloeddrukmetingen vanaf ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        // Koppel variabelen aan scherm componenten
        minimumDateView = findViewById(R.id.valueMinimumDate);
        alreadySentView = findViewById(R.id.checkboxAlreadySent);
        emailAdresView = findViewById(R.id.valueEmailAdres);

        // Ophalen metingen
        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel
                , this
                , getPackageName()
                , getFilesDir().getPath());

        // Basis directory definitie
        String baseDir = fileBaseService.getFileBaseDir();

        // Viewmodel definitie
        measurementViewModel = new ViewModelProvider(this).get(MeasurementViewModel.class);
        // Initialize viewmodel met basis directory (data ophalen in viewmodel)
        List<ReturnInfo> viewModelRetInfo = measurementViewModel.initializeMViewModel(baseDir);
        for (int i = 0; i < viewModelRetInfo.size(); i++) {
            Toast.makeText(SendEmailActivity.this,
                    viewModelRetInfo.get(i).getReturnMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        if (!measurementViewModel.isErrorViewModel()) {
            // Data zit in viewmodel, laatste metingen uit view model halen
        } else {
            // Ophalen data in view model mislukt
            Toast.makeText(SendEmailActivity.this,
                    "Loading Measurements failed",
                    Toast.LENGTH_LONG).show();
            //TODO: Wat moet er nu gebeuren ??
        }

        // Ophalen emailadres als cookie
        CookieRepository cookieRepository = new CookieRepository(baseDir);
        emailadresStr = cookieRepository.getCookieValueFromLabel(EMAIL_ADRES_KEY);
        if (emailadresStr == null){
            emailadresStr = EMAIL_ADRES_RECIPIENT;
        }

        // emailadres op scherm invullen
        emailAdresView.setText(emailadresStr);

        // Send button definitie
        final Button sendButton = findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent(SendEmailActivity.this,
                        MainActivity.class);
                if (TextUtils.isEmpty(minimumDateView.getText()) ||
                        TextUtils.isEmpty(emailAdresView.getText())){
                    // Er zijn geen gegevens ingevuld
                    Toast.makeText(SendEmailActivity.this,
                            "Nothing entered, nothing send !", Toast.LENGTH_LONG).show();
                }else{
                    // Er zijn gegevens ingevuld
                    minimumDateStr = minimumDateView.getText().toString();
                    emailadresStr = emailAdresView.getText().toString();
                    alreadySent = alreadySentView.isChecked();

                    // emailadres bewaren in cookie
                    cookieRepository.addCookie(new Cookie(EMAIL_ADRES_KEY, emailadresStr));

                    // De gegevens worden in een mail verwerkt
                    String[] emailAdresses = new String[1];
                    emailAdresses[0] = emailadresStr;
                    String emailSubject = EMAIL_ADRES_SUBJECT + new DateString(minimumDateStr).getFormatDate();
                    String emailBody = measurementViewModel.getMeasurementsForEmail(minimumDateStr, alreadySent);

                    // Send email directly via solution AndroidMail on github
                    MailSender mailSender = new MailSender(EMAIL_ADRES_SENDER, "Radio_ook0089");

                    Mail.MailBuilder builder = new Mail.MailBuilder();
                    Mail mail = builder
                            .setSender(EMAIL_ADRES_SENDER)
                            .addRecipient(new Recipient(emailadresStr))
                            .setText(emailBody)
                            .setSubject(emailSubject)
                            //.setHtml("<h1 style=\"color:red;\">Ciao</h1>")
                            //.addAttachment(new Attachment(baseDir, MeasurementViewModel.UPPER_BLOOD_PRESSURE_FILE))
                            .build();

                    mailSender.sendMail(mail, new MailSender.OnMailSentListener() {
                        @Override
                        public void onSuccess() {
                            System.out.println();
                            Toast.makeText(SendEmailActivity.this,
                                    "Metingen verstuurd !",
                                    Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onError(Exception error) {
                            System.out.println();
                            Toast.makeText(SendEmailActivity.this,
                                    "Fout bij versturen email: " + error.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                // Send email via Intent
                    //sendEmailWithIntent(emailAdresses, emailSubject, emailBody);

                    // Ga terug nr MainActivity
                    startActivity(replyIntent);
                }
            }
        });
    }

    public void sendEmailWithIntent(String[] addresses, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("*/*");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void showDatePicker(View view) {
        // Toont de datum picker, de gebruiker kan nu de datum picken
        DialogFragment newFragment = new DatePickerFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("Caller", typeMeasurement);
//        newFragment.setArguments(bundle);
        FragmentManager emailFragmentMgr = getSupportFragmentManager();
        newFragment.show(emailFragmentMgr, getString(R.string.datepicker));
    }

    @Override
    public void processDatePickerResult(int year, int month, int day) {
        // Verwerkt de gekozen datum uit de picker
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        String dateMessage = (day_string +
                "/" + month_string + "/" + year_string);

        minimumDateView.setText(dateMessage);

        Toast.makeText(this, "Date: " + dateMessage, Toast.LENGTH_SHORT).show();
    }
}