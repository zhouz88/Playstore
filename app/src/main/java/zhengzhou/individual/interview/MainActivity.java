package zhengzhou.individual.interview;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;

import java.util.Iterator;

import zhengzhou.individual.interview.fragments.AppInfoFragment;
import zhengzhou.individual.interview.fragments.LikedMusicFragment;
import zhengzhou.individual.interview.fragments.MusicFragment;
import zhengzhou.individual.interview.fragments.NewsFragment;
import zhengzhou.individual.interview.util.MyDialogFragment;
import zhengzhou.individual.interview.util.SongImageResult;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private TextView musicButton;
    private TextView newsButton;
    private Fragment cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);

        newsButton = findViewById(R.id.toolbar_news);
        musicButton = findViewById(R.id.toolbar_music);

        newsButton.setOnClickListener(this);
        musicButton.setOnClickListener(this);

        View headerView = navigationView.getHeaderView(0);
        navigationView.setItemTextColor(
                (ColorStateList) getResources().getColorStateList(R.color.black));

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
                        DialogFragment dialogFragment;
                        if (cur instanceof MusicFragment) {
                           dialogFragment = new MyDialogFragment(((MusicFragment) cur).adapter);
                        } else if (cur instanceof LikedMusicFragment) {
                           dialogFragment = new MyDialogFragment(((LikedMusicFragment) cur).adapter);
                        } else {
                           dialogFragment = new MyDialogFragment();
                        }
                        dialogFragment.showNow(getSupportFragmentManager(), "sdf");
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.music_likes:
                        fragment = new LikedMusicFragment();
                        break;
                    case R.id.nav_slideshow:
                        fragment = new AppInfoFragment();
                        break;
                }
                if (fragment != null) {
                    cur = fragment;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_holder, fragment).commit();
                    drawerLayout.closeDrawers();

                }
                return true;
            }
        });
        cur = new NewsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_holder, cur).commit();
    }

    @Override
    public void onClick(View v) {
        if (v == musicButton) {
            musicButton.setTextColor(getResources().getColor(R.color.white));
            newsButton.setTextColor(getResources().getColor(R.color.black));
            cur =  new MusicFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                   cur).commit();
        } else {
            newsButton.setTextColor(getResources().getColor(R.color.white));
            musicButton.setTextColor(getResources().getColor(R.color.black));
            cur = new NewsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                    cur).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 404 && resultCode == RESULT_OK) {
            for (SongImageResult.Al datum : ((MusicFragment) cur).adapter.getData()) {
                if (datum.id.equals(Long.parseLong(data.getStringExtra("mp3id")))) {
                    datum.like =  data.getBooleanExtra("mp3like", false);
                }
            }
            ((MusicFragment)cur).adapter.notifyDataSetChanged();
        } else if (requestCode == 402 && resultCode == RESULT_OK) {
            Iterator itr = ((LikedMusicFragment) cur).adapter.getData().iterator();
            while (itr.hasNext()) {
                SongImageResult.Al datum = (SongImageResult.Al)itr.next();
                if (datum.id.equals(Long.parseLong(data.getStringExtra("mp3id"))) && !data.getBooleanExtra("mp3like", false) ) {
                    itr.remove();
                }
            }
            ((LikedMusicFragment)cur).adapter.notifyDataSetChanged();
        }
    }
}