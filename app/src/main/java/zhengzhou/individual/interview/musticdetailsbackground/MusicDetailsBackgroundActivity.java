package zhengzhou.individual.interview.musticdetailsbackground;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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


public final class MusicDetailsBackgroundActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_main_play;
    private Button btn_main_stop;
    private Button btn_main_pause;
    private Button btn_main_exit;
    private CloudMusicService service = CloudMusicService.getInstance();
    private String mp3Id;
    private String mp3Url;
    private ObjectAnimator mAnimator;
    private SimpleDraweeView iv;
    private SeekBar seekbar;
    private Timer mTimer = new Timer();
    private ImageView toolbar_right_icon;

    private boolean started = false;
    private boolean hasPlayer = false;
    private boolean isPlayerPlaying = false;

    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if (!hasPlayer)
                return;
            if (isPlayerPlaying && seekbar.isPressed() == false) {
                Message msg = Message.obtain();
                msg.what = 3323;
                handler.sendMessage(msg); // 发送消息
            }
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 3323) {
                Intent intent = new Intent( MusicDetailsBackgroundActivity.this, BaseService.class);
                intent.putExtra("action", "timer");
                startService(intent);
            }
        }
    };

    private MsgReceiver msgReceiver;
    private PositionReceiver positionReceiver;
    private boolean intentToServiceUpdated = true;
   //All services must be represented by
   // <service> elements in the manifest file. Any that are not declared
   // there will not be seen by the system and will never be run.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);
        setContentView(R.layout.activity_music_details);
        this.toolbar_right_icon = findViewById(R.id.toolbar_right_icon);
        positionReceiver = new PositionReceiver();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("com.example.communication.RECEIVERS");
        registerReceiver(positionReceiver, intentFilter1);
        super.onCreate(savedInstanceState);
        Intent intent = new Intent( MusicDetailsBackgroundActivity.this, BaseService.class);
        stopService(intent);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_launcher_foreground);
        toolbar.setTitle("");
        ((TextView) toolbar.findViewById(R.id.toolbar_news)).setText("Music");
        setSupportActionBar(toolbar);

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
                                        likeStatus != null ? MusicDetailsBackgroundActivity.this.getDrawable(R.drawable.like_60dp) :
                                                MusicDetailsBackgroundActivity.this.getDrawable(R.drawable.like_60_dp_black) ,
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
                                                                l != null ? MusicDetailsBackgroundActivity.this.getDrawable(R.drawable.like_60dp) :
                                                                        MusicDetailsBackgroundActivity.this.getDrawable(R.drawable.like_60_dp_black) ,
                                                                null, null, null);
                                                        like =  l != null ? true: false;
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
        TextView textView = findViewById(R.id.music_text);
        textView.setText(getIntent().getStringExtra("musicName"));
        mp3Id = getIntent().getStringExtra("musicId");
        like = getIntent().getBooleanExtra("musicLike" ,false);
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
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setFalse();
                                    Intent intent = new Intent( MusicDetailsBackgroundActivity.this, BaseService.class);
                                    intent.putExtra("action", "play");
                                    intent.putExtra("url", mp3Url);
                                    startService(intent);
                                    initAnimator();
                                    mTimer.schedule(timerTask, 0, 1000);
                                }
                            });
                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MusicDetailsBackgroundActivity.this,
                                            "Cannot load mp3 url", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            } else {
                setFalse();
                Intent intent = new Intent( MusicDetailsBackgroundActivity.this, BaseService.class);
                intent.putExtra("action", "play");
                intent.putExtra("url", mp3Url);
                startService(intent);
                mAnimator.resume();
            }
        } else if (v == btn_main_stop) {
            if (intentToServiceUpdated && hasPlayer) {
                Intent intent = new Intent( MusicDetailsBackgroundActivity.this, BaseService.class);
                setFalse();
                intent.putExtra("action", "stop");
                startService(intent); //Asynchronous. It will not even begin doing any work until after you return from whatever callback you are in (e.g., onCreate(), onListItemClick()).
                started = false;
                seekbar.setProgress(0);
                mAnimator.pause();
            }
        } else if (v == btn_main_pause) {
            if (intentToServiceUpdated && hasPlayer && isPlayerPlaying) {
                setFalse();
                Intent intent = new Intent( MusicDetailsBackgroundActivity.this, BaseService.class);
                intent.putExtra("action", "pause");
                startService(intent);
                started = false;
                mAnimator.pause();
            }
        } else if (v == btn_main_exit) {
            Intent intent = new Intent(MusicDetailsBackgroundActivity.this, BaseService.class);
            stopService(intent);
            Intent intent1 = new Intent();
            intent1.putExtra("mp3id", mp3Id);
            intent1.putExtra("mp3like", like);
            setResult(RESULT_OK, intent1);
            finish();
        }
    }

    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isPlayerPlaying = intent.getBooleanExtra("playing", false );
            hasPlayer = intent.getBooleanExtra("hasPlayer", false);
            intentToServiceUpdated = true;
            btn_main_exit.setClickable(true);
            btn_main_pause.setClickable(true);
            btn_main_play.setClickable(true);
            btn_main_stop.setClickable(true);
        }
    }

    public void setFalse() {
        btn_main_exit.setClickable(false);
        btn_main_pause.setClickable(false);
        btn_main_play.setClickable(false);
        btn_main_stop.setClickable(false);
        intentToServiceUpdated = false;
    }

    public class PositionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("getCurrentPosition", -1);
            int duration = intent.getIntExtra("getDuration", -1);
            if (duration > 0) {
                long pos = seekbar.getMax() * position / duration;
                seekbar.setProgress((int) pos);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
        unregisterReceiver(positionReceiver);
    }
boolean like = false;
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
