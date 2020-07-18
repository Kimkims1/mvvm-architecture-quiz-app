package brainee.innhub.qzapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private ImageView details_image;
    private TextView details_title;
    private TextView details_message;
    private TextView detailsDiff;
    private TextView detailsQuestions;
    private TextView detailsScore;

    private Button details_start_btn;

    private QuizListViewModel quizListViewModel;
    private NavController navController;
    private int position;
    private String quizId;

    private long totalQuestions = 0;

    public DetailFragment() {
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
        return inflater.inflate(R.layout.fragment_detail, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        details_image = view.findViewById(R.id.details_image);
        details_message = view.findViewById(R.id.details_desc);
        details_title = view.findViewById(R.id.details_title);
        detailsDiff = view.findViewById(R.id.details_difficult_text);
        detailsQuestions = view.findViewById(R.id.details_question_text);
        details_start_btn = view.findViewById(R.id.details_start_btn);
        detailsScore = view.findViewById(R.id.details_score);

        details_start_btn.setOnClickListener(this);

        position = DetailFragmentArgs.fromBundle(getArguments()).getPosition();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {

                Glide
                        .with(getContext())
                        .load(quizListModels.get(position).getImage())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(details_image);

                details_title.setText(quizListModels.get(position).getName());
                details_message.setText(quizListModels.get(position).getDesc());
                detailsDiff.setText(quizListModels.get(position).getLevel());
                detailsQuestions.setText(quizListModels.get(position).getQuestions() + "");

                //Assign value to quiz Variable
                quizId = quizListModels.get(position).getQuiz_id();
                totalQuestions = quizListModels.get(position).getQuestions();

                loadResultsData();
            }
        });
    }

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private void loadResultsData() {

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("QuizList").document(quizId)
                .collection("Results")
                .document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot resultSnapshot = task.getResult();

                            Long correct = resultSnapshot.getLong("correct");
                            Long wrong = resultSnapshot.getLong("wrong");
                            Long missed = resultSnapshot.getLong("unanswered");


                            //Calculate progress
                            Long total = correct + wrong + missed;
                            Long percent = (correct * 100) / total;

                            detailsScore.setText(percent + "%");
                        } else {
                            //Document doesn't exist

                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.details_start_btn:
                DetailFragmentDirections.ActionDetailFragmentToQuizFragment2 action = DetailFragmentDirections.actionDetailFragmentToQuizFragment2();
                action.setTotalQuestions(totalQuestions);
                action.setQuizId(quizId);
                navController.navigate(action);
                break;
        }
    }
}