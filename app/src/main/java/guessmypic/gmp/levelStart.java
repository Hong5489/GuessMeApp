package guessmypic.gmp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Byte4;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class levelStart extends Activity {
    private int level=1,remainLetter;
    private ArrayList<ImageInfo> imgInfo = new ArrayList<>();
    private char[] letterBtn, charAns2;
    private Button[] letter = new Button[18];
    private TextView[] ansText = new TextView[3];
    private String answer="";
    private User user, opponent;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseGame = FirebaseDatabase.getInstance().getReference("games");
    private StorageReference storageReference;
    private ArrayList<Character> charAns;
    private ImageView image;
    private TextView coin, playerScore,opponentScore;
    private AlertDialog.Builder dialog;
    private int letterBtnLen=0,numOfLetter,cycle=0,subPosition=0,hintCycle=0,hintPosition=0,numOfHintLeft;
    private int[] numOfAlpha = new int[3];
    private String gameId, opponentId;
    private Dialog winDialog;
    private ProgressDialog progressDialog;
    private final ArrayList<File> imageFile = new ArrayList<>();
    private final ArrayList<Integer> imageIndex = new ArrayList<>();
    private MediaPlayer mp;
    private boolean surrender = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_level_start);
        if(FindOpponentActivity.progressDialog.isShowing())
            FindOpponentActivity.progressDialog.dismiss();
        letter[0] = (Button) findViewById(R.id.letter1);
        letter[1] = (Button) findViewById(R.id.letter2);
        letter[2] = (Button) findViewById(R.id.letter3);
        letter[3] = (Button) findViewById(R.id.letter4);
        letter[4] = (Button) findViewById(R.id.letter5);
        letter[5] = (Button) findViewById(R.id.letter6);
        letter[6] = (Button) findViewById(R.id.letter7);
        letter[7] = (Button) findViewById(R.id.letter8);
        letter[8] = (Button) findViewById(R.id.letter9);
        letter[9] = (Button) findViewById(R.id.letter10);
        letter[10] = (Button) findViewById(R.id.letter11);
        letter[11] = (Button) findViewById(R.id.letter12);
        letter[12] = (Button) findViewById(R.id.letter13);
        letter[13] = (Button) findViewById(R.id.letter14);
        letter[14] = (Button) findViewById(R.id.letter15);
        letter[15] = (Button) findViewById(R.id.letter16);
        letter[16] = (Button) findViewById(R.id.letter17);
        letter[17] = (Button) findViewById(R.id.letter12);
        ansText[0] = (TextView) findViewById(R.id.answer1);
        ansText[1] = (TextView) findViewById(R.id.answer2);
        ansText[2] = (TextView) findViewById(R.id.answer3);
        image = (ImageView) findViewById(R.id.imageView);
        gameId = getIntent().getStringExtra("GAME_ID");
        user = UserManager.getInstance(this).getUserInfo();
        winDialog = new Dialog(levelStart.this);
        winDialog.setContentView(R.layout.dialog_level);
        winDialog.setCancelable(false);
        winDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Downloading images...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        databaseGame.child(gameId).child("image-info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot infoSnapshot : dataSnapshot.getChildren()){
                    ImageInfo info = infoSnapshot.getValue(ImageInfo.class);
                    imgInfo.add(info);
                }
                for(final ImageInfo eachImage : imgInfo){
                    final File localFile;
                    try {
                        localFile = File.createTempFile("images", "jpg");
                        storageReference.child(eachImage.getName()).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                imageFile.add(localFile);
                                imageIndex.add(imgInfo.indexOf(eachImage));
                                if(imageFile.size() == imgInfo.size()){
                                    ArrayList<ImageInfo> imgInfoCopy = new ArrayList<>(imgInfo.size());
                                    for(int i : imageIndex){
                                        imgInfoCopy.add(imgInfo.get(i));
                                    }
                                    imgInfo = imgInfoCopy;
                                    updateImage();
                                    generateImage();
                                    remainLetter = numOfHintLeft = charAns2.length;
                                    for(int i =0;i<numOfLetter;i++) {
                                        ansText[i].setVisibility(View.VISIBLE);
                                        for (int j = 0; j < numOfAlpha[i]; j++)
                                            answer = answer.concat("?");
                                        ansText[i].setText(answer);
                                        answer = "";
                                    }
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }catch (IOException exp){}
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                databaseGame.child(gameId).child("players").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                            String id = playerSnapshot.getKey();
                            if(!id.equals(firebaseUser.getUid())){
                                opponent = playerSnapshot.getValue(User.class);
                                opponentId = id;
                            }else user = playerSnapshot.getValue(User.class);
                        }
                        if((user.getWinCount() == imgInfo.size()) || (opponent.getWinCount() == imgInfo.size())){
                            TextView winStatus = (TextView) winDialog.findViewById(R.id.winStatus);
                            TextView totalCoinGet = (TextView) winDialog.findViewById(R.id.totalCoinGet);
                            Button onContinue = (Button) winDialog.findViewById(R.id.OnContinue);
                            DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("user-info");
                            onContinue.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                    startActivity(new Intent(getApplicationContext(),Menu.class));
                                }
                            });
                            if(user.getWinCount() == imgInfo.size()){
                                winStatus.setText("You Win!!!");
                                totalCoinGet.setText("You Get 50 coins");
                                databaseUser.child(firebaseUser.getUid()).child("coin").setValue(user.getCoin()+50);

                            }else {
                                if(!surrender) {
                                    winStatus.setText("You lose...");
                                    totalCoinGet.setText("You lose 25 coins");
                                    databaseUser.child(firebaseUser.getUid()).child("coin").setValue(user.getCoin() - 25);
                                }
                            }
                            winDialog.show();
                        }
                        playerScore.setText(user.getName() + " "+ user.getWinCount());
                        opponentScore.setText(opponent.getName() + " " + opponent.getWinCount());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        }).start();


        storageReference = FirebaseStorage.getInstance().getReference("images");
        coin = (TextView) findViewById(R.id.coin);
        coin.setText(String.valueOf(user.getCoin()));
        playerScore = (TextView) findViewById(R.id.playerScore);
        opponentScore = (TextView) findViewById(R.id.opponentScore);
    }
    public void onLetterClick(View view) {
        stopPlaying();
        mp = MediaPlayer.create(this,R.raw.type);
        mp.start();
        int id = view.getId();
        char letterAns = '\0';
        boolean correct = false;
        for (int i = 0; i < letterBtn.length; i++) {
            if (id == letter[i].getId()) {
                letterAns = letterBtn[i];
                correct = true;
            }
        }
        if (correct) {
            String preLetter = answer.substring(0, subPosition);
            answer = preLetter + letterAns;
            for(int i = 0;i<numOfAlpha[cycle]-subPosition-1;i++)
                answer = answer.concat("?");
            ansText[cycle].setText(answer);
            subPosition++;
            if((numOfAlpha[cycle]-subPosition-1)<0){
                cycle++;subPosition=0;
            }
            remainLetter--;
            if (remainLetter == 0) {
                dialog = new AlertDialog.Builder(this);
                dialog.setCancelable(false);
                String s="";
                for(int i =0;i<numOfLetter;i++)
                    s += ansText[i].getText();
                if (s.equals(String.valueOf(charAns2))) {
                    user.setWinCount(user.getWinCount()+1);
                    databaseGame.child(gameId).child("players").child(firebaseUser.getUid()).setValue(user);
                    if(level != imgInfo.size()) displayPass();
                } else {
                    stopPlaying();
                    mp = MediaPlayer.create(this,R.raw.wrong);
                    mp.start();
                    dialog.setTitle("Oops!!!");
                    dialog.setMessage("You failed level " + level + " !!!");
                    dialog.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateData();
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("Buy Hint with 25 coins", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (user.getCoin() >= 25) {
                                displayLastHint();
                                cycle = hintCycle; subPosition = hintPosition; buyHint();
                            } else {
                                Toast.makeText(levelStart.this, "Insufficient Fund!!!", Toast.LENGTH_SHORT).show();
                                displayDialog();
                            }
                        }
                    });
                    dialog.show();

                    }
                }
            }
        }

    public void onBackClick(View view)
    {
        if(cycle==0 && subPosition==0)Toast.makeText(this,"Failed to backspace!!!",Toast.LENGTH_SHORT).show();
        else {
            if (subPosition == 0) {
                cycle--;
                subPosition = numOfAlpha[cycle];
                answer = String.valueOf(ansText[cycle].getText());
            }
            subPosition--;
            answer = answer.substring(0, subPosition);
            for (int i = 0; i < numOfAlpha[cycle]-subPosition; i++)
                answer = answer.concat("?");
            ansText[cycle].setText(answer);
            remainLetter++;
        }
    }
    private void updateData()
    {
        remainLetter = numOfHintLeft = charAns2.length;
        answer = "";
        cycle = subPosition = hintCycle = hintPosition = 0;
        for(int i = 0;i<3;i++)
            ansText[i].setVisibility(View.INVISIBLE);
        for(int i =0;i<numOfLetter;i++) {
            ansText[i].setVisibility(View.VISIBLE);
            for (int j = 0; j < numOfAlpha[i]; j++)
                answer = answer.concat("?");
            ansText[i].setText(answer);
            answer = "";
        }
    }
    private void generateImage()
    {
        //String[] letters = ans[level-1].split(" ");
        String[] letters = imgInfo.get(level-1).getWord().split(" ");
        numOfLetter = letters.length;
        String newLetter = "";
        int counter=0;
        for(String s: letters) {
            newLetter += s;
            numOfAlpha[counter] = s.length();
            counter++;
        }
        charAns = new ArrayList<Character>();
        charAns2 = newLetter.toCharArray();
        letterBtnLen = 0;
        for (int i =0;i<newLetter.length();i++) {
            if(charAns.indexOf(charAns2[i]) == -1) {
                charAns.add(charAns2[i]);
                letterBtnLen++;
            }
        }
        int ranNum,i=0;
        letterBtn = new char[letterBtnLen];
        while(!charAns.isEmpty())
        {
            ranNum =(int) (Math.random()* charAns.size());
            letterBtn[i] = charAns.get(ranNum);
            String strAns = String.valueOf(charAns.get(ranNum));
            letter[i].setText(strAns);
            charAns.remove(ranNum);
            i++;
        }
    }
    private void displayDialog()
    {
        dialog.show();
    }
    private void displayPass()
    {
        stopPlaying();
        mp = MediaPlayer.create(this,R.raw.correct);
        mp.start();
        //if (level == user.getLevel()) {
        //}
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("Congratulation!!!");
        dialog.setMessage("You pass level " + level + " !!!");
        dialog.setNeutralButton("Continue to next level", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                level++;
                updateImage();
                for (int i = 0; i < letterBtnLen; i++)
                    letter[i].setText("");
                generateImage();
                updateData();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void buyHint() {
        stopPlaying();
        mp = MediaPlayer.create(this,R.raw.coins);
        mp.start();
        user.setCoin(user.getCoin() - 25);
        coin.setText(String.valueOf(user.getCoin()));
        displayLastHint();
        hintPosition++;numOfHintLeft--;
        remainLetter = numOfHintLeft;
        subPosition = hintPosition;
        if((numOfAlpha[hintCycle]-hintPosition-1)<0){
            hintCycle++;hintPosition=0;
            cycle = hintCycle;subPosition=hintPosition;
        }
        if(numOfHintLeft==0){
            user.setWinCount(user.getWinCount()+1);
            databaseGame.child(gameId).child("players").child(firebaseUser.getUid()).setValue(user);
            if(level != imgInfo.size()) displayPass();
        }
    }

    private void displayLastHint()
    {
        String[] letters = imgInfo.get(level-1).getWord().split(" ");
        answer = letters[hintCycle].substring(0, hintPosition + 1);
        for (int i = 1; i < numOfAlpha[hintCycle]-hintPosition; i++)
            answer = answer.concat("?");
        ansText[hintCycle].setText(answer);
    }

    public void onHintClick(View view)
    {
        if(user.getCoin()>=25){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle("Buy Hint Cost 25 Coins");
            dialog.setMessage("Are you sure??");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    buyHint();
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});
            dialog.show();
        }
        else Toast.makeText(this,"Insufficient fund!!!",Toast.LENGTH_SHORT);
    }

    private void updateImage(){
        Glide.with(getApplicationContext()).load(imageFile.get(level-1)).into(image);
    }
    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Quit Game");
        builder.setMessage("Are you sure? You will not lose any coins after you quit");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                surrender = true;
                databaseGame.child(gameId).child("players").child(opponentId).child("winCount").setValue(imgInfo.size());
                finish();
                startActivity(new Intent(getApplicationContext(),Menu.class));
            }
        });
        builder.show();
    }
}