package zhengzhou.individual.interview;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;

import zhengzhou.individual.interview.fragments.MusicFragment;
import zhengzhou.individual.interview.fragments.NewsFragment;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private TextView musicButton;
    private TextView newsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);

        newsButton = toolbar.findViewById(R.id.toolbar_news);
        musicButton = toolbar.findViewById(R.id.toolbar_music);

        newsButton.setOnClickListener(this);
        musicButton.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder, new NewsFragment()).commit();
    }

    @Override
    public void onClick(View v) {
        if (v == musicButton) {
            musicButton.setTextColor(getResources().getColor(R.color.white));
            newsButton.setTextColor(getResources().getColor(R.color.black));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_holder, new MusicFragment())
                    .commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                    new MusicFragment()).commit();
        } else {
            newsButton.setTextColor(getResources().getColor(R.color.white));
            musicButton.setTextColor(getResources().getColor(R.color.black));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                    new NewsFragment()).commit();
        }
    }

    private void setupNavHeader() {
        View headerView = navigationView.getHeaderView(0);

        ((TextView) headerView.findViewById(R.id.text_view)).setText(
                "zheng zhou");

        ((SimpleDraweeView) headerView.findViewById(R.id.image_view))
                .setImageURI(Uri.parse("https://p1.music.126.net/4DRm5E8ahUJu5r1c4cNNbQ==/2450811418334469.jpg"));

    }
}