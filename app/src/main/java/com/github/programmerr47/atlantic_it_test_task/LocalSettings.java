package com.github.programmerr47.atlantic_it_test_task;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Michael Spitsin
 * @since 2014-09-07
 */
@SuppressWarnings("unused")
public class LocalSettings {

    private static final String FIRST_NUMBER = "FIRST_NUMBER";
    private static final String SECOND_NUMBER = "SECOND_NUMBER";
    private static final String COUNTER_NUMBER = "COUNTER_NUMBER";
    private static final String PREVIOUS_RESULT = "PREVIOUS_RESULT";
    private static final String IS_STILL_COUNTING = "IS_STILL_COUNTING";

    private Context mContext;

    private String mFirstNumber;
    private String mSecondNumber;
    private String mCounterNumber;
    private String mPreviousResult;

    private boolean isStillCounting;

    public LocalSettings(Context context) {
        if (context == null) {
            throw new NullPointerException("Context must be not null");
        }

        mContext = context;
        restore();
    }

    public boolean isCorrect() {
        boolean isCorrect = true;
        try {
            int intValue = Integer.parseInt(mFirstNumber);
        } catch (NumberFormatException e) {
            isCorrect = false;
        }

        if (isCorrect) {
            try {
                int intValue = Integer.parseInt(mSecondNumber);
            } catch (NumberFormatException e) {
                isCorrect = false;
            }
        }

        if (isCorrect) {
            try {
                int intValue = Integer.parseInt(mCounterNumber);
            } catch (NumberFormatException e) {
                isCorrect = false;
            }
        }

        return isCorrect;
    }

    public String getFirstNumber() {
        return mFirstNumber;
    }

    public String getSecondNumber() {
        return mSecondNumber;
    }

    public String getCounterNumber() {
        return mCounterNumber;
    }

    public String getPreviousResult() {
        return mPreviousResult;
    }

    public boolean isStillCounting() {
        return isStillCounting;
    }

    public void setPreviousResult(String mPreviousResult) {
        this.mPreviousResult = mPreviousResult;
    }

    public void setFirstNumber(String mFirstNumber) {
        this.mFirstNumber = mFirstNumber;
    }

    public void setSecondNumber(String mSecondNumber) {
        this.mSecondNumber = mSecondNumber;
    }

    public void setCounterNumber(String mCounterNumber) {
        this.mCounterNumber = mCounterNumber;
    }

    public void setCouting(boolean isStillCounting) {
        this.isStillCounting = isStillCounting;
    }
    /**
     * Saves necessary data using {@link android.content.SharedPreferences}.
     */
    public void save() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(FIRST_NUMBER, mFirstNumber);
        editor.putString(SECOND_NUMBER, mSecondNumber);
        editor.putString(COUNTER_NUMBER, mCounterNumber);
        editor.putString(PREVIOUS_RESULT, mPreviousResult);
        editor.putBoolean(IS_STILL_COUNTING, isStillCounting);

        editor.commit();
    }

    /**
     * Clear all data, that saved.
     */
    public void clear(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(FIRST_NUMBER);
        editor.remove(SECOND_NUMBER);
        editor.remove(COUNTER_NUMBER);
        editor.remove(PREVIOUS_RESULT);
        editor.remove(IS_STILL_COUNTING);

        editor.commit();

        mFirstNumber = null;
        mSecondNumber = null;
        mCounterNumber = null;
        mPreviousResult = null;
        isStillCounting = false;
    }

    /**
     * Restores access token and user id for further use.
     */
    private void restore() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        mFirstNumber = prefs.getString(FIRST_NUMBER, null);
        mSecondNumber = prefs.getString(SECOND_NUMBER, null);
        mCounterNumber = prefs.getString(COUNTER_NUMBER, null);
        mPreviousResult = prefs.getString(PREVIOUS_RESULT, null);
        isStillCounting = prefs.getBoolean(IS_STILL_COUNTING, false);
    }
}
