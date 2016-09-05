package com.example.alexander.testwords;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindArray;
import butterknife.ButterKnife;

public class TrainingActivity extends Activity implements TrainingCallback {

    private static final int LIMIT_OF_TEST = 10;

    @BindArray(R.array.words_keys)
    int[] wordKeys;

    private TrainingFragment trainingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        ButterKnife.bind(this);
        startTraining();
    }

    @Override
    public void onAnswerGiven(boolean right) {
        trainingFragment.onAnswerGiven(right);
    }

    @Override
    public void onAnswerCompleteShow() {
        trainingFragment.onAnswerCompleteShow();
    }

    @Override
    public void showResult(int rightAnswers, int countAnswers) {
        Intent intent = new Intent(this, ResultTrainingActivity.class);
        intent.putExtra(ResultTrainingActivity.EXTRA_RIGHT_ANSWERS,rightAnswers);
        intent.putExtra(ResultTrainingActivity.EXTRA_COUNT_ANSWERS,countAnswers);
        startActivity(intent);
        finish();
    }

    @Override
    public void cancelTesting(String errorMessage) {
        Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show();
        finish();
    }

    private void startTraining() {
        List<Integer> listOfKeys = new ArrayList<>();
        for (int wordKey : wordKeys) {
            listOfKeys.add(wordKey);
        }
        Collections.shuffle(listOfKeys);

        if (listOfKeys.size() > LIMIT_OF_TEST) {
            listOfKeys = listOfKeys.subList(0, LIMIT_OF_TEST);
        }

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            trainingFragment = TrainingFragment.newInstance(toIntArray(listOfKeys));
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,trainingFragment)
                    .commit();
        } else {
            trainingFragment = (TrainingFragment) fragment;
        }
    }

    private int[] toIntArray(List<Integer> list){
        int[] ret = new int[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
    }
}
