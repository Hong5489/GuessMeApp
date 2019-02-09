package guessmypic.gmp;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.app.Notification.PRIORITY_MAX;

/**
 * Created by HONGWEI on 2018/9/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    public static final int NOTIFICATION_ID = 666;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseGame = FirebaseDatabase.getInstance().getReference("games");
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification().getBody().equals("gameStart")){
            databaseGame.child(remoteMessage.getData().get("id")).child("players").child(user.getUid()).setValue(UserManager.getInstance(getApplicationContext()).getUserInfo());
            FindOpponentActivity.progressDialog.dismiss();
            startActivity(new Intent(getApplicationContext(),levelStart.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("GAME_ID",remoteMessage.getData().get("id")));
        }else {
            showNotification(remoteMessage.getNotification().getBody(), remoteMessage.getData().get("id"),remoteMessage.getData().get("type"));
        }
    }

    public void showNotification(String notification,String id,String type)
    {
        Notification myNotification;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        if(type.equals("invite")) {
            PendingIntent pendingIntentReject = PendingIntent.getBroadcast(
                    this,
                    NOTIFICATION_ID,
                    new Intent(getApplicationContext(), MyReceiver.class)
                            .putExtra("ID", id)
                            .setAction("reject"),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(
                    this,
                    NOTIFICATION_ID,
                    new Intent(getApplicationContext(), MyReceiver.class)
                            .putExtra("ID", id)
                            .setAction("accept"),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            myNotification = builder.setSmallIcon(R.mipmap.ic_launcher)
                    .addAction(R.drawable.ok, "Accept", pendingIntentAccept)
                    .addAction(R.drawable.reject, "Reject", pendingIntentReject)
                    .setPriority(PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{500, 1000})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(notification)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .build();
        }else{
            myNotification = builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{500, 1000})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(notification)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .build();
            if(type.equals("reject")) FindOpponentActivity.progressDialog.dismiss();
        }
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,myNotification);
    }
}
