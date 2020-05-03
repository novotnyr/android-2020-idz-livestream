package com.github.novotnyr.android.idz;

import android.app.*;
import android.content.Intent;
import android.os.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import androidx.core.app.*;
import retrofit2.*;

public class NavigationService extends Service {
    private static final String CHANNEL_ID = "0";
    public static final int NOTIFICATION_ID = 1;

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private AtomicBoolean initialized = new AtomicBoolean();

    public NavigationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Notification notification = createNotification("Žiadne inštrukcie");
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        executorService.shutdownNow();
        initialized.set(false);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (executorService.isShutdown()) {
            return START_STICKY;
        }
        if (initialized.compareAndSet(false, true)) {
            NavigationTask navigationTask = new NavigationTask();
            executorService.scheduleAtFixedRate(navigationTask, 0, 5, TimeUnit.SECONDS);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification(String direction) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("SHOW_ACTIVITY");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentTitle("Idz podľa navigácie: " + direction)
                .setContentText(direction)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(android.R.drawable.ic_menu_delete, "Ukonči navigáciu", pendingIntent)
                .build();
    }

    public class NavigationTask implements Runnable {

        @Override
        public void run() {
            IdzApi.API.getDirection().enqueue(new Callback<IdzApi.Direction>() {
                @Override
                public void onResponse(Call<IdzApi.Direction> call, Response<IdzApi.Direction> response) {
                    String direction = response.body().getDirection();
                    Notification notification = createNotification(direction);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NavigationService.this);
                    notificationManager.notify(NOTIFICATION_ID, notification);
                }

                @Override
                public void onFailure(Call<IdzApi.Direction> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Idz";
            String description = "Navigácia?";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
