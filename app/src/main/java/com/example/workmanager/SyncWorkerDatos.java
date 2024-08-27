package com.example.workmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class SyncWorkerDatos extends Worker {

    private static final String TAG = "SyncWorkerDatos";
    private static final String CHANNEL_ID = "SYNC_NOTIFICATION_CHANNEL";

    public SyncWorkerDatos(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        logSyncStart();

        boolean syncResult = performSync();

        return processSyncResult(syncResult);
    }

    private void logSyncStart() {
        Log.d(TAG, "Iniciando el proceso de sincronización...");
    }

    private boolean performSync() {
        // Logica para antes de sincronizar con el servidor
        return executeSyncTask();
    }

    private boolean executeSyncTask() {
        // Logica que simule conectarse a un servidor
        return true;
    }

    private Result processSyncResult(boolean isSuccess) {
        if (isSuccess) {
            Log.d(TAG, "Sincronizacion excitoza.");
            showNotification("Sincroniation", "Sincronizacion excitoza.");
            return Result.success();
        } else {
            Log.d(TAG, "Reintentando sincronización tras error.");
            showNotification("Sincronización", "Fallo en la sincronización, intentado volver a sincronizar.");
            return Result.retry();
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Sync Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setAutoCancel(true);

        notificationManager.notify(1, notificationBuilder.build());
    }

    // Configuracion de aplicacion al inicio
    public static void scheduleSync(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(SyncWorkerDatos.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueue(syncRequest);
    }
}


