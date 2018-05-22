package com.amazing_apps.ray.spd;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import android.provider.Settings.Secure;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    Firebase Root,Players;
    DatabaseReference databaseReference,databaseReference1,databaseReference2,databaseReference3;
    ProgressBar progressBar,progBar;
    Thread thread;
    Boolean connected=false,authenticated=false,lobbyfill=false;
    TextView tv_loading;
    String android_id,Username;
    int participant_num;
    int widthpx,heightpx;

    //do


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightpx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, displayMetrics.heightPixels, getResources().getDisplayMetrics());
        widthpx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, displayMetrics.widthPixels, getResources().getDisplayMetrics());

        // first page is loaded
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstpage);

        Firebase.setAndroidContext(this);


        // Root is Root node of database
        Root = new Firebase("https://spdfirebaseproject-e82f0.firebaseio.com/");
        //Players is child of root
        Players = Root.child("Players");
        //to get unique id of android device
        android_id = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);
        //A reference to database for initial connection
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Services");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Players");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Lobby");
        databaseReference3 = FirebaseDatabase.getInstance().getReference().child("Lobby");



        // A progress bar to indicate if data can be retrieved or not
        progressBar =(ProgressBar)findViewById(R.id.id_progressbar);
        tv_loading =(TextView)findViewById(R.id.loading_text);

        //A thread to handle progress of progress bar
        thread = new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i=0;i<100;i++)
                {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // if connection is going on increase progress
                    if(!connected)
                    progressBar.setProgress(i);

                }
            }
        };

        //call childevent listener for database reference
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    //connection established with database
                    //shows Loading complete

                    progressBar.setProgress(100);
                    connected = true;
                    display_user();          //this function tells if user is new/old

//                    //create a new login button
//                    login = new Button(getApplicationContext());
//                    login.setText("LOGIN");
//
//                    //main layout of firstpage
//                    LinearLayout linearLayout =(LinearLayout)findViewById(R.id.id_firstpagell);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    linearLayout.addView(login,layoutParams);
//
//                    login.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            display_main();
//                        }
//                    });
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //start the thread
        thread.start();
    }

    public void display_user()
    {
        progressBar.setProgress(30);
        tv_loading.setText("Authenticating");

        thread = new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i=0;i<100;i++)
                {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // if connection is going on increase progress
                    if(!authenticated)
                        progressBar.setProgress(i);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Stuff that updates the UI
                            if(progressBar.getProgress()>50)
                                display_new_user();

                        }
                    });
                }
            }
        };




        databaseReference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.hasChild(android_id))
                {
                    Username = dataSnapshot.child(android_id).getValue().toString();
                    authenticated=true;
                    display_lobby();
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        thread.start();
    }

    public void display_main()
    {
        setContentView(R.layout.activity_main);


        Button bt_single_player,bt_multi_player,bt_settings,bt_new_user;
        bt_single_player = findViewById(R.id.id_button_single_player);
        bt_multi_player = findViewById(R.id.id_button_multiplayer);
        bt_settings = findViewById(R.id.id_button_settings);

        bt_single_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_lobby();
            }
        });

    }

    public void display_new_user()
    {
        setContentView(R.layout.new_user);
        final EditText et_username,et_firstname,et_lastname;
        Button bt_play;

        et_username =(EditText)findViewById(R.id.id_playername);
        bt_play =(Button)findViewById(R.id.id_play_new_user);
        et_firstname =(EditText)findViewById(R.id.id_firstname);
        et_lastname =(EditText)findViewById(R.id.id_lastname);


        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_username.getText().toString().length()<8)
                {
                    et_username.setError("minimum 8 charaters required");
                }
                if(et_username.getText().toString().length()==0)
                {
                    et_username.setError("Field cannot be empty");
                }
                if(et_firstname.getText().toString().length()==0)
                {
                    et_firstname.setError("Field cannot be empty");
                }
                if(et_lastname.getText().toString().length()==0)
                {
                    et_lastname.setError("Field cannot be empty");
                }

                if(et_username.getText().toString().length()>=8 && et_firstname.getText().toString().length()>0 && et_lastname.getText().toString().length()>0)
                {
                    Username =et_username.getText().toString();
                    Players.child(android_id).child(android_id).setValue(et_username.getText().toString());
                    Players.child(android_id).child("First name").setValue(et_firstname.getText().toString());
                    Players.child(android_id).child("Last name").setValue(et_lastname.getText().toString());
                    display_lobby();
                }
            }
        });
    }

    public void display_lobby()
    {

        setContentView(R.layout.lobby);

        Button bt_new_lobby;
        bt_new_lobby = findViewById(R.id.id_newlobby);
        final EditText et_new_lobby;
        et_new_lobby =findViewById(R.id.id_et_create_lobby);
        final ArrayList<String> arrayList = new ArrayList<>();
        ListView lobbylist =(ListView)findViewById(R.id.id_lobby_list);
        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        lobbylist.setAdapter(arrayAdapter);


        databaseReference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String ss = dataSnapshot.child("Lobby Name").getValue().toString();
                arrayList.add(ss);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                arrayList.remove(dataSnapshot.child("Lobby Name").getValue().toString());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        bt_new_lobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Creating new Lobby",Toast.LENGTH_SHORT).show();

                if(et_new_lobby.getText().toString().length()<8)
                {
                    et_new_lobby.setError("minimum 8 charaters required");
                }
                else
                {
                    if(!arrayList.contains(et_new_lobby.getText().toString()))
                    {
                        Root.child("Lobby").child(et_new_lobby.getText().toString()).child("Lobby Name").setValue(et_new_lobby.getText().toString());
                        Root.child("Lobby").child(et_new_lobby.getText().toString()).child("Participant 1").setValue(Username);
                        Root.child("Lobby").child(et_new_lobby.getText().toString()).child("Participant 2").setValue("empty");
                        Root.child("Lobby").child(et_new_lobby.getText().toString()).child("Participant 3").setValue("empty");
                        Root.child("Lobby").child(et_new_lobby.getText().toString()).child("Participant 4").setValue("empty");

                        Toast.makeText(getApplicationContext(),"Lobby created "+et_new_lobby.getText().toString(),Toast.LENGTH_SHORT).show();
                        display_game();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Lobby already exists",Toast.LENGTH_SHORT).show();
                    }
                }


//                display_game();
            }
        });


        //initialize listview listener

        lobbylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                progBar =new ProgressBar(getApplicationContext(),null,android.R.attr.progressBarStyleHorizontal);
                LinearLayout linearLayout =(LinearLayout)findViewById(R.id.lobby_layout);

                LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.addView(progBar,layoutParams);

                thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        for(int i=0;i<100;i++)
                        {
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // if connection is going on increase progress
                            if(!lobbyfill)
                                progBar.setProgress(i);

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    // Stuff that updates the UI
                                    if(progBar.getProgress()>50)
                                        Toast.makeText(getApplicationContext(),"Lobby is Full",Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                };

                Query query = databaseReference3.orderByChild("Lobby Name").equalTo(arrayList.get(position));


                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String st2 = dataSnapshot.child("Participant 2").getValue().toString();
                        String st3 = dataSnapshot.child("Participant 3").getValue().toString();
                        String st4 = dataSnapshot.child("Participant 4").getValue().toString();


                        if(st2.equals("empty"))
                        {
                            participant_num =2;
                            lobbyfill=true;
                            Root.child("Lobby").child(arrayList.get(position)).child("Participant 2").setValue(Username);
                            display_game();
                        }
                        else if(st3.equals("empty"))
                        {
                            participant_num =3;
                            lobbyfill=true;
                            Root.child("Lobby").child(arrayList.get(position)).child("Participant 3").setValue(Username);
                            display_game();
                        }
                        else if(st4.equals("empty"))
                        {
                            participant_num =4;
                            lobbyfill=true;
                            Root.child("Lobby").child(arrayList.get(position)).child("Participant 4").setValue(Username);
                            display_game();
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
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                thread.start();
            }
        });


    }

    public void display_game()
    {
        setContentView(R.layout.game);
        Button startbutton;

        //height and width of screen

        startbutton =(Button)findViewById(R.id.start_button);

        startbutton.setBackground(new BitmapDrawable(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.cb), widthpx/2, widthpx/2, false)));

    }

}
