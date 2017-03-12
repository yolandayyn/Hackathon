package com.example.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ConversationService service;
    private String wusername = "03b1043a-cf1e-4327-bf00-af86cffb1cbf";
    private String wpassword = "P1gokUPraOiu";
    private Button btn;
    private EditText editText;
    private TextView textView;
    private String text;
    private String responseWord;
    private ImageButton recBtn;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Map<String, Object> con;

    LinearLayout ll;
    int lr = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);

        recBtn = (ImageButton) findViewById(R.id.imageButton2);
        initializeService();

        ll = (LinearLayout) findViewById(R.id.chat);
    }

    private class WastonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... textTranslation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            String a = process();

            System.out.println("a is: " + a);

            return a;
        }

        @Override
        protected void onPostExecute(String s) {

            //textView.append(responseWord+"\n");
            TextView tv = new TextView(MainActivity.this);
            tv.setId(View.generateViewId());

            RelativeLayout rl = new RelativeLayout(MainActivity.this);
            RelativeLayout.LayoutParams params;

            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.p12);
            int originalWidth = originalBitmap.getWidth();
            int originalHeight = originalBitmap.getHeight();
            int newWidth = 100;
            int newHeight = 100;
            float scale = ((float) newHeight) / originalHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap changedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                    originalWidth, originalHeight, matrix, true);
            ImageSpan is = new ImageSpan(MainActivity.this, changedBitmap);
            SpannableString ss = new SpannableString("  " + responseWord.substring(1,responseWord.length()-1));
            ss.setSpan(is, 0, 1, 0);
            tv.setText(ss);
            tv.setTextSize(24);

            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, tv.getId());

            rl.addView(tv, params);
            ll.addView(rl);

            //jump to the result page
            if(responseWord.contains("ER")){

                Intent i = new Intent(MainActivity.this, page3.class);
                startActivity(i);

            }
            else if(responseWord.contains("just fine")){
                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(i);
            }

            editText.setText("");

        }

    }

        public void pressBtn(View view) {
            text = editText.getText().toString();
            //textView.append(text+"\n");

            TextView tv = new TextView(this);
            tv.setId(View.generateViewId());

            RelativeLayout rl = new RelativeLayout(this);
            RelativeLayout.LayoutParams params;

            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.p11);
            int originalWidth = originalBitmap.getWidth();
            int originalHeight = originalBitmap.getHeight();
            int newWidth = 100;
            int newHeight = 100;
            float scale = ((float) newHeight) / originalHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap changedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                    originalWidth, originalHeight, matrix, true);
            ImageSpan is = new ImageSpan(this, changedBitmap);
            SpannableString ss = new SpannableString(text + "  ");
            ss.setSpan(is, ss.length() - 1, ss.length(), 0);
            tv.setText(ss);
            tv.setTextSize(24);
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, tv.getId());

            rl.addView(tv, params);
            ll.addView(rl);

            WastonTask wastonTask = new WastonTask();
            wastonTask.execute(new String[]{});

        }


    public void recordingBtn(View view) {
        promptSpeechInput();
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));
                }
                break;
            }

        }
    }

        private String process(){
            MessageRequest newMessage = new MessageRequest.Builder().inputText(text).context(con).build();

            String workspaceId = "8e90a03a-57f2-41fe-bdb8-87ad82aa770e";

            MessageResponse response = service.message(workspaceId, newMessage).execute();
            responseWord = response.getOutput().get("text").toString();
            con = response.getContext();
            return responseWord;
        }

        private void initializeService() {
            service = new ConversationService("2017-02-03");
            service.setUsernameAndPassword(wusername, wpassword);
        }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }



}

