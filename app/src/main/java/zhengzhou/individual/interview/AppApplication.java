package zhengzhou.individual.interview;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import zhengzhou.individual.interview.sqlite.Storage;
import zhengzhou.individual.interview.util.ImagePipelineConfigFactory;

public class AppApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
        Storage.init(this);
    }
}
