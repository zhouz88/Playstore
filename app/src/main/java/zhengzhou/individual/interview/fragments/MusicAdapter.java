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
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zhengzhou.individual.interview.MainActivity;
import zhengzhou.individual.interview.R;
import zhengzhou.individual.interview.loadingTasks.utils.ThreadPoolUtil;
import zhengzhou.individual.interview.musicdetails.MusicDetailsActivity;
import zhengzhou.individual.interview.musticdetailsbackground.MusicDetailsBackgroundActivity;
import zhengzhou.individual.interview.sqlite.LikeStatus;
import zhengzhou.individual.interview.sqlite.Storage;
import zhengzhou.individual.interview.util.CloudMusicService;
import zhengzhou.individual.interview.util.SongImageResult;

import static zhengzhou.individual.interview.sqlite.Storage.copyIds;

public final class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.AdapterViewHolder> {

    @Getter
    private List<SongImageResult.Al> data;
    private List<SongImageResult.Al> tempList = new ArrayList<>();
    private int count = 0;
    private boolean showLoading = true;
    private final Object mutex = new Object();
    private final Object mutex2 = new Object();
    private final Context context;

    private static final int PORGRESSBAR_TYPE = 0;
    private static final int DATA_TYPE = 1;
    private static final int FULL_PORGRESSBAR_TYPE = 2;
    private CloudMusicService service;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Builder
    public MusicAdapter(Context context, List<SongImageResult.Al> data) {
        this.context = context;
        this.service = CloudMusicService.getInstance();
        this.data = data;
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
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int pos) {
        int viewType = getItemViewType(pos);
        if (viewType == PORGRESSBAR_TYPE || viewType == FULL_PORGRESSBAR_TYPE) {
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
                                    for (int j = 0; j < tempList.size(); j++) {
                                        ThreadPoolUtil.getService().execute(new MyR(tempList, j));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Something went wrong!",
                                            Toast.LENGTH_LONG).show();
                                    tempList = new ArrayList<>();
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
            return;
        }
        final int position = pos;
        holder.getTextView().setText(data.get(position).name);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(data.get(position).picUrl))
                .setAutoPlayAnimations(true)
                .build();
        if (data.get(position).like) {
            holder.getLove().setCompoundDrawablesWithIntrinsicBounds(
                    context.getDrawable(R.drawable.ic_favor_red),
                    null, null, null);
        } else {
            holder.getLove().setCompoundDrawablesWithIntrinsicBounds(
                    context.getDrawable(R.drawable.ic_favorite_gray_18dp),
                    null, null, null);
        }
        holder.getLove().setOnClickListener(new AdapterListener(holder, data));
        holder.getImageView().setController(controller);
        holder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Storage.musicConfig == 0) {
                    Intent intent = new Intent(context, MusicDetailsActivity.class);
                    intent.putExtra("imageUrl", data.get(position).picUrl);
                    intent.putExtra("musicLike", data.get(position).like);
                    intent.putExtra("musicId", data.get(position).id + "");
                    intent.putExtra("musicName", data.get(position).name + "");
                    ((MainActivity)context).startActivityForResult(intent, 404);
                } else {
                    Intent intent = new Intent(context, MusicDetailsBackgroundActivity.class);
                    intent.putExtra("imageUrl", data.get(position).picUrl);
                    intent.putExtra("musicLike", data.get(position).like);
                    intent.putExtra("musicId", data.get(position).id + "");
                    intent.putExtra("musicName", data.get(position).name + "");
                    ((MainActivity)context).startActivityForResult(intent, 404);
                }
            }
        });
    }

    class AdapterListener implements View.OnClickListener {
        RecyclerView.ViewHolder viewHolder;
        private List<SongImageResult.Al> data;

        public AdapterListener(RecyclerView.ViewHolder viewHolder, List<SongImageResult.Al> data) {
            this.viewHolder = viewHolder;
            this.data = data;
        }

        @Override
        public void onClick(View v) {
            SongImageResult.Al item = data.get(viewHolder.getAdapterPosition());
            ThreadPoolUtil.getService().execute(new ClickRun(item.id));
            item.like = !item.like;
            MusicAdapter.this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return showLoading ? 1 + data.size() : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoading) {
            if (data.size() == 0) {
                return FULL_PORGRESSBAR_TYPE;
            }
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
        private TextView love;

        @Builder
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView.findViewById(R.id.text_view) == null) {
                return;
            }
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
            love = itemView.findViewById(R.id.shot_view_count);
        }
    }

    @LayoutRes
    private int getLayoutByViewType(int viewType) {
        switch (viewType) {
            case PORGRESSBAR_TYPE:
                return R.layout.progressbar;
            case DATA_TYPE:
                return R.layout.item_music;
            case FULL_PORGRESSBAR_TYPE:
                return R.layout.full_pro;
        }
        return -1;
    }

    class MyR implements Runnable {
        final List<SongImageResult.Al> tempList;
        final int j;

        public MyR(List<SongImageResult.Al> tempList, int j) {
            this.tempList = tempList;
            this.j = j;
        }

        @Override
        public void run() {
            LikeStatus likeStatus = Storage.db.likeStatusDao().findByName(tempList.get(j).id);
            if (likeStatus != null) {
                tempList.get(j).like = true;
            }
            synchronized (mutex2) {
                count++;
                if (count == tempList.size()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            data.addAll(tempList);
                            tempList.clear();
                            count = 0;
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }

    public static class ClickRun implements Runnable {
        final long  item;

        public ClickRun(long item) {
            this.item = item;
        }

        @Override
        public void run() {
            LikeStatus likeStatus = Storage.db.likeStatusDao().findByName(item);
            if (likeStatus == null) {
                likeStatus = new LikeStatus();
                likeStatus.uid = item;
                Storage.db.likeStatusDao().insert(likeStatus);
            } else {
                likeStatus = new LikeStatus();
                likeStatus.uid = item;
                Storage.db.likeStatusDao().delete(likeStatus);
            }
        }
    }
}

