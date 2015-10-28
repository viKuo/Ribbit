package com.organizationiworkfor.ribbit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.organizationiworkfor.ribbit.ParseConstants;
import com.organizationiworkfor.ribbit.R;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

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
            holder.iconImage.setImageResource(R.drawable.ic_image_black_24dp);
        } else {
            holder.iconImage.setImageResource(R.drawable.ic_videocam_black_24dp);
        }
        holder.nameLabel.setText(message.getParseObject(ParseConstants.KEY_SENDER_NAME).getString(ParseConstants.KEY_USERNAME));

        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
        formatter.setTimeZone(TimeZone.getDefault());
        String dateString = formatter.format(message.getCreatedAt());
        holder.timeLabel.setText(dateString);

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
