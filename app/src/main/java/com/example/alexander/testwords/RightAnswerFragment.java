package com.example.alexander.testwords;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.alexander.testwords.model.Word;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RightAnswerFragment extends Fragment {
    private static final String ARG_WORD = "word";

    @BindView(R.id.textview_word)
    TextView wordTextView;
    @BindView(R.id.image_word)
    ImageView imageView;
    @BindView(R.id.textview_translate)
    TextView translateTextView;

    private Word word;
    private TrainingCallback trainingCallback;

    public static RightAnswerFragment newInstance(Word word) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_WORD,word);
        RightAnswerFragment fragment = new RightAnswerFragment();
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
        //А почему тут нет IllegalState?
        if (getArguments() != null && getArguments().containsKey(ARG_WORD)) {
            word = (Word) getArguments().getSerializable(ARG_WORD);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_answer,container,false);
        ButterKnife.bind(this,view);

        wordTextView.setText(word.getTranslation());
        translateTextView.setText(word.getText());
        Glide.with(this)
                .load(word.getMainImageUrl())
                .into(imageView);
        return view;
    }

    @OnClick(R.id.button_next)
    public void onClickNext() {
        trainingCallback.onAnswerCompleteShow();
    }
}
