package guessmypic.gmp;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends Activity {
    private FirebaseUser user;
    private DatabaseReference databaseUser;
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference("user-info");
        name = (EditText) findViewById(R.id.displayName);
    }

    public void OnUpdateNameClick(View view){
        String nameText = name.getText().toString().trim();
        if(nameText == ""){
            Toast.makeText(this,"Please enter a name!!", Toast.LENGTH_SHORT).show();
        }else{
            databaseUser.child(user.getUid()).child("name").setValue(nameText);
            Toast.makeText(this,"Profile updated",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(),Menu.class));
        }
    }
}
