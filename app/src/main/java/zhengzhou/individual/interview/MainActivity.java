package zhengzhou.individual.interview;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;

import zhengzhou.individual.interview.fragments.LikedMusicFragment;
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

        View headerView = navigationView.getHeaderView(0);
        navigationView.setItemTextColor(
                (ColorStateList)getResources().getColorStateList(R.color.black));

        ((TextView) headerView.findViewById(R.id.text_view)).setText(
                "zheng zhou");

        ((SimpleDraweeView) headerView.findViewById(R.id.image_view))
                .setImageURI(Uri.parse("https://p1.music.126.net/4DRm5E8ahUJu5r1c4cNNbQ==/2450811418334469.jpg"));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.liked_news:
                        // news fragment;
                        break;
                    case R.id.music_likes:
                        fragment = new LikedMusicFragment();
                        break;
                    case R.id.nav_slideshow:
                        break;
                }
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_holder, fragment).commit();
                    drawerLayout.closeDrawers();

                }
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder, new NewsFragment()).commit();
    }

    @Override
    public void onClick(View v) {
        if (v == musicButton) {
            musicButton.setTextColor(getResources().getColor(R.color.white));
            newsButton.setTextColor(getResources().getColor(R.color.black));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                    new MusicFragment()).commit();
        } else {
            newsButton.setTextColor(getResources().getColor(R.color.white));
            musicButton.setTextColor(getResources().getColor(R.color.black));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                    new NewsFragment()).commit();
        }
    }
}