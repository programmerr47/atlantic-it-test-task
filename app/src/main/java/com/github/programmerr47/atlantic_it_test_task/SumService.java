package com.github.programmerr47.atlantic_it_test_task;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Michael Spitsin
 * @since 2014-09-07
 */
public class SumService extends IntentService{

    public static final String UPDATE_COUNTER_ACTION = "SumService.UPDATE_COUNTER_ACTION";
    public static final String FINISHED_COUNTING = "SumService.FINISHED_COUNTING";

    public static final String FIRST_NUMBER = "SumService.FIRST_NUMBER";
    public static final String SECOND_NUMBER = "SumService.SECOND_NUMBER";
    public static final String COUNTER_NUMBER = "SumService.COUNTER_NUMBER";
    public static final String RESULT_NUMBER = "SumService.RESULT_NUMBER";

    private double mCounterNumber;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SumService(String name) {
        super(name);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SumService() {
        super("SumService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final int firstNumber = intent.getIntExtra(FIRST_NUMBER, 0);
        final int secondNumber = intent.getIntExtra(SECOND_NUMBER, 0);
        int counterNumber = intent.getIntExtra(COUNTER_NUMBER, 0);
        mCounterNumber = 1.0 * counterNumber;

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                decrementCounter(0.5);

                if (mCounterNumber <= 0) {
                    timer.cancel();

                    LocalSettings localSettings = new LocalSettings(SumService.this);
                    localSettings.setPreviousResult(String.valueOf(firstNumber + secondNumber));
                    localSettings.setCouting(false);
                    localSettings.save();

                    Intent resultIntent = new Intent(FINISHED_COUNTING);
                    resultIntent.putExtra(RESULT_NUMBER, firstNumber + secondNumber);

                    sendBroadcast(resultIntent);
                    sendNotificationIfNeeded(firstNumber + secondNumber);
                } else {
                    Intent updateIntent = new Intent(UPDATE_COUNTER_ACTION);
                    updateIntent.putExtra(COUNTER_NUMBER, (int) Math.ceil(mCounterNumber));

                    sendBroadcast(updateIntent);
                }

            }
        }, 0, 500);
    }

    private void decrementCounter(double seconds) {
        mCounterNumber -= seconds;

        if (mCounterNumber < 0) {
            mCounterNumber = 0;
        }
    }

    private void sendNotificationIfNeeded(int result) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(getPackageName().toString())) {
            isActivityFound = true;
        }

        if (!isActivityFound) {
            sendNotification(result);
        }
    }

    //TODO future rewrite because of deprecated usage of Notifications
    private void sendNotification(int result) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, "Counted!" , new Date().getTime());

        String title = getString(R.string.app_name);

        Intent notificationIntent = new Intent(this, SumActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, title, String.format(getString(R.string.SUM_RESULT), String.valueOf(result)), intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);
    }
}
