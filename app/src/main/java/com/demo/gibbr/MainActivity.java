package com.demo.gibbr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.andexert.library.RippleView;

public class MainActivity extends AppCompatActivity {

    private boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RippleView rippleView = (RippleView)findViewById(R.id.rippleView);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                if(clicked) {
                    Intent fillPlaceHolderActivity = new Intent(MainActivity.this, FillPlaceHolderActivity.class);
                    startActivity(fillPlaceHolderActivity);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }

        });
    }

    public void getStartedOnClick(View view){
        clicked = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        clicked = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clicked = false;
    }
}

