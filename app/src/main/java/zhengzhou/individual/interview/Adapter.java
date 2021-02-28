package zhengzhou.individual.interview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zhengzhou.individual.interview.notifications.NotificationsHelper;
import zhengzhou.individual.interview.util.NewsBreakApiService;
import zhengzhou.individual.interview.util.ResponseResult;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {
    private final NewsBreakApiService api = new NewsBreakApiService();
    private static int VIEW_TYPE = 0;
    private static int DATA_TYPE = 1;

    private List<ResponseResult.Result.Document> data;
    private boolean showLoading;
    private Context context;

    public Adapter(List<ResponseResult.Result.Document> data, Context context) {
        this.data = data;
        showLoading = true;
        this.context = context;
    }

    @NonNull
    @Override
    public Adapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE) {
            return AdapterViewHolder.builder()
                    .itemView(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar,
                            parent, false))
                    .build();
        }
        return AdapterViewHolder.builder()
                .itemView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,
                        parent, false))
                .build();
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.AdapterViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE) {
            api.getNewsApiSingle()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ResponseResult>() {
                        @Override
                        public void onSuccess(ResponseResult value) {
                            data.addAll(value.result.get(0).documents);
                            showLoading = false;
                            notifyDataSetChanged();
                            NotificationsHelper.
                                    getInstance(context.getApplicationContext()).createNotification();
                        }

                        @Override
                        public void onError(Throwable e) {
                            showLoading = false;
                            notifyDataSetChanged();
                        }
                    });
            return;
        }
        holder.getTextView().setText(data.get(position).titleText);
        Glide.with(holder.getImageView().getContext())
                .setDefaultRequestOptions
                        (new RequestOptions()
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_foreground))
                .load(data.get(position).imageSource)
                .into(holder.getImageView());
    }

    @Override
    public int getItemCount() {
        return showLoading ? 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return showLoading ? VIEW_TYPE : DATA_TYPE;
    }

    public static final class AdapterViewHolder extends RecyclerView.ViewHolder {
        @Getter
        @Setter
        private ImageView imageView;
        @Getter
        @Setter
        private TextView textView;

        @Builder
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
