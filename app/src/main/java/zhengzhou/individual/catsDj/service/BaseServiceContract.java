package zhengzhou.individual.catsDj.service;

public interface BaseServiceContract {
    void initService();

    void onTaskMoved();

    void onDestroy();

    interface ReceiverPresenter {
        void releaseResource();
    }
}