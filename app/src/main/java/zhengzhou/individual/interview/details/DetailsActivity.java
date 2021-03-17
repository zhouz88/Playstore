package zhengzhou.individual.interview.details;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import zhengzhou.individual.interview.R;

public class DetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_launcher_foreground);
        toolbar.setTitle("");
        ((TextView)toolbar.findViewById(R.id.toolbar_news)).setText("Music");
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(DetailsPageAdapter
                .builder()
                .context(this)
                .imageUrl(intent.getStringExtra("imageurl"))
                .textTitle(intent.getStringExtra("textTitle"))
                .text(intent.getStringExtra("text"))
                .build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

