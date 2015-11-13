package com.organizationiworkfor.ribbit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.organizationiworkfor.ribbit.R;
import com.organizationiworkfor.ribbit.Utils.MD5Util;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vivien on 10/22/2015.
 */
public class FriendsAdapter extends ArrayAdapter<ParseUser> {
    private Context mContext;
    private List<ParseUser> mUsers;

    public FriendsAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.user_grid_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_grid_item, null);
            holder = new ViewHolder();
            holder.userAvatar = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.checkedImageView = (ImageView) convertView.findViewById(R.id.userImageChecked);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        holder.nameLabel.setText(user.getUsername());
        String email = user.getEmail().toLowerCase();
        if (!email.equals("")) {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl ="http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Picasso.with(mContext).load(gravatarUrl).placeholder(R.drawable.avatar_empty).into(holder.userAvatar);
        }

        GridView gridView = (GridView) parent;
        //parent is the gridView this adapter is being used in
        if(gridView.isItemChecked(position)) {
            holder.checkedImageView.setVisibility(View.VISIBLE);
        } else {
            holder.checkedImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView userAvatar;
        ImageView checkedImageView;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
