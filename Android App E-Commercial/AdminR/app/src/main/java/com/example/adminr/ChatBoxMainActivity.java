package com.example.adminr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.adminr.Adapter.Chat_Adapter;
import com.example.adminr.model.ChatMessage;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatBoxMainActivity extends AppCompatActivity {
    List<ChatMessage> chatMessageList;
    List<String> mkey;
    private static int SIGN_IN_REQUEST_CODE = 1;
    RelativeLayout activity_chatboxmain;
    FloatingActionButton btn_send_message;
    FirebaseDatabase database;
    DatabaseReference myRef;
    EditText input;
    ListView listOfMessage;
    private static final String TAG = "ChatBoxMainActivity";
    Chat_Adapter customListAdapter;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatboxmainactivity);
        //Slidr.attach(this);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Message with admin");
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        activity_chatboxmain = (RelativeLayout) findViewById(R.id.activity_chatboxmainactivity);
        btn_send_message = (FloatingActionButton) findViewById(R.id.fab);
        input = (EditText) findViewById(R.id.input);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            displayChatMessage();
        }
        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat();
            }
        });
        findViewById(R.id.activity_chatboxmainactivity).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    public void Chat() {
        Date date = new Date();
        String message = input.getText().toString().trim();
        if (!message.equals("")) {
            FirebaseDatabase.getInstance().getReference()
                    .child("chat_message")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("message")
                    .push()
                    .setValue(
                    new ChatMessage(
                            message,
                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            System.currentTimeMillis(),
                            "admin",
                            FirebaseAuth.getInstance().getCurrentUser().getUid()));
            input.setText("");
            customListAdapter.notifyDataSetChanged();
            scrollMyListViewToBottom();

            FirebaseDatabase.getInstance().getReference()
                    .child("chat_message")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("emailCustomer").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(activity_chatboxmain, "You have been signed out", Snackbar.LENGTH_SHORT).show();
                        finish();
                    }
                });
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_chatbox, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_chatboxmain, "Succesfully sign in", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            } else {
                Snackbar.make(activity_chatboxmain, "TRY AGAIN !!!!", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void displayChatMessage() {

        listOfMessage = (ListView) findViewById(R.id.list_of_message);
        chatMessageList = new ArrayList<ChatMessage>();
        customListAdapter = new Chat_Adapter(this, chatMessageList, FirebaseAuth.getInstance().getCurrentUser().getEmail());
        mkey = new ArrayList<String>();
        listOfMessage.setAdapter(customListAdapter);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.child("chat_message").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage itemProduct = dataSnapshot.getValue(ChatMessage.class);
                chatMessageList.add(itemProduct);
                Log.d(TAG, "onChildAdded: " + itemProduct.getMessageUser());
                customListAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void scrollMyListViewToBottom() {
        listOfMessage.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listOfMessage.setSelection(customListAdapter.getCount() - 1);
            }
        });
    }
}

