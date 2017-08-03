package com.hendiware.hendienger;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourhcode.forhutils.FUtilsProgress;
import com.hendiware.hendienger.adapters.ChatRoomsAdapter;
import com.hendiware.hendienger.fragments.AddChatRoomFragment;
import com.hendiware.hendienger.models.ChatRoom;
import com.hendiware.hendienger.models.MainResponse;
import com.hendiware.hendienger.models.User;
import com.hendiware.hendienger.utils.Session;
import com.hendiware.hendienger.webservices.WebService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @BindView(R.id.recycler_chat_rooms)
    RecyclerView recyclerChatRooms;
    @BindView(R.id.content_main)
    RelativeLayout contentMain;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_desc)
    TextView toolbarDesc;
    @BindView(R.id.add_char_room_fab)
    FloatingActionButton addCharRoomFab;

    private FUtilsProgress progress;
    private Call<List<ChatRoom>> getChatRoomsCall;
    private List<ChatRoom> chatRooms;
    private ChatRoomsAdapter adapter;
    // item touch helper to use swipe feature in recycler view to delete chat room
    ItemTouchHelper.SimpleCallback swipChatRoomCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // get adapter position
            final int position = viewHolder.getAdapterPosition();
            // get chat room id from chat rooms list depedning on position
            int chatRoomID = Integer.parseInt(chatRooms.get(position).id);
            // start Retrofit call to delete chat room
            WebService.getInstance().getApi().deleteChatRoom(chatRoomID).enqueue(new Callback<MainResponse>() {
                @Override
                public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                    if (response.body().status == 1) {
                        // toast message result
                        Toast.makeText(MainActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                        // delete message from local chat room list which showed on adapter  now
                        chatRooms.remove(position);
                        // notify adapter that chat room deleted so its delete it
                        adapter.notifyItemRemoved(position);

                    } else {
                        // toast message if status 0 it will be error
                        Toast.makeText(MainActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MainResponse> call, Throwable t) {
                    // toast message of fail
                    Toast.makeText(MainActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                }
            });
        }


        // to color the background of swiped item red color
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            // if swiping now
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // get item that swiped
                View itemView = viewHolder.itemView;
                // create new pain
                Paint p = new Paint();
                // if swiping to left dx will < 0 so we do what we want
                if (dX <= 0) {
                    // set color for paint red color
                    p.setColor(Color.parseColor("#ED1220"));
                    // draw rectangle depending on the item view ends
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), p);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        progress = FUtilsProgress.newProgress(this, contentMain);
        recyclerChatRooms.setLayoutManager(new LinearLayoutManager(this));
        recyclerChatRooms.setHasFixedSize(true);

        // get chat rooms and view them
        getChatRooms();

        // get current login user
        User user = Session.getInstance().getUser();
        if (user != null) {
            toolbarTitle.setText(getString(R.string.welcome) + user.username);
            toolbarDesc.setText(R.string.nice_to_meet_you);
            // check if user admin or not
            if (user.isAdmin) {

                toolbarDesc.setText(R.string.nice_to_meet_you_admin);
                addCharRoomFab.setVisibility(View.VISIBLE);
                // attach item touch helper with our recycler view
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipChatRoomCallBack);
                itemTouchHelper.attachToRecyclerView(recyclerChatRooms);

            } else {
                addCharRoomFab.setVisibility(View.GONE);
            }

        }

    }

    // get chat rooms using retrofit
    private void getChatRooms() {
        // show transparent progress (loading indicator )
        progress.showTransparentProgress();

        // retrofit call init
        getChatRoomsCall = WebService.getInstance().getApi().getAllChatRooms();

        // retrofit call start in background
        getChatRoomsCall.enqueue(new Callback<List<ChatRoom>>() {
            @Override
            public void onResponse(Call<List<ChatRoom>> call, Response<List<ChatRoom>> response) {
                // receive chat rooms list comming from server
                // init adapter by assign new object to it
                // set adapter for recycler
                // dismiss progress
                chatRooms = response.body();
                adapter = new ChatRoomsAdapter(chatRooms, MainActivity.this);
                recyclerChatRooms.setAdapter(adapter);
                progress.dismiss();

            }

            @Override
            public void onFailure(Call<List<ChatRoom>> call, Throwable t) {
                progress.dismiss();
                // log error message
                Log.e(TAG, "Error " + t.getLocalizedMessage());
                // toast error message
                Toast.makeText(MainActivity.this, "Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    // method to get Chat rooms we call it from Add room dialog to update rooms after add
    public void reloadChatRooms() {
        getChatRooms();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getChatRoomsCall.cancel();
    }

    @OnClick({R.id.toolbar_tv_logout, R.id.add_char_room_fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_tv_logout:
                Session.getInstance().logoutAndGoToLogin(this);
                break;
            case R.id.add_char_room_fab:
                AddChatRoomFragment addChatRoomFragment = new AddChatRoomFragment();
                addChatRoomFragment.show(getSupportFragmentManager(), addChatRoomFragment.TAG);
                break;
        }
    }


}
