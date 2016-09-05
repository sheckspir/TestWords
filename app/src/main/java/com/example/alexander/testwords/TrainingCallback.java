package com.example.alexander.testwords;

public interface TrainingCallback {

    void onAnswerGiven(boolean right);

    void onAnswerCompleteShow();

    void showResult(int rightAnswers, int countAnswers);

    void cancelTesting(String errorMessage);

}
