package com.hendiware.hendienger.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fourhcode.forhutils.FUtilsValidation;
import com.hendiware.hendienger.MainActivity;
import com.hendiware.hendienger.R;
import com.hendiware.hendienger.models.ChatRoom;
import com.hendiware.hendienger.models.MainResponse;
import com.hendiware.hendienger.webservices.WebService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddChatRoomFragment extends DialogFragment {

    public final String TAG = "ADDCHATROOMFRAGMENT";
    // declare views using butter knife
    @BindView(R.id.et_room_name)
    EditText etRoomName;
    @BindView(R.id.et_room_desc)
    EditText etRoomDesc;
    @BindView(R.id.btn_add_room)
    Button btnAddRoom;
    @BindView(R.id.lnlt_body)
    LinearLayout lnltBody;
    @BindView(R.id.rllt_loading)
    RelativeLayout rlltLoading;

    public AddChatRoomFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_chat_room, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_add_room)
    public void onClick() {
        // using futils library to check if room name or room desc empty and show error message
        if (!FUtilsValidation.isEmpty(etRoomName, getString(R.string.please_enter_room_name))
                && !FUtilsValidation.isEmpty(etRoomDesc, getString(R.string.please_enter_room_desc))) {

            ChatRoom chatRoom = new ChatRoom();
            chatRoom.room_name = etRoomName.getText().toString();
            chatRoom.room_desc = etRoomDesc.getText().toString();
            // call addChatRoom method
            addChatRoom(chatRoom);
        }
    }


    // method take chat room as parameter and make call using retrofit to add this chatroom
    private void addChatRoom(ChatRoom chatRoom) {
        // show progress laoding
        setLoadingMode();
        WebService.getInstance().getApi().addChatRoom(chatRoom).enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if (response.body().status == 1) {
                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                    // get the prarent activity and cast it to main activity because we want to call reload chat rooms method
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.reloadChatRooms();
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), response.body().message, Toast.LENGTH_SHORT).show();
                    dismiss();

                }
                // dismiss progressloading
                setLoadingMode();

            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                Log.e(TAG, "Error :" + t.getLocalizedMessage());
                dismiss();
                Toast.makeText(getActivity(), "Error :" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setLoadingMode() {
        lnltBody.setVisibility(View.GONE);
        rlltLoading.setVisibility(View.VISIBLE);
    }

    private void setNormalMode() {
        lnltBody.setVisibility(View.VISIBLE);
        rlltLoading.setVisibility(View.GONE);
    }
}
