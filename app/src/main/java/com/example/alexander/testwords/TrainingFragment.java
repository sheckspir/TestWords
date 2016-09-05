package com.example.alexander.testwords;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.alexander.testwords.api.ApiException;
import com.example.alexander.testwords.api.DictionaryApi;
import com.example.alexander.testwords.api.RetrofitAdapter;
import com.example.alexander.testwords.model.Word;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingFragment extends Fragment {
    private static final String TAG = TrainingFragment.class.getSimpleName();
    private static final String ARG_IDS = "ids_words";

    DictionaryApi dictionaryApi;

    @BindView(R.id.training_progress)
    ProgressBar progressBar;

    private int[] idsTestWords;
    private List<Word> trainingWords;
    private int rightAnswers = 0;
    private ExerciseFragment exerciseFragment;
    private RightAnswerFragment rightAnswerFragment;
    private ProgressDialog progressDialog;
    private TrainingCallback trainingCallback;

    public static TrainingFragment newInstance(int[] idsWords) {
        Bundle args = new Bundle();
        args.putIntArray(ARG_IDS, idsWords);
        TrainingFragment fragment = new TrainingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onAttachToContext(activity);
    }

    public void onAttachToContext(Context context) {
        if (context instanceof TrainingCallback) {
            trainingCallback = (TrainingCallback) context;
        } else {
            throw new IllegalArgumentException(context + " should implement " + TrainingCallback.class.getName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments().containsKey(ARG_IDS)) {
            idsTestWords = getArguments().getIntArray(ARG_IDS);
        } else {
            throw new IllegalArgumentException(TrainingFragment.class.getSimpleName() + " should be created with newInstance method");
        }
        dictionaryApi = RetrofitAdapter.getInstance().create(DictionaryApi.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (trainingWords == null) {
            uploadWords();
        }
    }

    public void onAnswerGiven(boolean right) {
        progressBar.setProgress(progressBar.getProgress() + 1);
        if (right) {
            rightAnswers++;
        }
        getFragmentManager().beginTransaction()
                .setCustomAnimations(0,R.animator.slide_left)
                .replace(R.id.training_fragment_container,rightAnswerFragment)
                .commit();
    }

    public void onAnswerCompleteShow() {
        if (progressBar.getProgress() < progressBar.getMax()) {
            initNewWord(trainingWords.get(progressBar.getProgress()));
        } else {
            trainingCallback.showResult(rightAnswers, progressBar.getMax());
        }
    }

    private void uploadWords() {
        if (isAdded() && !isDetached()) {
            progressDialog = ProgressDialog.show(getActivity(),getString(R.string.download_list_for_task),null,true);
            String idsSplitedByComma = Arrays.toString(idsTestWords);
            idsSplitedByComma = idsSplitedByComma.replace("[","");
            idsSplitedByComma = idsSplitedByComma.replace("]","");
            idsSplitedByComma = idsSplitedByComma.replace(" ", "");
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            dictionaryApi.getTranslate(idsSplitedByComma, metrics.widthPixels)
                    .enqueue(new DictionaryCallback());
        }
    }

    private void initView() {
        progressBar.setMax(trainingWords.size());
        progressBar.setProgress(0);
        if (trainingWords.size() > 0) {
            initNewWord(trainingWords.get(0));
        } else {
            Log.wtf(TAG, "something go wrong, training words is empty");
            if (isAdded() && !isDetached()){
                trainingCallback.cancelTesting(getString(R.string.error_something));
            }
        }
    }

    private void initNewWord(Word word) {
        exerciseFragment = ExerciseFragment.newInstance(word);
        rightAnswerFragment = RightAnswerFragment.newInstance(word);
        // cache first photo
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Glide.with(getActivity().getBaseContext())
                .load(word.getMainImageUrl())
                .downloadOnly(metrics.widthPixels,metrics.heightPixels);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(0,R.animator.slide_left)
                .replace(R.id.training_fragment_container, exerciseFragment)
                .commit();
    }

    private void uploadCompleteStartTraining(List<Word> wordList) {
        trainingWords = new ArrayList<>(wordList);
        unsortList(trainingWords);
        initView();
    }

    private void unsortList(final List<Word> wordList) {
        final Random random = new Random();
        Collections.sort(wordList, new Comparator<Word>() {
            @Override
            public int compare(Word word, Word t1) {
                return random.nextInt();
            }
        });
    }


    private class DictionaryCallback implements Callback<List<Word>> {
        @Override
        public void onResponse(Call<List<Word>> call, Response<List<Word>> response) {
            if (response.isSuccessful()) {
                progressDialog.cancel();
                uploadCompleteStartTraining(response.body());
            } else {
                onFailure(call, new ApiException());
            }
        }

        @Override
        public void onFailure(Call<List<Word>> call, Throwable t) {
            Log.e(TAG, "onFailure : ", t);
            if (isAdded() && !isDetached()) {
                if (t instanceof UnknownHostException || t instanceof TimeoutException) {
                    trainingCallback.cancelTesting(getString(R.string.error_connection_internet));
                } else if (t instanceof ApiException) {
                    trainingCallback.cancelTesting(getString(R.string.error_connection_server));
                } else {
                    trainingCallback.cancelTesting(getString(R.string.error_connection_something));
                }
            }
            progressDialog.cancel();
        }
    }
}
