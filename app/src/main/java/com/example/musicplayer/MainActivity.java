package com.example.musicplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//从存储卡内部读取所有音乐列表，并显示相关音乐信息
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //音乐信息
    public static List<MusicInfo> musicInfos = new ArrayList<MusicInfo>();
    //音乐列表，当前页面listitem项数据
    private List<Map<String, String>> List_map = new ArrayList<>();
    private ListView MusicListView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    SimpleAdapter simpleAdapter;
    //权限申请码requestCode
    private final static int STORGE_REQUEST = 1;
    Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MusicListView = (ListView) findViewById(R.id.musicListView);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        registerForContextMenu(MusicListView);

        //首先检查自身是否已经拥有相关权限，拥有则不再重复申请
        int check = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (check != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORGE_REQUEST);
        } else {
            //已有权限的情况下可以直接初始化程序
            LitePal.getDatabase();  // 创建数据库
            GetMusicInfo.getLocalMusic(MainActivity.this);
            init();
        }
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //这里获取数据的逻辑
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        if (musicInfos.isEmpty()) {
            return;
        }
        for (int i = 0; i < musicInfos.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("image", musicInfos.get(i).getAlbum_id());
            map.put("name", musicInfos.get(i).getMusicName());
            map.put("artist", musicInfos.get(i).getArtist());
            List_map.add(map);
        }
        //SimpleAdapter实例化
        simpleAdapter = new
                SimpleAdapter(this, List_map, R.layout.music_item_main,
                new String[]{
                        "image", "name", "artist"
                },
                new int[]{
                        R.id.image_MusicImage,
                        R.id.textView_MusicName, R.id.textView_MusicArtist
                });
        //为ListView对象指定adapter
        MusicListView.setAdapter(simpleAdapter);
        //绑定item点击事件
        MusicListView.setOnItemClickListener(this);
    }

    //item点击实现
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //将点击位置传递给播放界面，在播放界面获取相应的音乐信息再播放。
        //绑定需要传递的参数,并跳转到音乐播放页面
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        intent.setClass(MainActivity.this, MusicPlay.class);
        startActivity(intent);
    }

    //删除对话框
    private void DeleteDialog(final int position) {
        new AlertDialog.Builder(this).setTitle("删除单词").setMessage("是否真的删除歌曲?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除数据
                        LitePal.delete(MusicInfo.class, musicInfos.get(position).get_id());
                        musicInfos = LitePal.findAll(MusicInfo.class);//重新加载数据
                        recreate();
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }

    //创建上下文菜单，来源contextmenu_wordslistview，并添加菜单项删除和更新
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu_musiclistview, menu);
    }

    //对上下文菜单项设置点击动作1
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = null;
        View itemView = null;
        switch (item.getItemId()) {
            case R.id.action_delete:
                //删除单词
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int position = info.position;
                Log.e("POSITION IS : ", position + "");
                itemView = info.targetView;//设置该view在哪个蒙层上显示
                DeleteDialog(position);
                break;
            default:
        }
        return true;
    }

    //选项菜单的创建和其选项的动作
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_help:
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //申请权限处理结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORGE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //从本地获取音乐信息，加入数据库。
                    LitePal.getDatabase();
                    GetMusicInfo.getLocalMusic(MainActivity.this);
                    //完成程序的初始化
                    init();
                } else {
                    Toast.makeText(getApplication(), "程序没有获得相关权限，请处理", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
