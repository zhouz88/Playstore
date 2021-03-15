package zhengzhou.individual.interview.service;

public interface BaseServiceContract {
    void initService();

    void onTaskMoved();

    void onDestroy();

    interface ReceiverPresenter {
        void releaseResource();
    }
}