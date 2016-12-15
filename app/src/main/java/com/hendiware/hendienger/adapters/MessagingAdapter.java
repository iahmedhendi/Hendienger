package com.hendiware.hendienger.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hendiware.hendienger.R;
import com.hendiware.hendienger.models.Message;
import com.hendiware.hendienger.models.MessageType;
import com.hendiware.hendienger.utils.Session;
import com.hendiware.hendienger.webservices.Urls;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hendiware on 2016/12 .
 */

public class MessagingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;
    private Context context;

    /**
     * Constructor for Adapter
     *
     * @param messages list of messages will showed
     * @param context  context
     */
    public MessagingAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**
         * check the type of view and return holder
         */
        if (viewType == MessageType.SENT_TEXT) {
            return new SentTextHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sent_message_text, parent, false));
        } else if (viewType == MessageType.SENT_IMAGE) {
            return new SentImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sent_message_img, parent, false));
        } else if (viewType == MessageType.RECEIVED_TEXT) {
            return new ReceivedTextHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_received_message_text, parent, false));
        } else if (viewType == MessageType.RECEIVED_IMAGE) {
            return new ReceivedImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_received_message_img, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, int position) {

        int type = getItemViewType(position);
        Message message = messages.get(position);
        /**
         * check message type and init holder to user it and set data in the right place for every view
         */
        if (type == MessageType.SENT_TEXT) {
            SentTextHolder holder = (SentTextHolder) mHolder;
            holder.tvTime.setText(message.getTime());
            holder.tvMessageContent.setText(message.getContent());

        } else if (type == MessageType.SENT_IMAGE) {
            SentImageHolder holder = (SentImageHolder) mHolder;
            holder.tvTime.setText(message.getTime());
            Glide.with(context).load(Urls.IMAGES_URL + message.getContent()).into(holder.imgMsg);


        } else if (type == MessageType.RECEIVED_TEXT) {
            ReceivedTextHolder holder = (ReceivedTextHolder) mHolder;
            holder.tvTime.setText(message.getTime());
            holder.tvUsername.setText(message.getUsername());
            holder.tvMessageContent.setText(message.getContent());


        } else if (type == MessageType.RECEIVED_IMAGE) {
            ReceivedImageHolder holder = (ReceivedImageHolder) mHolder;
            holder.tvTime.setText(message.getTime());
            holder.tvUsername.setText(message.getUsername());
            Glide.with(context).load(Urls.IMAGES_URL + message.getContent()).into(holder.imgMsg);

        }

    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        /**
         * check the user id to detect if message sent or received
         * then check if message is text or img
         */

        int userID = Session.getInstance().getUser().id;
        Message message = messages.get(position);

        if (userID == Integer.parseInt(message.getUserId())) {

            if (message.getType().equals("1")) {
                return MessageType.SENT_TEXT;
            } else if (message.getType().equals("2")) {
                return MessageType.SENT_IMAGE;
            }

        } else {

            if (message.getType().equals("1")) {
                return MessageType.RECEIVED_TEXT;
            } else if (message.getType().equals("2")) {
                return MessageType.RECEIVED_IMAGE;
            }

        }
        return super.getItemViewType(position);
    }

    // sent message holders
    class SentMessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_time)
        TextView tvTime;

        public SentMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // sent message with type text
    class SentTextHolder extends SentMessageHolder {
        @BindView(R.id.tv_message_content)
        TextView tvMessageContent;

        public SentTextHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    // sent message with type image
    class SentImageHolder extends SentMessageHolder {
        @BindView(R.id.img_msg)
        ImageView imgMsg;

        public SentImageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // received message holders
    class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_username)
        TextView tvUsername;
        @BindView(R.id.tv_time)
        TextView tvTime;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // received message with type text
    class ReceivedTextHolder extends ReceivedMessageHolder {
        @BindView(R.id.tv_message_content)
        TextView tvMessageContent;

        public ReceivedTextHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // received message with type image

    class ReceivedImageHolder extends ReceivedMessageHolder {
        @BindView(R.id.img_msg)
        ImageView imgMsg;

        public ReceivedImageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
