package com.github.programmerr47.atlantic_it_test_task;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

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

                Intent updateIntent = new Intent(UPDATE_COUNTER_ACTION);
                updateIntent.putExtra(COUNTER_NUMBER, (int) Math.ceil(mCounterNumber));

                sendBroadcast(updateIntent);
            }
        }, 0, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.cancel();

                LocalSettings localSettings = new LocalSettings(SumService.this);
                localSettings.setPreviousResult(String.valueOf(firstNumber + secondNumber));
                localSettings.save();

                Intent resultIntent = new Intent(FINISHED_COUNTING);
                resultIntent.putExtra(RESULT_NUMBER, firstNumber + secondNumber);

                sendBroadcast(resultIntent);
            }
        }, counterNumber * 1000);
    }

    private void decrementCounter(double seconds) {
        mCounterNumber -= seconds;

        if (mCounterNumber < 0) {
            mCounterNumber = 0;
        }
    }
}
