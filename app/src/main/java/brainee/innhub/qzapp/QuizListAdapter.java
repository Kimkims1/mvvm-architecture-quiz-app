package brainee.innhub.qzapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {

    private List<QuizListModel> quizListModels;
    private onQuizListItemClicked onQuizListItemClicked;

    public QuizListAdapter(QuizListAdapter.onQuizListItemClicked onQuizListItemClicked) {
        this.onQuizListItemClicked = onQuizListItemClicked;
    }

    public void setQuizListModels(List<QuizListModel> quizListModels) {
        this.quizListModels = quizListModels;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);

        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {

        holder.list_title.setText(quizListModels.get(position).getName());

        String image_url = quizListModels.get(position).getImage();

        Glide
                .with(holder.itemView.getContext())
                .load(image_url)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.list_Image);

        String listDescription = quizListModels.get(position).getDesc();
        if (listDescription.length() > 150) {
            listDescription = listDescription.substring(0, 150);
        }
        holder.list_desc.setText(listDescription + "...");
        holder.list_level.setText(quizListModels.get(position).getLevel());
    }

    @Override
    public int getItemCount() {
        if (quizListModels == null) {
            return 0;
        } else {
            return quizListModels.size();
        }
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView list_Image;
        private TextView list_title;
        private TextView list_desc;
        private TextView list_level;
        private Button list_btn;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);

            list_Image = itemView.findViewById(R.id.list_image);
            list_title = itemView.findViewById(R.id.list_title);
            list_desc = itemView.findViewById(R.id.list_description);
            list_level = itemView.findViewById(R.id.list_difficult);
            list_btn = itemView.findViewById(R.id.list_btn);

            list_btn.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onQuizListItemClicked.onItemClicked(getAdapterPosition());
        }
    }

    public interface onQuizListItemClicked {
        void onItemClicked(int position);
    }
}
