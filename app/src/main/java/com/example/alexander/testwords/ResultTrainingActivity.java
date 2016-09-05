package com.example.alexander.testwords;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultTrainingActivity extends Activity {
    public static final String EXTRA_RIGHT_ANSWERS = "result";
    public static final String EXTRA_COUNT_ANSWERS = "max_result";

    @BindView(R.id.textview_result)
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_of_testing);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(EXTRA_RIGHT_ANSWERS) && getIntent().hasExtra(EXTRA_COUNT_ANSWERS)){
            int result = getIntent().getIntExtra(EXTRA_RIGHT_ANSWERS,0);
            int maxResult = getIntent().getIntExtra(EXTRA_COUNT_ANSWERS,0);
            resultTextView.setText(getString(R.string.result,result,maxResult));
        }
    }

    @OnClick(R.id.button_restart)
    public void onClickRestart() {
        Intent intent = new Intent(this, TrainingActivity.class);
        startActivity(intent);
        finish();
    }
}
