package zhengzhou.individual.catsDj.loadingTasks.utils;

import android.widget.ProgressBar;

import lombok.Builder;

public final class UIRunnable implements Runnable{
    private  ProgressBar bar;
    private int value;

    @Builder
    public UIRunnable(ProgressBar bar, int value) {
        this.bar = bar;
        this.value = value;
    }

    @Override
    public void run() {
        if (value != -1) {
            bar.setProgress(value);
        }
    }
}
