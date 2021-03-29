package zhengzhou.individual.catsDj;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import zhengzhou.individual.catsDj.sqlite.Storage;
import zhengzhou.individual.catsDj.util.ImagePipelineConfigFactory;

public class AppApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
        Storage.init(this);
    }
}
