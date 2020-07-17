package brainee.innhub.qzapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class QuizFragment extends Fragment implements View.OnClickListener {

    private FirebaseFirestore firestore;
    private String quizId;
    private TextView quizTitle;

    private Button option_one_btn;
    private Button option_two_btn;
    private Button option_three_btn;
    private Button nextBtn;
    private ImageButton closeBtn;
    private TextView questionFeedback;
    private TextView questionText;
    private TextView questionTime;
    private ProgressBar questionProgress;
    private TextView questionNumber;
    private boolean canAnswer = false;
    private int currentQuestion;


    private List<QuestionsModel> allQuestionsList = new ArrayList<>();
    private long totalQuestionsToAnswer = 10;
    private List<QuestionsModel> questionsToAnswer = new ArrayList<>();
    private CountDownTimer countDownTimer;

    public QuizFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quizTitle = view.findViewById(R.id.quiz_title);
        option_one_btn = view.findViewById(R.id.quiz_option_one);
        option_two_btn = view.findViewById(R.id.quiz_option_two);
        option_three_btn = view.findViewById(R.id.quiz_option_three);
        nextBtn = view.findViewById(R.id.quiz_next_btn);
        questionFeedback = view.findViewById(R.id.quiz_question_feedback);
        questionText = view.findViewById(R.id.quiz_question);
        questionTime = view.findViewById(R.id.quiz_question_time);
        questionProgress = view.findViewById(R.id.quiz_question_progress);
        questionNumber = view.findViewById(R.id.quiz_question_number);

        //Get quizId
        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();

        totalQuestionsToAnswer = QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestions();

        firestore = FirebaseFirestore.getInstance();
        firestore.collection("QuizList")
                .document(quizId).collection("Questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            allQuestionsList = task.getResult().toObjects(QuestionsModel.class);
                            pickQuestions();
                            loadUi();
                        } else {
                            quizTitle.setText("Error :" + task.getException().getMessage());
                        }
                    }
                });

        option_one_btn.setOnClickListener(this);
        option_two_btn.setOnClickListener(this);
        option_three_btn.setOnClickListener(this);
    }

    private void loadUi() {
        //Quiz Data loaded , load the UI
        quizTitle.setText("Quiz data loaded");
        questionText.setText("Load First Question");

        enableOptions();

        //Load first question
        loadQuestions(1);
    }

    private void loadQuestions(int questNumber) {
        /*Set Question Number*/
        questionNumber.setText(questNumber + "");

        /*Load question text*/
        questionText.setText(questionsToAnswer.get(questNumber - 1).getQuestion());

        /*Load Options*/
        option_one_btn.setText(questionsToAnswer.get(questNumber - 1).getOption_a());
        option_two_btn.setText(questionsToAnswer.get(questNumber - 1).getOption_b());
        option_three_btn.setText(questionsToAnswer.get(questNumber - 1).getOption_c());

        /*Set can answer to true*/
        canAnswer = true;
        currentQuestion = questNumber;

        //Start Question Timer
        startTimer(questNumber);
    }

    private void startTimer(int questionNumber) {
        /*Set timer text*/
        final Long timeToAnswer = questionsToAnswer.get(questionNumber - 1).getTimer();
        questionText.setText(timeToAnswer.toString());

        /*Show Timer progress*/
        questionProgress.setVisibility(View.VISIBLE);

        /*Start Countdown*/
        countDownTimer = new CountDownTimer(timeToAnswer * 1000, 10) {

            @Override
            public void onTick(long millisUntilFinished) {

                //Update time
                questionTime.setText(millisUntilFinished / 1000 + "");

                /*Progress in percent*/
                Long percent = millisUntilFinished / (timeToAnswer * 10);
                questionProgress.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {

                canAnswer = true;
            }
        };
        countDownTimer.start();
    }

    private void enableOptions() {
        option_one_btn.setVisibility(View.VISIBLE);
        option_two_btn.setVisibility(View.VISIBLE);
        option_three_btn.setVisibility(View.VISIBLE);

        option_one_btn.setEnabled(true);
        option_two_btn.setEnabled(true);
        option_three_btn.setEnabled(true);

        //Hide feedback text & next Button
        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
    }

    private void pickQuestions() {
        for (int i = 0; i < totalQuestionsToAnswer; i++) {
            int randomNumber = getRandomInteger(allQuestionsList.size(), 0);

            questionsToAnswer.add(allQuestionsList.get(randomNumber));
            allQuestionsList.remove(randomNumber);
            // Toast.makeText(getContext(), "Questions: " + i + ":" + questionsToAnswer.get(i).getQuestion(), Toast.LENGTH_SHORT).show();
        }
    }

    public static int getRandomInteger(int maximum, int minimum) {
        return ((int) (Math.random() * (maximum - minimum))) + minimum;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.quiz_option_one:
                verifyAnswer(option_one_btn);
                break;
            case R.id.quiz_option_two:
                verifyAnswer(option_two_btn);
                break;
            case R.id.quiz_option_three:
                verifyAnswer(option_three_btn);
                break;
        }
    }

    private void verifyAnswer(Button selectedAnswerBtn) {

        if (canAnswer) {
            if (questionsToAnswer.get(currentQuestion - 1).getAnswer().equals(selectedAnswerBtn.getText())) {
                //Correct Answer
                selectedAnswerBtn.setBackground(getResources().getDrawable(R.drawable.correct_ans_btn_bg, null));
            } else {
                //Wrong Answer
                selectedAnswerBtn.setBackground(getResources().getDrawable(R.drawable.wrong_ans_btn_bg, null));
            }
        }

        canAnswer = false;

        /*Cancel/Stop the timer*/
        countDownTimer.cancel();
    }
}