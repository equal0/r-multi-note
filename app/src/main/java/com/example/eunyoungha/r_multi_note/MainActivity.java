package com.example.eunyoungha.r_multi_note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAPICallback {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private Toolbar toolbar;
    private RelativeLayout mHamburgerButton;
    private RelativeLayout mAppTitle;
    private DrawerLayout mDrawerLayout;
    private ScrollView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mUserInfoDrawer;
    private LinearLayout mCreateMemoDrawer;
    private ListView showingResult;
    private MemoListAdapter memoListAdapter;

    private DatabaseHelper dbHelper;

    private final int REQUEST_CODE_NEW_MEMO = 101;

    private List<MemoList> mDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DatabaseHelper 클래스 생성
        dbHelper = new DatabaseHelper(getApplicationContext(), "memo.db", null, 1);

        //어댑터 생성 및 데이터베이스에 있는 내용 리스트뷰로 뿌리기
        memoListAdapter = new MemoListAdapter();
        showingResult = (ListView) findViewById(R.id.showing_memo_list);

        //test for interacting with app and api
        //new NoteAPI(getApplicationContext(),this).execute();

        //memo for database
        final ArrayList<MemoList> memoList = dbHelper.getResult();
        for(int i = 0 ; i < memoList.size() ; i++){
            int id = memoList.get(i).getId();
            String date = memoList.get(i).getDate();
            String context_text = memoList.get(i).getContent_text();
            int photoId = memoList.get(i).getId_photo();
            int videoId = memoList.get(i).getId_video();
            int voiceId = memoList.get(i).getId_voice();
            int mapId = memoList.get(i).getId_map();
            memoListAdapter.addItem(new MemoList(id, date,context_text,photoId,videoId,voiceId,mapId));
        }
       showingResult.setAdapter(memoListAdapter);

        //리스트 뷰의 메모항목을 눌렀을 때 해당되는 동작
        showingResult.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemoList memo = (MemoList) memoListAdapter.getItem(position);

                //id값 잘 넘어옴

                Intent intent = new Intent(getApplicationContext(),CreateMemoActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("memo",memo);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

        //drawer메뉴를 보여주기 위한 세팅
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar,R.string.app_name,R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mDrawerList = (ScrollView) findViewById(R.id.left_drawer);

        //툴바 세팅
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //툴바의 타이틀 세팅
        mAppTitle = (RelativeLayout) findViewById(R.id.layout_toolbar_app_name);
        mAppTitle.setVisibility(View.VISIBLE);

        //툴바의 아이콘 세팅 및 온클릭리스너
        mHamburgerButton = (RelativeLayout) findViewById(R.id.layout_toolbar_hamburger_button);
        mHamburgerButton.setVisibility(View.VISIBLE);
        mHamburgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //Drawer 메뉴의 user info 항목을 눌렀을 경우
        mUserInfoDrawer = (LinearLayout) findViewById(R.id.drawer_user_info);
        mUserInfoDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"user info",Toast.LENGTH_SHORT).show();
            }
        });

        //Drawer 메뉴의 New Memo 항목을 눌렀을 경우
        mCreateMemoDrawer = (LinearLayout) findViewById(R.id.drawer_create_new_memo);
        mCreateMemoDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newMemoIntent = new Intent(getApplicationContext(), CreateMemoActivity.class);
                startActivity(newMemoIntent);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void processFinish(APIResponse response) {
        List<MemoList> list = response.getMemoList();
        if(list == null){
            Toast.makeText(this,"memo is not exist",Toast.LENGTH_SHORT).show();
        }else{
            if(mDataSet == null || memoListAdapter == null){
                mDataSet = list;
                memoListAdapter = new MemoListAdapter();
            for(int i = 0 ; i < mDataSet.size() ; i++){
            int id = mDataSet.get(i).getId();
            String date = mDataSet.get(i).getDate();
            String context_text = mDataSet.get(i).getContent_text();
            int photoId = mDataSet.get(i).getId_photo();
            int videoId = mDataSet.get(i).getId_video();
            int voiceId = mDataSet.get(i).getId_voice();
            int mapId = mDataSet.get(i).getId_map();
            memoListAdapter.addItem(new MemoList(id, date,context_text,photoId,videoId,voiceId,mapId));
        }
                showingResult.setAdapter(memoListAdapter);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
