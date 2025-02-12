package com.example.test1706;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.test1706.Adapter.Chat_Adapter;
import com.example.test1706.model.ChatMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import pl.droidsonroids.gif.GifImageView;


public class Admin_Message_chat_with_user extends AppCompatActivity {
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
    String userUID, userEmail;
    GifImageView loadingscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_message_main);
        //Slidr.attach(this);
        Bundle b = getIntent().getExtras();
        userUID = b.getString("userUID");
        userEmail = b.getString("userEmail");
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(userEmail);
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        activity_chatboxmain = (RelativeLayout) findViewById(R.id.activity_chatboxmainactivity);
        btn_send_message = (FloatingActionButton) findViewById(R.id.fab);
        loadingscreen = (GifImageView) findViewById(R.id.loadingscreen);


        input = (EditText) findViewById(R.id.input);

        displayChatMessage();

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
                    .child(userUID)
                    .child("message")
                    .push()
                    .setValue(
                            new ChatMessage(
                                    message,
                                    "admin",
                                    System.currentTimeMillis(),
                                    userEmail,
                                    userUID));
            input.setText("");
            customListAdapter.notifyDataSetChanged();
            scrollMyListViewToBottom();
            sendNotification(userUID,"ADMIN: "+message);
        }

    }
    private void sendNotification(String userUID, String noiDung) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MzMwMWU4YTMtNmRjYi00NTFlLWJiZDMtNWUyMGI5NDYxMTYz");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"1de18513-2a3e-48d2-84d0-3124988e7a03\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + userUID + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"" + noiDung + "\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                Toast.makeText(this, getString(R.string.menu_sign_out_Toast), Toast.LENGTH_SHORT).show();
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
        customListAdapter = new Chat_Adapter(this, chatMessageList, "admin");
        mkey = new ArrayList<String>();
        listOfMessage.setAdapter(customListAdapter);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        if (userUID != null) {
            myRef.child("chat_message").child(userUID).child("message").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ChatMessage itemProduct = dataSnapshot.getValue(ChatMessage.class);
                    chatMessageList.add(itemProduct);
                    customListAdapter.notifyDataSetChanged();
                    scrollMyListViewToBottom();
                    loadingscreen.setVisibility(View.GONE);
                    // btn_send_message.setVisibility(View.VISIBLE);
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

