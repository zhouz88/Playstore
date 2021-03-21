package zhengzhou.individual.interview.musicdetails;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Timer;
import java.util.TimerTask;

import zhengzhou.individual.interview.R;
import zhengzhou.individual.interview.fragments.LikeBottomSheetFragment;
import zhengzhou.individual.interview.loadingTasks.utils.ThreadPoolUtil;
import zhengzhou.individual.interview.service.BaseService;
import zhengzhou.individual.interview.sqlite.LikeStatus;
import zhengzhou.individual.interview.sqlite.Storage;
import zhengzhou.individual.interview.util.CloudMusicService;
import zhengzhou.individual.interview.util.SongResult;

public final class MusicDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_main_play;
    private Button btn_main_stop;
    private Button btn_main_pause;
    private Button btn_main_exit;
    private ImageView toolbar_right_icon;
    private CloudMusicService service = CloudMusicService.getInstance();
    private String mp3Id;
    private String mp3Url;
    private ObjectAnimator mAnimator;
    private SimpleDraweeView iv;
    private SeekBar seekbar;
    private Timer mTimer = new Timer();

    private MusicService.NewBinder mBinder;
    private MusicService musicService;
    private boolean mBound = false;
    private boolean started = false;
    private boolean like;

    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if (musicService.getPlayer() == null)
                return;
            if (musicService.getPlayer().isPlaying() && seekbar.isPressed() == false) {
                Message msg = Message.obtain();
                msg.what = 3323;
                handler.sendMessage(msg); // 发送消息
            }
        }
    };

    /*
      两种service的区别：
      使用startService()方法启用服务，调用者与服务之间没有关连，即使调用者退出了，
      服务仍然运行。 使用bindService()方法启用服务，调用者与服务绑定在了一起，调用者一旦退出，服务也就终止。
    */

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 3323) {
                int position = musicService.getPlayer().getCurrentPosition();
                int duration = musicService.getPlayer().getDuration();
                if (duration > 0) {
                    long pos = seekbar.getMax() * position / duration;
                    seekbar.setProgress((int) pos);
                }
            }
        }
    };

    private ServiceConnection connection;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MusicDetailsActivity.this, MusicService.class);
        connection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder mBinder) {
                MusicDetailsActivity.this.mBinder = (MusicService.NewBinder) mBinder;
                MusicDetailsActivity.this.musicService = MusicDetailsActivity.this.mBinder.getService();
                btn_main_play.setClickable(true);
                btn_main_stop.setClickable(true);
                btn_main_pause.setClickable(true);
                btn_main_exit.setClickable(true);
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBinder = null;
                mBound = false;
            }
        };
        MusicDetailsActivity.this.bindService(intent, connection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(this, BaseService.class);
        stopService(intent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_launcher_foreground);
        toolbar.setTitle("");
        ((TextView) toolbar.findViewById(R.id.toolbar_news)).setText("Record");
        setSupportActionBar(toolbar);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(getIntent().getStringExtra("imageUrl"))
                .setAutoPlayAnimations(true)
                .build();
        iv = findViewById(R.id.image_view);
        iv.setController(controller);
        seekbar = findViewById(R.id.seekbar);

        this.btn_main_play = findViewById(R.id.btn_main_play);
        this.btn_main_stop = findViewById(R.id.btn_main_stop);
        this.btn_main_pause = findViewById(R.id.btn_main_pause);
        this.btn_main_exit = findViewById(R.id.btn_main_exit);
        this.toolbar_right_icon = findViewById(R.id.toolbar_right_icon);
        mp3Id = getIntent().getStringExtra("musicId");
        like = getIntent().getBooleanExtra("musicLike" ,false);
        toolbar_right_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolUtil.getService().execute(new Runnable() {
                    @Override
                    public void run() {
                        LikeStatus likeStatus = Storage.db.likeStatusDao().findByName(Long.parseLong(mp3Id));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                LikeBottomSheetFragment f =  new LikeBottomSheetFragment();
                                f.showNow(getSupportFragmentManager(),"tags");
                                TextView textView = f.getTextView();
                                textView.setCompoundDrawablesWithIntrinsicBounds(
                                       likeStatus != null ? MusicDetailsActivity.this.getDrawable(R.drawable.like_60dp) :
                                               MusicDetailsActivity.this.getDrawable(R.drawable.like_60_dp_black) ,
                                        null, null, null);
                                like = likeStatus != null ? true: false;
                                textView.setClickable(true);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ThreadPoolUtil.getService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                LikeStatus likeStatus = Storage.db.likeStatusDao().findByName(Long.parseLong(mp3Id));
                                                if (likeStatus == null) {
                                                    likeStatus = new LikeStatus();
                                                    likeStatus.uid = Long.parseLong(mp3Id);
                                                    Storage.db.likeStatusDao().insert(likeStatus);
                                                } else {
                                                    likeStatus = new LikeStatus();
                                                    likeStatus.uid = Long.parseLong(mp3Id);
                                                    Storage.db.likeStatusDao().delete(likeStatus);
                                                }
                                                final LikeStatus l = Storage.db.likeStatusDao().findByName(Long.parseLong(mp3Id));
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        textView.setCompoundDrawablesWithIntrinsicBounds(
                                                                l != null ? MusicDetailsActivity.this.getDrawable(R.drawable.like_60dp) :
                                                                        MusicDetailsActivity.this.getDrawable(R.drawable.like_60_dp_black) ,
                                                                null, null, null);
                                                        like = l!= null ? true: false;
                                                        f.dismiss();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });
            }
        });

        TextView textView = findViewById(R.id.music_text);
        textView.setText(getIntent().getStringExtra("musicName"));
        btn_main_play.setOnClickListener(this);
        btn_main_stop.setOnClickListener(this);
        btn_main_pause.setOnClickListener(this);
        btn_main_exit.setOnClickListener(this);
    }

    private void initAnimator() {
        mAnimator = ObjectAnimator.ofFloat(iv, "rotation", 0.0f, 360.0f);
        mAnimator.setDuration(5000); //设定转一圈的时间
        mAnimator.setRepeatCount(Animation.INFINITE); //设定无限循环
        mAnimator.setRepeatMode(ObjectAnimator.RESTART); // 循环模式
        mAnimator.setInterpolator(new LinearInterpolator()); // 匀速
        mAnimator.start(); //动画开始
        mAnimator.resume();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_main_play && !started) {
            started = true;
            if (mp3Url == null) {
                ThreadPoolUtil.getService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final SongResult songResult =
                                    service.getApi().getSongById(mp3Id).execute().body();
                            mp3Url = songResult.data.get(0).url;
                            if (mp3Url == null) {
                                throw new RuntimeException("mp3 url is null");
                            }
                            handler.post(new MyR(mp3Url));
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MusicDetailsActivity.this,
                                            "Network error", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            } else {
                musicService.playMusic(mp3Url);
                mAnimator.resume();
            }
        } else if (v == btn_main_stop) {
            if (musicService.getPlayer() != null) {
                musicService.stopMusic();
                started = false;
                seekbar.setProgress(0);
                mAnimator.pause();
            }
        } else if (v == btn_main_pause) {
            if (musicService.getPlayer() != null && musicService.getPlayer().isPlaying()) {
                musicService.pauseMusic();
                started = false;
                mAnimator.pause();
            }
        } else if (v == btn_main_exit) {
            unbindService(connection);
            connection = null;
            Intent intent = new Intent();
            intent.putExtra("mp3id", mp3Id);
            intent.putExtra("mp3like", like);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("mp3id", mp3Id);
            intent.putExtra("mp3like", like);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyR implements Runnable {
        private final String url;

        private MyR(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            musicService.playMusic(mp3Url);
            initAnimator();
            mTimer.schedule(timerTask, 0, 1000);
        }
    }

//    @Override
//    protected void onStop() {
//        if (connection != null)
//            unbindService(connection);
//        connection = null;
//        mBinder = null;
//        mBound = false;
//        super.onStop();
//    }
}