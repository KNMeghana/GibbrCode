package com.demo.gibbr;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andexert.library.RippleView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by meghanak on 16/06/16.
 */

public class FillPlaceHolderActivity extends AppCompatActivity {

    private String TAG = "FillPlaceHolderActivity";

    private TextView wordsLeft;
    private EditText wordsToFill;
    private Button moveToNextWord;

    private List<String> placeHolders;
    private int count = 0;
    private String story;

    private boolean animationComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fill_place_holder);

        wordsLeft = (TextView) findViewById(R.id.words_left);
        wordsToFill = (EditText) findViewById(R.id.words_to_fill);
        moveToNextWord = (Button) findViewById(R.id.move_next);

        moveToNextWord.setEnabled(false);

        //Start an asynctask to read story from file
        ReadFromFile rf = new ReadFromFile();
        rf.execute();

        final RippleView rippleView = (RippleView) findViewById(R.id.rippleView);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                if (animationComplete) {
                    moveToNextWord.setEnabled(false);
                }
            }

        });

        wordsToFill.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    moveToNextWord.setEnabled(true);
                } else {
                    moveToNextWord.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        moveToNextWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (placeHolders != null) {
                    animationComplete = false;

                    String placeHolderTobeReplaced = "<" + placeHolders.get(count) + ">";
                    story = story.replaceFirst(placeHolderTobeReplaced, "<b><i>" + wordsToFill.getText().toString() + "</i></b>");

                    if (count >= placeHolders.size() - 1) {
                        Intent storyActivity = new Intent(FillPlaceHolderActivity.this, StoryActivity.class);
                        storyActivity.putExtra("story", story);
                        startActivity(storyActivity);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        count++;
                        String scoreString = String.format(getResources().getString(R.string.words_left), placeHolders.size() - count);
                        wordsLeft.setText(scoreString);
                        wordsToFill.setText(null);
                        wordsToFill.setHint(placeHolders.get(count));

                        if (count == placeHolders.size() - 1) {
                            moveToNextWord.setText(getString(R.string.finish));
                        }
                    }
                }
            }
        });
    }

    private void displayWords(String story) {
        this.story = story;
        if (placeHolders != null) {
            moveToNextWord.setText(getString(R.string.next));

            Resources resources = getResources();
            String scoreString = String.format(resources.getString(R.string.words_left), placeHolders.size());
            wordsLeft.setText(scoreString);

            wordsToFill.setHint(getString(R.string.enter) + " " + placeHolders.get(count));
        }
    }


    private class ReadFromFile extends AsyncTask<String, Integer, String> {

        public ReadFromFile() {
            super();
        }

        @Override
        protected String doInBackground(String... str) {
            Resources r = getResources();
            InputStream inputStream = r.openRawResource(R.raw.story);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte buf[] = new byte[1024];
            int len;
            try {
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                inputStream.close();

                String storyText = outputStream.toString();

                //Match all the words within <> to be replaced
                Pattern p = Pattern.compile("\\<(.*?)\\>");
                Matcher m = p.matcher(storyText);

                while (m.find()) {
                    if (placeHolders == null) {
                        placeHolders = new ArrayList<>();
                    }
                    placeHolders.add(m.group(1));
                }

                return storyText;
            } catch (IOException e) {
                Log.e(TAG, "Error : " + e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            displayWords(s);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}
