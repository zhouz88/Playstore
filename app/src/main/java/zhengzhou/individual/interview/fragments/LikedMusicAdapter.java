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

public class LikedMusicAdapter extends RecyclerView.Adapter<LikedMusicAdapter.AdapterViewHolder> {

    private static final int PORGRESSBAR_TYPE = 0;
    private static final int DATA_TYPE = 1;
    private static final int NOTHING_TYPE = 2;

    private final Context context;
    private final CloudMusicService service;
    @Getter
    private List<SongImageResult.Al> data;
    private List<SongImageResult.Al> tempList = new ArrayList<>();
    private final Object mutex = new Object();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean showLoading = true;

    @Builder
    public LikedMusicAdapter(Context context, List<SongImageResult.Al> data) {
        this.context = context;
        this.data = data;
        this.service = CloudMusicService.getInstance();
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return LikedMusicAdapter.AdapterViewHolder.builder().itemView(
                LayoutInflater.from(parent.getContext()).inflate(getLayoutByViewType(viewType),
                        parent, false)
        ).build();
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == PORGRESSBAR_TYPE) {
            ThreadPoolUtil.getService().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<LikeStatus> res = Storage.db.likeStatusDao().getAll();
                        if (res.size() == 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    data.addAll(tempList);
                                    showLoading = false;
                                    notifyDataSetChanged();
                                }
                            });
                            return;
                        }
                        for (LikeStatus likeStatus : res) {
                            ThreadPoolUtil.getService().execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final SongImageResult songResult =
                                                service.getApi().getSongImageById(likeStatus.uid + "").execute().body();
                                        songResult.songs.get(0).al.id = likeStatus.uid;
                                        songResult.songs.get(0).al.like = true;
                                        synchronized (mutex) {
                                            tempList.add(songResult.songs.get(0).al);
                                            if (tempList.size() == res.size()) {
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        data.addAll(tempList);
                                                        tempList.clear();
                                                        showLoading = false;
                                                        notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            return;
        }
        if (viewType == NOTHING_TYPE) {
            return;
        }
        holder.getTextView().setText(data.get(position).name);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(data.get(position).picUrl))
                .setAutoPlayAnimations(true)
                .build();
        holder.getLove().setCompoundDrawablesWithIntrinsicBounds(
                    context.getDrawable(R.drawable.ic_favor_red),
                    null, null, null);
        holder.getImageView().setController(controller);
        holder.getCover().setClickable(true);
        holder.getCover().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Storage.musicConfig == 0) {
                    Intent intent = new Intent(context, MusicDetailsActivity.class);
                    intent.putExtra("imageUrl", data.get(position).picUrl);
                    intent.putExtra("musicId", data.get(position).id + "");
                    intent.putExtra("musicLike", data.get(position).like);
                    intent.putExtra("musicName", data.get(position).name + "");
                    ((MainActivity)context).startActivityForResult(intent, 402);
                } else {
                    Intent intent = new Intent(context , MusicDetailsBackgroundActivity.class);
                    intent.putExtra("imageUrl", data.get(position).picUrl);
                    intent.putExtra("musicLike", data.get(position).like);
                    intent.putExtra("musicId", data.get(position).id + "");
                    intent.putExtra("musicName", data.get(position).name + "");
                    ((MainActivity)context).startActivityForResult(intent, 402);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return showLoading ? 1 : (data.size() == 0 ? 1 : data.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoading) {
            return PORGRESSBAR_TYPE;
        }
        if (data.size() == 0) {
            return NOTHING_TYPE;
        }
        return DATA_TYPE;
    }

    @Getter
    @Setter
    public static final class AdapterViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView imageView;
        private TextView textView;
        private TextView love;
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
            love = itemView.findViewById(R.id.shot_view_count);
        }
    }

    @LayoutRes
    private int getLayoutByViewType(int viewType) {
        switch (viewType) {
            case PORGRESSBAR_TYPE:
                return R.layout.full_pro;
            case DATA_TYPE:
                return R.layout.item_music_like;
            case NOTHING_TYPE:
                return R.layout.nothing_view;
        }
        return -1;
    }
}
