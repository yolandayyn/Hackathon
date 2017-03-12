package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ConversationService service;
    private String wusername = "03b1043a-cf1e-4327-bf00-af86cffb1cbf";
    private String wpassword = "P1gokUPraOiu";
    private Button btn;
    private EditText editText;
    private TextView textView;
    private String text;
    private String responseWord;
    private ToggleButton recBtn;
    private MediaRecorder mr;
    private File soundFile;
    public static final int RADIO_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        recBtn = (ToggleButton) findViewById(R.id.toggleButton2);
    }

    private class WastonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... textTranslation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            initializeService();
            String a = process();
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            textView.append(responseWord+"\n");
        }
    }

        public void pressBtn(View view) {
            text = editText.getText().toString();
            textView.append(text+"\n");
            WastonTask wastonTask = new WastonTask();
            wastonTask.execute(new String[]{});
        }


    public void recordingBtn(View view) {
        if(recBtn.isChecked()){
            startRec();
        }
        else{
            stopRec();
        }
    }

        private String process(){
            MessageRequest newMessage = new MessageRequest.Builder().inputText(text).build();

            String workspaceId = "8e90a03a-57f2-41fe-bdb8-87ad82aa770e";

            MessageResponse response = service.message(workspaceId, newMessage).execute();
            responseWord = response.getOutput().get("text").toString();
            return responseWord;
        }

        private void initializeService() {
            service = new ConversationService("2017-02-03");
            service.setUsernameAndPassword(wusername, wpassword);
        }

        public void startRec() {
            if(isExternalStorageWritable()) {
                if (mr == null) {
                    File dir = new File(Environment.getExternalStorageDirectory(), "sounds");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    soundFile = new File(dir, System.currentTimeMillis() + ".amr");
                    if (!soundFile.exists()) {
                        try {
                            soundFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mr = new MediaRecorder();
                    mr.reset();
                    mr.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    mr.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    mr.setOutputFile(soundFile.getAbsolutePath());
                    try {
                        mr.prepare();
                        mr.start();  //开始录制
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void stopRec(){
            if(mr != null){
                mr.stop();
                mr.release();
                mr = null;
            }
        }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void checkRequest(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        RADIO_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RADIO_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    }

