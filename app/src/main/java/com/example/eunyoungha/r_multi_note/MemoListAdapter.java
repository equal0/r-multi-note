package com.example.eunyoungha.r_multi_note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by eunyoung.ha on 2017/10/20.
 */

public class MemoListAdapter extends BaseAdapter{

    private ArrayList<MemoList> memoList = new ArrayList<>();

    public MemoListAdapter() {
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public Object getItem(int position) {
        return memoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int index = position;
        final Context context = parent.getContext();

        //"memo_list_view" 레이아웃을 inflate 하여 convertview 참조 획득
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.memo_list_view, parent, false);
        }

        TextView memoTitle = (TextView) convertView.findViewById(R.id.memo_list_title);
        TextView memoDate = (TextView) convertView.findViewById(R.id.memo_list_date);

        MemoList memo = memoList.get(position);

        memoTitle.setText(memo.getContent_text());
        memoDate.setText(memo.getDate());

        return convertView;
    }

    public void addItem(MemoList memo){
        memoList.add(memo);
    }

}
