package zhengzhou.individual.catsDj.loadingTasks;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zhengzhou.individual.catsDj.loadingTasks.utils.ThreadPoolUtil;
import zhengzhou.individual.catsDj.loadingTasks.utils.UIRunnable;
import zhengzhou.individual.catsDj.R;
import zhengzhou.individual.catsDj.notifications.NotificationsHelper;
import zhengzhou.individual.catsDj.util.NewsBreakApiService;
import zhengzhou.individual.catsDj.util.ResponseResult;

public final class LoadingAdapter extends RecyclerView.Adapter<LoadingAdapter.AdapterViewHolder> {
    private final NewsBreakApiService api = NewsBreakApiService.getInstance();
    private final int VIEW_TYPE = 0;
    private final int DATA_TYPE = 1;
    public final static int MESSAGE_POST_PROGRESS = 100;
    private List<ResponseResult.Result.Document> data;
    private boolean showLoading;
    private Context context;
    private final Handler handler;

    @Builder
    public LoadingAdapter(List<ResponseResult.Result.Document> data, Context context, Handler handler) {
        this.data = data;
        this.showLoading = true;
        this.context = context;
        this.handler = handler;
    }

    @NonNull
    @Override
    public LoadingAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return AdapterViewHolder.builder()
                .itemView(LayoutInflater.from(parent.getContext()).inflate(getLayoutByViewType(viewType),
                        parent, false))
                .build();
    }

    @Override
    public void onBindViewHolder(@NonNull LoadingAdapter.AdapterViewHolder holder, final int position) {
        int type = getItemViewType(position);
        if (type == VIEW_TYPE) {
            api.getNewsApiSingle()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
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
        holder.getButton().setOnClickListener(new View.OnClickListener() {
            private int count = 0;
            private MyRunnable runnable;
            private AdapterAsyncTask task;

            @Override
            public void onClick(View v) {
                if (count == 0) {
                    count++;
                    ((Button) v).setTextColor(Color.GRAY);
                    if (runnable == null) {
                        runnable = MyRunnable
                                .builder()
                                .data(data)
                                .handler(handler)
                                .position(position)
                                .holder(holder)
                                .build();
                    }
                    task = AdapterAsyncTask
                            .builder()
                            .callable(runnable)
                            .build();
                    runnable.setTask(task);
                    ThreadPoolUtil.getService().execute(task);
                } else {
                    ((Button) v).setTextColor(Color.WHITE);
                    count--;
                    task.cancel(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return showLoading ? 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return showLoading ? VIEW_TYPE : DATA_TYPE;
    }

    @Getter
    @Setter
    public static final class AdapterViewHolder extends RecyclerView.ViewHolder {
        private Button button;
        private ProgressBar bar;

        @Builder
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.start_load_button);
            bar = itemView.findViewById(R.id.progress_bar);
        }
    }

    private static class AdapterAsyncTask extends FutureTask {

        @Builder
        public AdapterAsyncTask(Callable callable) {
            super(callable);
        }
    }

    private static class MyRunnable implements Callable {
        private List<ResponseResult.Result.Document> data;
        private AdapterViewHolder holder;
        private Handler handler;
        private int position;
        private ByteArrayOutputStream bos = new ByteArrayOutputStream();

        @Setter
        private AdapterAsyncTask task;

        @Builder
        public MyRunnable(List<ResponseResult.Result.Document> data,
                          AdapterViewHolder holder, Handler handler, int position) {
            this.data = data;
            this.holder = holder;
            this.handler = handler;
            this.position = position;

        }

        @Override
        public Object call() throws Exception {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(data.get(position).imageSource);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(20000);
                int code = connection.getResponseCode();
                if (code == 200) {
                    InputStream is = connection.getInputStream();
                    int length = -1;
                    int progress = 0;
                    int count = connection.getContentLength();
                    byte[] bs = new byte[5];
                    if (task.isCancelled()) {
                        return null;
                    }
                    while (bos.size() > progress) {
                        progress += (length = is.read(bs));
                    }
                    while ((length = is.read(bs)) != -1) {
                        if (task.isCancelled()) {
                            return null;
                        }
                        progress += length;
                        int target = count == 0 ? -1 : (int) ((float) progress / count * 100);
                        Message msg = handler.obtainMessage(MESSAGE_POST_PROGRESS,
                                UIRunnable.builder().bar(holder.getBar()).value(target).build());
                        msg.sendToTarget();
                        bos.write(bs, 0, length);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }
    }

    @LayoutRes
    private int getLayoutByViewType(int viewType) {
        switch (viewType) {
            case VIEW_TYPE:
                return R.layout.progressbar;
            case DATA_TYPE:
                return R.layout.loading_task;
        }
        return -1;
    }
}
