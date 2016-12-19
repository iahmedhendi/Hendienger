package com.hendiware.hendienger.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hendiware.hendienger.R;
import com.hendiware.hendienger.adapters.MessagingAdapter;
import com.hendiware.hendienger.models.MainResponse;
import com.hendiware.hendienger.models.Message;
import com.hendiware.hendienger.utils.Session;
import com.hendiware.hendienger.webservices.WebService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.img_attachment)
    ImageView imgAttachment;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.img_send)
    ImageView imgSend;
    @BindView(R.id.rllt_text_box)
    RelativeLayout rlltTextBox;
    @BindView(R.id.content_chat)
    LinearLayout contentChat;

    private int roomId = 0;
    private int userId = 0;
    private String roomName;
    private String username;
    private MessagingAdapter adapter;
    private List<Message> messages;
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message message = intent.getParcelableExtra("msg");
            if (message != null) {
                messages.add(message);
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerChat.scrollToPosition(messages.size() - 1);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // get room ID and room name
        roomId = getIntent().getExtras().getInt("room_id");
        roomName = getIntent().getExtras().getString("room_name");
        // set room name as toolbar title
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(roomName);
        // display back button in toolbar
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // get user id and username from session
        userId = Session.getInstance().getUser().id;
        username = Session.getInstance().getUser().username;

        // set layoutmanager for chat recycler
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(layoutManager);

        // get messages
        getMessages(roomId);

        FirebaseMessaging.getInstance().subscribeToTopic("room" + roomId);
        Log.e("room topic is ", "room" + roomId);


    }

    /**
     * get messages method get messages from server for the room id
     *
     * @param roomId the room id we want its message
     */

    private void getMessages(int roomId) {
        WebService.getInstance().getApi().getMessages(roomId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                messages = response.body();
                adapter = new MessagingAdapter(messages, ChatActivity.this);
                recyclerChat.setAdapter(adapter);
                recyclerChat.scrollToPosition(messages.size() - 1);


            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error:" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method to send message to server using retrofit
     *
     * @param message is the new message you want to send
     */

    private void addMessage(Message message) {
        WebService.getInstance().getApi().addMessage(message).enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if (response.body().status == 0)
                    Toast.makeText(ChatActivity.this, "Error while trying to send message", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }

    /**
     * on click method using  butter knife library for img attachment and img send
     *
     * @param view
     */
    @OnClick({R.id.img_attachment, R.id.img_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_attachment:
                break;
            case R.id.img_send:
                // if msg editText is empty return don't do any thing
                if (etMessage.getText().toString().isEmpty()) return;

                // get msg from edit text
                String msg = etMessage.getText().toString();

                // create new message
                Message message = new Message();
                // set type to 1 (text message)
                message.setType("1");
                // set room id int
                message.setRoomId(String.valueOf(roomId));
                // set user id int
                message.setUserId(String.valueOf(userId));
                // set user name
                message.setUsername(username);
                // set message content
                message.setContent(msg);
                // add message to messages list
                messages.add(message);
                // notify adapter that there is new message in this position
                adapter.notifyItemInserted(messages.size() - 1);
                // scroll to last item in recycler
                recyclerChat.scrollToPosition(messages.size() - 1);
                // set message box empty
                etMessage.setText("");
                // sent message to server
                addMessage(message);
                break;
        }
    }

    /**
     * if user press back button finish the current activity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver to handle "" UPDATE CHAT ACTIVITY "" Filter
        registerReceiver(messageReceiver, new IntentFilter("UpdateChateActivity"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister receiver
        unregisterReceiver(messageReceiver);
    }


}
