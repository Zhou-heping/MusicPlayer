package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.musicplayer.data.Music;
import com.example.musicplayer.util.GetMusicInfo;
import com.google.android.material.navigation.NavigationView;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<Music> currentMusicList = new ArrayList<>();     // 当前播放音乐列表
    public static List<Music> localMusic = new ArrayList<>();   // 本地的音乐
    public static List<Music> loveMusic = new ArrayList<>();    // 喜欢的音乐
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;      // 适配器
    private MenuItem menuItem;      // 顶部菜单
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: "+"excuted");
        // 权限申请
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else {
            GetMusicInfo.getLocalMusic(MainActivity.this);
        }

        // 创建数据库
        LitePal.getDatabase();
        initLayout();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.iconsmenu);
        }
        navigationView.setCheckedItem(R.id.nav_call);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                // do something
                return true;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (menuItem != null){
                    menuItem.setChecked(false);
                }else {
                    toolbar.getMenu().getItem(0).setChecked(false);
                }
                menuItem = toolbar.getMenu().getItem(position);
                menuItem.setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        List<Fragment> list = new ArrayList<>();
        list.add(new MyMusicFragment());
        list.add(new OnlineMusicFragment());
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.setList(list);
        viewPager.setAdapter(viewPagerAdapter);
        // 下滑
        swipeRefreshLayout.setRefreshing(false);
    }
    private void initLayout(){
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.view_pager);
        navigationView = findViewById(R.id.nav_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    GetMusicInfo.getLocalMusic(MainActivity.this);
                }else {
                    Toast.makeText(MainActivity.this, "拒绝权限将无法使用程序",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuItem = item;
        switch (item.getItemId()){
            case R.id.my_music:
                viewPager.setCurrentItem(0);
                break;
            case R.id.online_music:
                viewPager.setCurrentItem(1);
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.help:
                break;
            default:
        }
        return true;
    }
}