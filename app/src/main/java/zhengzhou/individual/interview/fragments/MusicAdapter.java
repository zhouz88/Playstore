package zhengzhou.individual.interview.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zhengzhou.individual.interview.R;
import zhengzhou.individual.interview.loadingTasks.utils.ThreadPoolUtil;
import zhengzhou.individual.interview.musicdetails.MusicDetailsActivity;
import zhengzhou.individual.interview.util.CloudMusicService;
import zhengzhou.individual.interview.util.SongImageResult;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.AdapterViewHolder> {

    private List<String> copyIds = Arrays.asList(
            1824473080 + "",
            1494985963 + "",
            1406673720 + "",
            1436384830 + "",
            1377100917 + "",
            1814798370 + "",
            1494995397 + "",
            543986441 + "",
            1440162979 + "",
            5142104 + "");

    private List<SongImageResult.Al> data = new ArrayList<>();
    private List<SongImageResult.Al> tempList = new ArrayList<>();
    private boolean showLoading = true;
    private Object mutex = new Object();
    private final  Context context;

    private static final int PORGRESSBAR_TYPE = 0;
    private static final int DATA_TYPE = 1;
    private CloudMusicService service;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public MusicAdapter(Context context) {
        this.context = context;
        this.service = CloudMusicService.getInstance();
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return AdapterViewHolder.builder().itemView(
                LayoutInflater.from(parent.getContext()).inflate(getLayoutByViewType(viewType),
                        parent, false)
        ).build();
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == PORGRESSBAR_TYPE) {
            for (int i = 0; i < copyIds.size(); i++) {
                final String id = copyIds.get(i);
                ThreadPoolUtil.getService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final SongImageResult songResult =
                                    service.getApi().getSongImageById(id).execute().body();
                            songResult.songs.get(0).al.id = Long.parseLong(id);
                            synchronized (mutex) {
                                tempList.add(songResult.songs.get(0).al);
                                if (tempList.size() == copyIds.size()) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            data.addAll(tempList);
                                            tempList.clear();
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
            return;
        }
        holder.getTextView().setText(data.get(position).name);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(data.get(position).picUrl))
                .setAutoPlayAnimations(true)
                .build();

        holder.getImageView().setController(controller);

        holder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MusicDetailsActivity.class);
                intent.putExtra("imageUrl", data.get(position).picUrl);
                intent.putExtra("musicId", data.get(position).id+"");
                intent.putExtra("musicName", data.get(position).name+"");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showLoading ? 1 + data.size() : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoading) {
            if (position < data.size()) {
                return DATA_TYPE;
            } else {
                return PORGRESSBAR_TYPE;
            }
        } else {
            return DATA_TYPE;
        }
    }

    @Getter
    @Setter
    public static final class AdapterViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView imageView;
        private TextView textView;
        private View cover;

        @Builder
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView.findViewById(R.id.text_view) == null) {
                return;
            }
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
            cover = itemView.findViewById(R.id.cover);
        }
    }

    @LayoutRes
    private int getLayoutByViewType(int viewType) {
        switch (viewType) {
            case PORGRESSBAR_TYPE:
                return R.layout.progressbar;
            case DATA_TYPE:
                return R.layout.item_music;
        }
        return -1;
    }
}
