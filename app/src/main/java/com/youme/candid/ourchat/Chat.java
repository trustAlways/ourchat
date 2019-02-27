package com.youme.candid.ourchat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseError;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    String strDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chit Chat");


        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);


         final   Calendar c = Calendar.getInstance();
         SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
         strDate = sdf.format(c.getTime());

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://ourchat-bae78.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://ourchat-bae78.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                Firebase reference = new Firebase("https://ourchat-bae78.firebaseio.com/message");

                //reference.child(UserDetails.username).setValue(messageText);
                String id = reference.push().getKey();
                reference.child(id).child(UserDetails.username)
                        .child(UserDetails.chatWith).setValue(messageText);
                reference.child(id).child(UserDetails.username).child("timestamp")
                       .setValue(c.getTimeInMillis());

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    //map.put("timestamp",(DateFormat.getDateTimeInstance().format(strDate)));
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });




        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                //String time = map.get("timestamp").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox("You\n "+ message, 1);
                }
                else{
                    addMessageBox(UserDetails.chatWith+" \n " + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(com.firebase.client.FirebaseError firebaseError) {

            }


        });
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.setMargins(5,5,5,0);
       // lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.rounded_corner);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            //textView.setBackgroundColor(Color.WHITE);

        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundResource(R.drawable.corner_rounded);
            textView.setTextSize(18);

        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        //scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
