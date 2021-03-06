package ir.radical_app.radical.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import ir.radical_app.radical.activities.MainActivity;
import ir.radical_app.radical.classes.Const;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.database.MyDatabase;
import ir.radical_app.radical.models.MessageModel;
import ir.radical_app.radical.R;

import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST;
import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST_MESSAGE_EXTRA;
import static ir.radical_app.radical.classes.Const.RADICAL_BROADCAST_SUPPORT_EXTRA;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String date = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ENGLISH).format(new Date(remoteMessage.getSentTime()));
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
        String sender = data.get("sender")+"";
        sender=sender.trim();
        MessageModel model = new MessageModel();
        model.setMessage(body);
        model.setTitle(title);
        model.setSender(sender);
        model.setDate(date);
        MyDatabase myDatabase = new MyDatabase(this);
        Intent intent = new Intent();
        intent.setAction(RADICAL_BROADCAST);

        if(sender.contains("admin")||sender.equals("1")) {
            model.setRead("0");
            myDatabase.saveSupport(model);
            intent.putExtra(RADICAL_BROADCAST_SUPPORT_EXTRA,"new");
            sendBroadcast(intent);
            createNotification("پیام جدید", "پیام جدید از طرف پشتیانی","support");

        }
        else {
            myDatabase.saveMessage(model);
            intent.putExtra(RADICAL_BROADCAST_MESSAGE_EXTRA,"new");
            sendBroadcast(intent);
            createNotification(title, body,"notification");
        }

    }


    @Override
    public void onNewToken(String s) {
        MySharedPreference.Companion.getInstance(this).setFBToken(s);
        super.onNewToken(s);
    }

    private void createNotification(String title, String message,String intentValue){

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notidata",intentValue);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Const.CHANNEL_CODE);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setSound(alarmSound, AudioManager.STREAM_NOTIFICATION);
        builder.setVibrate(new long[] {1000,1000});
        builder.setLights(Color.YELLOW,1000,1000);
        NotificationManagerCompat notificationManagerCompat =  NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(Const.NOTIFICATION_ID,builder.build());


    }


    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            CharSequence name = "Radical Message Notifications";
            String description = "Using this channel to display notification for radical app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(Const.CHANNEL_CODE,name,importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
