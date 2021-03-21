package zhengzhou.individual.interview.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import zhengzhou.individual.interview.R;
import zhengzhou.individual.interview.loadingTasks.utils.ThreadPoolUtil;
import zhengzhou.individual.interview.util.CloudMusicService;
import zhengzhou.individual.interview.util.SongImageResult;

import static zhengzhou.individual.interview.sqlite.Storage.copyIds;

public class AppInfoFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_edit, container, false);
        return view;
    }

    private EditText textV;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("https://p1.music.126.net/4DRm5E8ahUJu5r1c4cNNbQ==/2450811418334469.jpg"))
                .setAutoPlayAnimations(true)
                .build();
        textV = view.findViewById(R.id.edittext);
        ((SimpleDraweeView) view.findViewById(R.id.image_view)).setController(controller);
        ((SimpleDraweeView) view.findViewById(R.id.image_view)).setOnClickListener(this);
    }

    private final Object mutex = new Object();
int count = 0;
    @Override
    public void onClick(View v) {
        Handler handler = new Handler(Looper.getMainLooper());
        String[] t = textV.getText().toString().split("\n");
        for (final String str : t) {
            ThreadPoolUtil.getService().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        CloudMusicService.getInstance().getApi().getSongImageById(str.trim()).execute().body();
                        synchronized (mutex) {
                            count++;
                            if (!copyIds.contains(str) && "".equals(str)) {
                                copyIds.add(str);
                            }
                            if (count == t.length) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "ok!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "ids are wrong!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }

    }
}
