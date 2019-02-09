package guessmypic.gmp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;


public class Menu extends Activity implements RewardedVideoAdListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private User userInfo;
    private DatabaseReference databaseOnline, databaseUser;
    private Dialog dialog, registerDialog, forgetDialog ,profileDialog, resetDialog;
    private String name;
    private ProgressDialog progressDialog;
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        Log.d("msg","hello");
        databaseOnline = FirebaseDatabase.getInstance().getReference("online");
        databaseUser = FirebaseDatabase.getInstance().getReference("user-info");
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_sign_in);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        registerDialog = new Dialog(this);
        registerDialog.setCancelable(false);
        registerDialog.setContentView(R.layout.activity_sign_up);
        registerDialog.setCancelable(false);
        registerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        forgetDialog = new Dialog(this);
        forgetDialog.setCancelable(false);
        forgetDialog.setContentView(R.layout.activity_reset_password);
        forgetDialog.setCancelable(false);
        forgetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText email = (EditText) dialog.findViewById(R.id.editTextEmail2);
        final EditText password = (EditText) dialog.findViewById(R.id.editTextPassword2);
        Button onSignInClick = (Button) dialog.findViewById(R.id.buttonSignIn);
        TextView onForgetPwd = (TextView) dialog.findViewById(R.id.onForgetPwd);
        TextView onRegisterClick = (TextView) dialog.findViewById(R.id.onRegisterClick);
        onSignInClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passText = password.getText().toString();
                if(TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passText)) {
                    Toast.makeText(Menu.this,"Please enter email and password properly!!", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.signInWithEmailAndPassword(emailText,passText)
                            .addOnCompleteListener(Menu.this, new OnCompleteListener<AuthResult>(){
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task){
                                    if(task.isSuccessful()){
                                        Toast.makeText(Menu.this,"Successfully Log In!!!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),Menu.class));
                                    }else Toast.makeText(Menu.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        onRegisterClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                registerDialog.show();
            }
        });
        onForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                forgetDialog.show();
            }
        });
        final EditText registerEmail = (EditText) registerDialog.findViewById(R.id.editTextEmail);
        final EditText registerPassword = (EditText) registerDialog.findViewById(R.id.editTextPassword);
        final EditText nameText = (EditText) registerDialog.findViewById(R.id.editTextUsername);
        final EditText confirmPassword = (EditText) registerDialog.findViewById(R.id.editTextPassword2);
        final Button buttonRegister = (Button) registerDialog.findViewById(R.id.buttonSignUp);
        TextView signInText = (TextView) registerDialog.findViewById(R.id.signInText);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = registerEmail.getText().toString().trim();
                String passText = registerPassword.getText().toString();
                String passText2 = confirmPassword.getText().toString();
                final String usernameText = nameText.getText().toString().trim();
                if(TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passText) || TextUtils.isEmpty(passText2)) {
                    Toast.makeText(Menu.this,"Please enter email and password properly!!", Toast.LENGTH_SHORT).show();
                }else{
                    if(!passText.equals(passText2)){
                        Toast.makeText(Menu.this,"Password does not match", Toast.LENGTH_SHORT).show();
                    }else {
                        firebaseAuth.createUserWithEmailAndPassword(emailText, passText)
                                .addOnCompleteListener(Menu.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Menu.this, "Successfully registered!!!", Toast.LENGTH_SHORT).show();
                                            User user = new User(usernameText, 100);
                                            databaseUser.child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
                                            startActivity(new Intent(getApplicationContext(), Menu.class));
                                        } else
                                            Toast.makeText(Menu.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDialog.dismiss();
                dialog.show();
            }
        });
        final EditText forgetEmail = (EditText) forgetDialog.findViewById(R.id.editTextReset);
        Button sendEmail = (Button) forgetDialog.findViewById(R.id.sendEmail);
        Button back = (Button) forgetDialog.findViewById(R.id.backButton);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgetEmail.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Please enter your email properly",Toast.LENGTH_SHORT).show();
                }else {
                    ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                            .setAndroidPackageName(
                                    getPackageName(),
                                    true,
                                    null)
                            .setUrl("https://guessme.page.link")
                            .setHandleCodeInApp(true)
                            .build();
                    firebaseAuth.sendPasswordResetEmail(email,settings).addOnCompleteListener(Menu.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Email send successfully",Toast.LENGTH_SHORT).show();
                                forgetDialog.dismiss();
                                dialog.show();
                            }else Toast.makeText(Menu.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetDialog.dismiss();
                dialog.show();
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Authenticating");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        profileDialog = new Dialog(Menu.this);
        profileDialog.setContentView(R.layout.dialog_profile);
        profileDialog.setCancelable(true);
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if(user == null) {
            dialog.show();
        }else{
            progressDialog.show();
            databaseUser.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    UserManager.getInstance(Menu.this).storeUserInfo(u);
                    userInfo = UserManager.getInstance(Menu.this).getUserInfo();
                    name = userInfo.getName();
                    progressDialog.dismiss();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    public void onClick(View view) {
        startActivity(new Intent(view.getContext(),FindOpponentActivity.class).putExtra("NAME",name));
    }
    public void onClickSetting(View view)
    {
        startActivity(new Intent(view.getContext(),settingActivity.class));
    }

    public void onClickHow(View view)
    {
        startActivity(new Intent(view.getContext(),HowActivity.class));
    }


    public void onClickLogout(View view){
        databaseOnline.child(user.getUid()).removeValue();
        firebaseAuth.signOut();
        dialog.show();
    }

    public void onClickTop(View view)
    {
        startActivity(new Intent(getApplicationContext(),TopScoreActivity.class));
    }

    public void OnProfileClick(View view){
        final TextView username = (TextView) profileDialog.findViewById(R.id.username);
        TextView coins = (TextView) profileDialog.findViewById(R.id.coins);
        Button buttonEditName = (Button) profileDialog.findViewById(R.id.buttonEditName);
        Button buttonEditEmail = (Button) profileDialog.findViewById(R.id.buttonEditEmail);
        Button buttonResetPwd = (Button) profileDialog.findViewById(R.id.buttonResetPwd);
        Button back = (Button) forgetDialog.findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetDialog.dismiss();
                profileDialog.show();
            }
        });
        final EditText editText = (EditText) forgetDialog.findViewById(R.id.editTextReset);
        final Button button = (Button) forgetDialog.findViewById(R.id.sendEmail);
        buttonEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileDialog.dismiss();
                editText.setHint("Enter Your Name");
                editText.setText("");
                button.setText("Edit");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = editText.getText().toString();
                        if(TextUtils.isEmpty(name)){
                            Toast.makeText(Menu.this,"Please enter a name!!", Toast.LENGTH_LONG).show();
                        }else{
                            databaseUser.child(user.getUid()).child("name").setValue(name);
                            Toast.makeText(Menu.this,"Profile Updated", Toast.LENGTH_SHORT).show();
                            forgetDialog.dismiss();
                            username.setText(name);
                            profileDialog.show();
                        }
                    }
                });
                forgetDialog.show();
            }
        });
        buttonEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileDialog.dismiss();
                editText.setHint("Enter Your New Email");
                editText.setText("");
                button.setText("Edit");
                button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      String email = editText.getText().toString();
                      if (TextUtils.isEmpty(name)) {
                          Toast.makeText(Menu.this, "Field cannot be empty", Toast.LENGTH_LONG).show();
                      } else {
                          user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if (task.isSuccessful()) {
                                      Toast.makeText(getApplicationContext(), "Email Updated", Toast.LENGTH_SHORT).show();
                                      forgetDialog.dismiss();
                                      profileDialog.show();
                                  } else
                                      Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                              }
                          });
                      }
                  }
              });
                forgetDialog.show();
            }
        });
        buttonResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetDialog = new Dialog(Menu.this);
                resetDialog.setContentView(R.layout.dialog_reset_password);
                resetDialog.setCancelable(true);
                resetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final EditText editTextPwd = (EditText) resetDialog.findViewById(R.id.editTextPwd);
                final EditText editTextNewPwd = (EditText) resetDialog.findViewById(R.id.editTextNewPwd);
                final EditText editTextConfirmPwd = (EditText) resetDialog.findViewById(R.id.editTextConfirmPwd);
                Button back = (Button) resetDialog.findViewById(R.id.back);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetDialog.dismiss();
                        profileDialog.show();
                    }
                });
                Button update = (Button) resetDialog.findViewById(R.id.update);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pwd = editTextPwd.getText().toString();
                        final String newPwd = editTextNewPwd.getText().toString();
                        String confirmPwd = editTextConfirmPwd.getText().toString();
                        if(TextUtils.isEmpty(pwd) || TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(confirmPwd)){
                            Toast.makeText(getApplicationContext(),"All fields cannot be empty",Toast.LENGTH_LONG).show();
                        }else{
                            if(!newPwd.equals(confirmPwd)){
                                Toast.makeText(getApplicationContext(),"Password not match",Toast.LENGTH_LONG).show();
                            }else{
                                user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(),pwd)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            user.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                                                        resetDialog.dismiss();
                                                        profileDialog.show();
                                                    }
                                                    else Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }else Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                });
                resetDialog.show();
            }
        });
        username.setText(name);
        coins.setText(String.valueOf(userInfo.getCoin()));
        profileDialog.show();
    }

    public void OnAdsClick(View view)
    {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }
}
