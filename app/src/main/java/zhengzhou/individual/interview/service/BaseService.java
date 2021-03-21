package zhengzhou.individual.interview.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.Message;
import android.widget.Toast;

import java.util.Objects;

public class BaseService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private Intent intent = new Intent("com.example.communication.RECEIVER");

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                switch (msg.what) {
                    case 0:
                         playMusic((String) msg.obj);
                         break;
                    case 1:
                        stopMusic();
                        break;
                    case 2:
                        pauseMusic();
                        break;
                    case 3:
                        exitMusic();
                        break;
                }
            } catch (Exception e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    HandlerThread thread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");
        if ("play".equals(action)) {
            sendPlayMusic(intent.getStringExtra("url"));
        } else if ("pause".equals(action)) {
            sendPauseMusic();
        } else if ("stop".equals(action)) {
            sendStopMusic();
        } else if ("exit".equals(action)) {
            sendExitMusic();
        } else if ("timer".equals(action)) {
            final Intent intent1 = new Intent("com.example.communication.RECEIVERS");
            intent1.putExtra("getCurrentPosition", player == null ? 0: player.getCurrentPosition());
            intent1.putExtra("getDuration", player == null ? 0:player.getDuration());
            sendBroadcast(intent1);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendExitMusic() {
        Message msg = serviceHandler.obtainMessage();
        msg.what = 3;
        serviceHandler.sendMessage(msg);
    }

    private void sendStopMusic() {
        Message msg = serviceHandler.obtainMessage();
        msg.what = 1;
        serviceHandler.sendMessage(msg);
    }


    private void sendPauseMusic() {
        Message msg = serviceHandler.obtainMessage();
        msg.what = 2;
        serviceHandler.sendMessage(msg);
    }

    private void sendPlayMusic(String url) {
        Message msg = serviceHandler.obtainMessage();
        msg.what = 0;
        msg.obj = url;
        serviceHandler.sendMessage(msg);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.stop(); // 停止
            player.reset(); // 重置
            player.release(); // 释放加载的音乐资源
            player = null;   // 赋予空
        }
        thread.quitSafely();
    }

    private MediaPlayer player;

    private void pauseMusic() {
        if (Objects.nonNull(player) && player.isPlaying()) {
            player.pause();
            intent.putExtra("playing", false);
            intent.putExtra("hasPlayer", true);
            sendBroadcast(intent);
        }
    }

    private void stopMusic() {
        if (Objects.nonNull(player)) {
            player.stop(); // 停止
            player.reset(); // 重置
            player.release(); // 释放加载的音乐资源
            player = null;   // 赋予空
            intent.putExtra("playing", false);
            intent.putExtra("hasPlayer", false);
            sendBroadcast(intent);
        }
    }

    private void exitMusic() {
        stopMusic();
    }

    private void playMusic(String url) {
        if (Objects.isNull(player)) {
            try {
                player = new MediaPlayer();
                player.setDataSource(url);
                player.prepare();
                player.setLooping(true);
                player.start();
                intent.putExtra("playing", true);
                intent.putExtra("hasPlayer", true);
                sendBroadcast(intent);
                return;
            } catch (Exception e) {

            }
        }

        player.start();
        intent.putExtra("playing", true);
        intent.putExtra("hasPlayer", true);
        sendBroadcast(intent);
    }
}

