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

import java.util.List;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private ImageView details_image;
    private TextView details_title;
    private TextView details_message;
    private TextView detailsDiff;
    private TextView detailsQuestions;

    private Button details_start_btn;

    private QuizListViewModel quizListViewModel;
    private NavController navController;
    private int position;

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

        details_start_btn.setOnClickListener(this);

        position = DetailFragmentArgs.fromBundle(getArguments()).getPosition();

        Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
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


            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.details_start_btn:
                navController.navigate(R.id.action_detailFragment_to_quizFragment2);
                break;
        }
    }
}