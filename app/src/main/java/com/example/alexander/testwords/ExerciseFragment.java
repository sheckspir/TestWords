package com.example.alexander.testwords;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alexander.testwords.model.Word;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExerciseFragment extends Fragment {
    private static final String ARG_WORD = "word";
    private static final int COUNT_VARIANTS = 4;

    @BindView(R.id.textview_word)
    TextView wordTextView;
    @BindView(R.id.button_answer_first)
    Button firstButton;
    @BindView(R.id.button_answer_second)
    Button secondButton;
    @BindView(R.id.button_answer_third)
    Button thirdButton;
    @BindView(R.id.button_answer_fourth)
    Button fourthButton;

    private TrainingCallback trainingCallback;
    private Word word;

    public static ExerciseFragment newInstance(Word word) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_WORD,word);
        ExerciseFragment fragment = new ExerciseFragment();
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
        //+
        if (getArguments().containsKey(ARG_WORD)) {
            word = (Word) getArguments().getSerializable(ARG_WORD);
        } else {
            throw new IllegalArgumentException(ExerciseFragment.class.getSimpleName() + " should be created with newInstance method");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise,container,false);
        ButterKnife.bind(this,view);

        initView(word);
        return view;
    }

    @OnClick({R.id.button_answer_first, R.id.button_answer_second, R.id.button_answer_third, R.id.button_answer_fourth})
    public void onClickAnswer(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            int textColor;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textColor = getActivity().getColor(R.color.text_dark_button);
            } else {
                //noinspection deprecation
                textColor = getResources().getColor(R.color.text_dark_button);
            }
            button.setTextColor(textColor);
            int drawableResource;
            if (word.getText().equals((button).getText())) {
                showCard(true);
                //Deprecated же
                //ну вообще конечно да, но так не хотелось писать вот эту конструкцию, как здесь так и с getColor выше
                drawableResource = R.drawable.right_answer_button;
            } else {
                showCard(false);
                drawableResource = R.drawable.wrong_answer_button;
            }
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                drawable = getActivity().getDrawable(drawableResource);
            } else {
                //noinspection deprecation
                drawable = getResources().getDrawable(drawableResource);
            }
            button.setBackground(drawable);
        }
    }

    @OnClick(R.id.button_answer_not_remember)
    public void onNotRemember() {
        showCard(false);
    }

    private void showCard(boolean right) {
        trainingCallback.onAnswerGiven(right);
    }

    private void initView(Word word) {
        wordTextView.setText(word.getTranslation());
        List<String> shuffleAlternatives = word.getVariants(COUNT_VARIANTS);

        //Ну RecyclerView же) в крайнем случае LinearLayout
        // *"в крайнем случае ListView"? Да это я чё-то затупил....
        firstButton.setText(shuffleAlternatives.get(0));
        secondButton.setText(shuffleAlternatives.get(1));
        thirdButton.setText(shuffleAlternatives.get(2));
        fourthButton.setText(shuffleAlternatives.get(3));
    }
}
