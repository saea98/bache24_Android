package com.cmi.bache24.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.Faq;

import java.util.List;

/**
 * Created by omar on 12/9/15.
 */
public class FaqExpandableListAdapter extends BaseExpandableListAdapter {

    private List<Faq> groups;
    private Context mContext;

    public FaqExpandableListAdapter(Context mContext, List<Faq> groups) {
        this.groups = groups;
        this.mContext = mContext;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return groups.get(i).getId();
    }

    @Override
    public Object getChild(int i, int i1) {
        return groups.get(i).getId();
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View view, ViewGroup viewGroup) {
        View groupView;

        LayoutInflater inf = (LayoutInflater) mContext
                .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        groupView = inf.inflate(R.layout.faq_parent_row, null);

        TextView textView = (TextView) groupView.findViewById(R.id.textview_faq_title);
        textView.setText(groups.get(i).getTitle());

        ImageView arrow = (ImageView) groupView.findViewById(R.id.image_arrow_jcon);

        if (isExpanded) {
            arrow.setImageResource(R.drawable.faq_arrow_up);
            textView.setTextColor(mContext.getResources().getColor(R.color.primary));
        } else {
            arrow.setImageResource(R.drawable.faq_arrow_down);
            textView.setTextColor(mContext.getResources().getColor(R.color.gray_a));
        }

        return groupView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.faq_children_row, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.textview_faq_description);
        tv.setText(groups.get(groupPosition).getDescription());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
