package zhengzhou.individual.interview.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zhengzhou.individual.interview.R;
import zhengzhou.individual.interview.details.DetailsActivity;
import zhengzhou.individual.interview.notifications.NotificationsHelper;
import zhengzhou.individual.interview.util.GetNewsAPICompositeKey;
import zhengzhou.individual.interview.util.NewsBreakApiService;
import zhengzhou.individual.interview.util.ResponseResult;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.AdapterViewHolder> {
    private final NewsBreakApiService api = NewsBreakApiService.getInstance();
    private static final int PORGRESSBAR_TYPE = 0;
    private static final int DATA_TYPE = 1;
    private static final int FIRST_DATA_TYPE = 2;

    private List<ResponseResult.Result.Document> data;
    private boolean showLoading;
    private Context context;
    private double lgt = 122.3321;
    private double ltd = 47.6062;

    @Builder
    public NewsAdapter(List<ResponseResult.Result.Document> data, Context context) {
        this.data = data;
        showLoading = true;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return AdapterViewHolder.builder()
                .itemView(LayoutInflater.from(parent.getContext()).inflate(getLayoutByViewType(viewType),
                        parent, false))
                .build();
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.AdapterViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == PORGRESSBAR_TYPE) {
            GetNewsAPICompositeKey key = GetNewsAPICompositeKey
                    .builder()
                    .applicationName("interview")
                    .tokenName("hA7lIIPoxZWmhF9wd4muThQGiJzUwwW0")
                    .latitude(String.valueOf(ltd))
                    .longitude(String.valueOf(lgt))
                    .build();
            lgt -= 5;
            ltd += 5;
            api.callGetNewsApiObserver(key)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseResult>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseResult value) {
                            if (value.result.size() == 0 || value.result.get(0).documents.size() == 0) {
                                showLoading = false;
                                return;
                            }
                            data.addAll(value.result.get(0).documents);
                            notifyDataSetChanged();
                            NotificationsHelper.
                                    getInstance(context.getApplicationContext()).createNotification();
                        }

                        @Override
                        public void onError(Throwable e) {
                            showLoading = false;
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            return;
        }
        int index = position;
        if (type == FIRST_DATA_TYPE) {
            holder.getTextView().setText(data.get(index).titleText);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(data.get(index).imageSource))
                    .setAutoPlayAnimations(true)
                    .build();
            holder.getImageView().setController(controller);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("imageurl", data.get(index).imageSource);
                    bundle.putString("text", data.get(index).summary);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
            return;
        }
        holder.getTextView().setText(data.get(index).titleText);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(data.get(index).imageSource))
                .setAutoPlayAnimations(true)
                .build();
        holder.getImageView().setController(controller);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("imageurl", data.get(index).imageSource);
                bundle.putString("text", data.get(index).summary);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showLoading ?  data.size() + 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoading) {
            if (position < data.size()) {
                if (position == 0) {
                    return FIRST_DATA_TYPE;
                } else {
                    return DATA_TYPE;
                }
            } else {
                return PORGRESSBAR_TYPE;
            }
        } else {
            if (position == 0) {
                return FIRST_DATA_TYPE;
            } else {
                return DATA_TYPE;
            }
        }
    }

    @Getter
    @Setter
    public static final class AdapterViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView imageView;
        private TextView textView;
        private Button button;

        @Builder
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView.findViewById(R.id.image_view) == null) {
                // progress bar or button
                button = itemView.findViewById(R.id.button);
                return;
            }
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
        }
    }

    @LayoutRes
    private int getLayoutByViewType(int viewType) {
        switch (viewType) {
            case PORGRESSBAR_TYPE:
                return R.layout.progressbar;
            case DATA_TYPE:
                return R.layout.item_view;
            case FIRST_DATA_TYPE:
                return R.layout.item_view_first;
        }
        return -1;
    }
}
