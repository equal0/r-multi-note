package com.example.eunyoungha.r_multi_note.activities;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eunyoungha.r_multi_note.APIResponse;
import com.example.eunyoungha.r_multi_note.utils.DatabaseHelper;
import com.example.eunyoungha.r_multi_note.models.MemoList;
import com.example.eunyoungha.r_multi_note.models.MemoListAdapter;
import com.example.eunyoungha.r_multi_note.interfaces.NoteAPICallback;
import com.example.eunyoungha.r_multi_note.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAPICallback {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private Toolbar toolbar;
    private RelativeLayout mHamburgerButton;
    private RelativeLayout mAppTitle;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mUserInfoDrawer;
    private LinearLayout mCreateMemoDrawer;
    private LinearLayout mSearchMemo;
    private ListView mShowingResult;
    private MemoListAdapter memoListAdapter;
    private RelativeLayout mSearchLayout;
    private Button mSearchButton;
    private TextView mSearchText;

    private boolean listFlag = false;

    private DatabaseHelper dbHelper;

    private List<MemoList> mDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DatabaseHelper
        dbHelper = new DatabaseHelper(getApplicationContext(), "memo.db", null, 1);

        //Drawer setup
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar,R.string.app_name,R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //Toolbar setup
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Toolbar title setup
        mAppTitle = (RelativeLayout) findViewById(R.id.layout_toolbar_app_name);
        mAppTitle.setVisibility(View.VISIBLE);

        //Menu button is visible and pop up drawer
        mHamburgerButton = (RelativeLayout) findViewById(R.id.layout_toolbar_hamburger_button);
        mHamburgerButton.setVisibility(View.VISIBLE);
        mHamburgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //Get view references
        mShowingResult = (ListView) findViewById(R.id.showing_memo_list);
        mSearchLayout = (RelativeLayout) findViewById(R.id.search_layout);
        mUserInfoDrawer = (LinearLayout) findViewById(R.id.drawer_user_info);
        mCreateMemoDrawer = (LinearLayout) findViewById(R.id.drawer_create_new_memo);
        mSearchMemo = (LinearLayout) findViewById(R.id.drawer_search_memo);
        mSearchButton = (Button) findViewById(R.id.search_memo_button);
        mSearchText = (EditText) findViewById(R.id.search_memo_bar);

        //memo for database
        //setAllMemo();

        //When user clicked list item in database
        mShowingResult.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemoList memo = (MemoList) memoListAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(),CreateMemoActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("memo",memo);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

        //Drawer menu
        mUserInfoDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"user info",Toast.LENGTH_SHORT).show();
            }
        });
        mCreateMemoDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newMemoIntent = new Intent(getApplicationContext(), CreateMemoActivity.class);
                startActivity(newMemoIntent);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        mSearchMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                mSearchLayout.setVisibility(View.VISIBLE);
            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listFlag = true;
                setAllMemo();
        }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //test for interacting with app and api
        String [] parameters = {"getMemo".toString(),"http://localhost:8080/engine/api/RNote/text"};
        new NoteAPI(getApplicationContext(),this).execute(parameters);
    }

    protected void setAllMemo(){
        memoListAdapter = new MemoListAdapter();
        ArrayList<MemoList> memoList;

        if(!listFlag){
            memoList = dbHelper.getResult();
        }else{
            String searchText = mSearchText.getText().toString();
            memoList = dbHelper.getSearchResult(searchText);
        }

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
        mShowingResult.setAdapter(memoListAdapter);
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
                mShowingResult.setAdapter(memoListAdapter);
            }
        }
    }

}
