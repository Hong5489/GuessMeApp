package guessmypic.gmp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by HONGWEI on 2018/9/22.
 */

public class MyReceiver extends BroadcastReceiver {
    private DatabaseReference databaseInvite;
    @Override
    public void onReceive(Context context, Intent intent) {
        String id = intent.getStringExtra("ID");
        String acceptence = intent.getAction();
        databaseInvite = FirebaseDatabase.getInstance().getReference("Invite");
        databaseInvite.child(id).child("status").setValue(acceptence);
        if(acceptence.equals("accept")){
            FindOpponentActivity.progressDialog.setTitle("Connecting...");
            FindOpponentActivity.progressDialog.show();
        }
    }
}
