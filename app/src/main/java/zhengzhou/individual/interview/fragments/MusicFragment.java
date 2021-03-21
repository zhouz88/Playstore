package zhengzhou.individual.interview.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import zhengzhou.individual.interview.R;
import zhengzhou.individual.interview.util.SongImageResult;

public final class MusicFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.music_fragment, container, false);
        recyclerView = view.findViewById(R.id.music_rec_view);
        return view;
    }

    private List<SongImageResult.Al> data =  new ArrayList<>();
    public MusicAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == data.size()) {
                    return 2;
                }
                return 1;
            }
        });
        adapter = MusicAdapter.builder().context(this.getContext()).data(this.data).build();
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Subscribe   //这里没有指定线程模型，使用默认值
    public void onStringEvent(EvbusEvent event) {
        ((TextView) event.subView)
                .getCompoundDrawables()[0].setTint(getContext()
                .getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
