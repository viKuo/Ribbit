package com.organizationiworkfor.ribbit.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.organizationiworkfor.ribbit.R;
import com.organizationiworkfor.ribbit.Utils.ParseConstants;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Vivien on 10/22/2015.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {
    private Context mContext;
    private List<ParseObject> mMessage;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_layout, messages);
        mContext = context;
        mMessage = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_layout, null);
            holder = new ViewHolder();
            holder.iconImage = (ImageView) convertView.findViewById(R.id.messageIconImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.messageUsernameImageView);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.dateTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        ParseObject message = mMessage.get(position);
        if (mMessage.get(position).getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.IMAGE)){
            holder.iconImage.setImageResource(R.drawable.ic_picture);
        } else {
            holder.iconImage.setImageResource(R.drawable.ic_video);
        }
        holder.nameLabel.setText(message.getParseUser(ParseConstants.KEY_SENDER_NAME).getUsername());

        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();
        holder.timeLabel.setText(convertedDate);

        return convertView;
    }

    private class ViewHolder {
        ImageView iconImage;
        TextView nameLabel;
        TextView timeLabel;
    }

    public void refill(List<ParseObject> messages) {
        mMessage.clear();
        mMessage.addAll(messages);
        notifyDataSetChanged();
    }
}
