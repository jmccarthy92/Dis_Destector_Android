package com.example.android.disdetector;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import javax.mail.Message;

public class MainActivity extends AppCompatActivity {

    private Button searchButton;
    private Button speechButton;
    private Button emailButton;
    private Button searchTextButton;
    private EditText userInput;
    private TextView speech;
    private TextView result;
    private TextView emailResult;
    private TextView userNameView;
    private TextView passwordView;
    private ProgressBar pBar;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String speechText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speech = (TextView) findViewById(R.id.textView2);
        pBar = (ProgressBar)findViewById(R.id.progressBar);
        userInput = (EditText) findViewById(R.id.editText);
        result = (TextView) findViewById(R.id.textView);
        pBar.setVisibility(View.GONE);
      //  new MailAsyncTask().execute();
        emailResult = (TextView) findViewById(R.id.textView3);
        userNameView = (TextView) findViewById(R.id.textView4);
        passwordView = (TextView) findViewById(R.id.textView5);
        emailButton = (Button) findViewById(R.id.button3);
        speechButton = (Button) findViewById(R.id.button);
        searchButton = (Button) findViewById(R.id.button2);
        searchTextButton = (Button) findViewById(R.id.button4);
        searchTextButton.setOnClickListener(searchText);
        searchButton.setOnClickListener(speechExtract);
        speechButton.setOnClickListener(searchDis);
        emailButton.setOnClickListener(searchDisEmail);
    }

    private View.OnClickListener searchText = new View.OnClickListener() {
          @Override
        public void onClick(View v) {
              new DisAsyncTask().execute(userInput.getText().toString());
          }
    };

    private View.OnClickListener speechExtract = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            speech.setText("");
            askSpeechInput();
        }
    };

    private View.OnClickListener searchDis = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            result.setText("");
            new DisAsyncTask().execute(speechText);
        }
    };

    private View.OnClickListener searchDisEmail = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            emailResult.setText("");
            String username = userNameView.getText().toString();
            String password = passwordView.getText().toString();

            new MailAsyncTask().execute(username, password);
        }
    };

    private void askSpeechInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Please Say Something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a){
            a.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        switch(requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechText = result.get(0);
                    speech.setText(speechText);
                }
            }
        }
    }

    private class MailAsyncTask extends AsyncTask<String, Void, String[]>{


        @Override
        protected String[] doInBackground(String... arg) {
            GMailReader gMail = new GMailReader(arg[0], arg[1]);
            try {

                String emailText =  gMail.readMail();
                URL disRequestURL = NetworkUtils.buildURL(emailText);
                String response = NetworkUtils.getResponseFromHttpUrl(disRequestURL);
                String [] responseArray = NetworkUtils.getJsonResponse(response);
                return responseArray;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String [] response) {
            if( response != null) {
                for( int j = 0; j < response.length; j++){
                    emailResult.append(response[j] + "\n");
                }
            } else {

            }
        }
    }

    private class DisAsyncTask extends AsyncTask<String, Void, String[]>{
        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            pBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String [] doInBackground(String... params){
            if(params.length == 0){
                return null;
            }
            String dis = params[0];
            URL disRequestURL = NetworkUtils.buildURL(dis);

            try{
                String response = NetworkUtils.getResponseFromHttpUrl(disRequestURL);
                String [] responseArray = NetworkUtils.getJsonResponse(response);
                return responseArray;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String [] response){
            pBar.setVisibility(View.INVISIBLE);
//            Log.e("JAMES", response[0]);
            if( response != null) {

                for( int j = 0; j < response.length; j++){
                    switch( j){
                        case 0:
                            result.append("Score_Tag : " +response[j] + "\n");
                            break;
                        case 1:
                            result.append("Confidence : " +response[j] + "\n");
                            break;
                        case 2:
                            result.append("Irony : "+response[j]+"\n");
                            break;
                    }

                }
            } else {

            }

        }
    }
}
