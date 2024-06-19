package com.example.mediaplayer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mediaplayer.R;

public class FolderFragmentAdapter extends BaseAdapter {
    private String[] titles = {"1001次日落", "Chance", "Lazy", "陪着你", "Melt", "爱爱爱"};
    private String[] autor = {"饭卡", "Sbbac07", "Surfaces", "DoubleBam", "方大同",
            "280元"};
    private int[] icons = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return titles[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=new ViewHolder();
        if (view==null){
            view=View.inflate(viewGroup.getContext(),R.layout.song_item,null);
            viewHolder.iv=view.findViewById(R.id.iv_1);
            viewHolder.title=view.findViewById(R.id.item_name);
            viewHolder.autor=view.findViewById(R.id.item_singer);
            view.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.autor.setText(autor[i]);
        viewHolder.title.setText(titles[i]);
        viewHolder.iv.setBackgroundResource(icons[i]);
        return view;
    }
    class ViewHolder{
        TextView title,autor;
        ImageView iv;
    }
}