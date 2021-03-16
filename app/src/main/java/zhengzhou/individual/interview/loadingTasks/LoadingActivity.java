package zhengzhou.individual.interview.loadingTasks;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zhengzhou.individual.interview.R;
import zhengzhou.individual.interview.loadingTasks.utils.UIRunnable;

import static zhengzhou.individual.interview.loadingTasks.LoadingAdapter.MESSAGE_POST_PROGRESS;

public class LoadingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private final Handler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(LoadingAdapter
                .builder()
                .context(this)
                .data(new ArrayList<>())
                .handler(this.handler)
                .build());
    }

    static class MyHandler extends Handler {

        WeakReference<Activity> wf;

        MyHandler(Activity activity) {
            wf = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (wf.get() != null) {
                if (msg.what == MESSAGE_POST_PROGRESS) {
                    ((UIRunnable) msg.obj).run();
                }
            }
        }
    }
}