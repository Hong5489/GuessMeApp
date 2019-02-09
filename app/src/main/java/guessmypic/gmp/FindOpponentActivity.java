package guessmypic.gmp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

public class FindOpponentActivity extends Activity{

    static RecyclerView recyclerView;
    UserAdapter adapter;
    static List<UserOnline> userList;
    DatabaseReference databaseUser;
    static String name;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_find_opponent);
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        databaseUser = FirebaseDatabase.getInstance().getReference("online");
        userList = new ArrayList<>();
        Intent intent = getIntent();
        name = intent.getExtras().getString("NAME");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Waiting the player response...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser.child(user.getUid()).child("name").setValue(UserManager.getInstance(FindOpponentActivity.this).getUserInfo().getName());
        databaseUser.child(user.getUid()).child("token").setValue(UserManager.getInstance(FindOpponentActivity.this).getToken());
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    UserOnline user = userSnapshot.getValue(UserOnline.class);
                    userList.add(user);
                }
                adapter = new UserAdapter(FindOpponentActivity.this,userList);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    static class UserViewHolder extends RecyclerView.ViewHolder{

        public TextView nameText;
        private DatabaseReference databaseInvite;
        public UserViewHolder(final View userView){
            super(userView);
            nameText = (TextView) userView.findViewById(R.id.userText);
            databaseInvite = FirebaseDatabase.getInstance().getReference("Invite");

            userView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildLayoutPosition(userView);
                    UserOnline user = userList.get(position);
                    if(!user.getToken().equals(UserManager.getInstance(v.getContext()).getToken())) {
                        String id = databaseInvite.push().getKey();
                        Invite invite = new Invite(name, UserManager.getInstance(v.getContext()).getToken(), user.getName(), user.getToken());
                        databaseInvite.child(id).setValue(invite);
                        progressDialog.show();
                    }else Toast.makeText(v.getContext(),"Cannot select yourself!!!",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser.child(user.getUid()).removeValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser.child(user.getUid()).child("name").setValue(UserManager.getInstance(FindOpponentActivity.this).getUserInfo().getName());
        databaseUser.child(user.getUid()).child("token").setValue(UserManager.getInstance(FindOpponentActivity.this).getToken());
    }
}
