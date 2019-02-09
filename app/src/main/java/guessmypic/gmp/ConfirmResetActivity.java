package guessmypic.gmp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class ConfirmResetActivity extends Activity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private TextView emailText;
    private Uri deepLink;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirm_reset);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Verifying");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        emailText = (TextView) findViewById(R.id.resetPwdEmail);
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        firebaseAuth.verifyPasswordResetCode(deepLink.getQueryParameter("oobCode")).addOnCompleteListener(ConfirmResetActivity.this, new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if(task.isSuccessful()){
                                    emailText.setText(task.getResult());
                                    progressDialog.dismiss();
                                }else Toast.makeText(ConfirmResetActivity.this,task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }

    public void onResetPwd(View view){
        EditText pwd = (EditText) findViewById(R.id.editTextPwd);
        EditText pwd2 = (EditText) findViewById(R.id.editTextPwd2);
        String p1 = pwd.getText().toString();
        String p2 = pwd2.getText().toString();
        if(TextUtils.isEmpty(p1) || TextUtils.isEmpty(p2)) {
            Toast.makeText(getApplicationContext(),"Both field cannot be empty",Toast.LENGTH_SHORT).show();
        }else{
            if(!p1.equals(p2)){
                Toast.makeText(getApplicationContext(),"Both password not match",Toast.LENGTH_SHORT).show();
            }else {
                firebaseAuth.confirmPasswordReset(deepLink.getQueryParameter("oobCode"),p1).addOnCompleteListener(ConfirmResetActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Reset password successful",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(),Menu.class));
                        }else Toast.makeText(ConfirmResetActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
