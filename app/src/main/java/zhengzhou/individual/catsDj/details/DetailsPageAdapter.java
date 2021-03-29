package zhengzhou.individual.catsDj.details;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zhengzhou.individual.catsDj.R;

public class DetailsPageAdapter extends RecyclerView.Adapter {
    String imageUrl;
    String text;
    String textTitle;
    Context context;

    @Builder
    public DetailsPageAdapter(String imageUrl, Context context,
                              String text,String textTitle) {
        this.imageUrl = imageUrl;
        this.text = text;
        this.textTitle = textTitle;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return AdapterViewHolder.builder()
                .itemView(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_detail,
                        parent, false))
                .build();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((AdapterViewHolder) holder).getTextView().setText(text);
        ((AdapterViewHolder) holder).getTitleView().setText(textTitle);
        final DetailsActivity activity = (DetailsActivity) context;
        ((AdapterViewHolder) holder).getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        });
        DetailsPageAsyncTask task = new DetailsPageAsyncTask(imageUrl, (AdapterViewHolder) holder);
        task.execute();
    }


    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Getter
    @Setter
    public static final class AdapterViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private TextView titleView;
        private ProgressBar progressBar;

        @Builder
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
            progressBar = itemView.findViewById(R.id.pb);
            titleView = itemView.findViewById(R.id.title_text);
        }
    }
}
