package zhengzhou.individual.interview.musicdetails;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.SeekBar;

import java.util.Objects;

import lombok.Getter;

public class MusicService extends Service {

    @Getter
    private MediaPlayer player;

    private NewBinder mBinder = new NewBinder();

    class NewBinder extends Binder {
         MusicService getService() {
             return MusicService.this;
         }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");
        if ("play".equals(action)) {
            playMusic(intent.getStringExtra("url"));
        } else if ("pause".equals(action)) {
            pauseMusic();
        } else if ("stop".equals(action)) {
            stopMusic();
        } else if ("exit".equals(action)) {
            exitMusic();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void pauseMusic() {
        if (Objects.nonNull(player) && player.isPlaying()) {
            player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mp.pause();
                }
            });
        }
    }

    public void stopMusic() {
        if (Objects.nonNull(player)) {
            player.stop(); // 停止
            player.reset(); // 重置
            player.release(); // 释放加载的音乐资源
            player = null;   // 赋予空
        }
    }

    public void exitMusic() {
        stopMusic();
    }

    public void playMusic(String url) {
        if (Objects.isNull(player)) {
            try {
                player = new MediaPlayer();
                player.setDataSource(url);
                player.prepareAsync();
                player.setLooping(true);
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (Exception e) {

            }
        }

        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mp.start();
            }
        });

    }
}