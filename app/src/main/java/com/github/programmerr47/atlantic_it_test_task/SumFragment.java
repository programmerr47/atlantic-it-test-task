package com.github.programmerr47.atlantic_it_test_task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Michael Spitsin
 * @since 2014-09-07
 */
public class SumFragment extends Fragment implements View.OnClickListener {

    private static final String UNKNOWN_PREVIOUS_RESULT = "?";

    private EditText mFirstNumberEditText;
    private EditText mSecondNumberEditText;
    private EditText mTimerDelayEditText;
    private TextView mSumResultTextView;
    private Button mStartCounterButton;

    private LocalSettings mLocalSettings;
    private SumReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sum, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mFirstNumberEditText = (EditText) view.findViewById(R.id.firstNumberEditText);
        mSecondNumberEditText = (EditText) view.findViewById(R.id.secondNumberEditText);
        mTimerDelayEditText = (EditText) view.findViewById(R.id.timerDelayEditText);
        mSumResultTextView = (TextView) view.findViewById(R.id.sumResultTextView);
        mStartCounterButton = (Button) view.findViewById(R.id.startCounterButton);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocalSettings = new LocalSettings(getActivity());

        if (mLocalSettings.getFirstNumber() != null) {
            mFirstNumberEditText.setText(mLocalSettings.getFirstNumber());
        } else {
            mFirstNumberEditText.setText("");
        }

        if (mLocalSettings.getSecondNumber() != null) {
            mSecondNumberEditText.setText(mLocalSettings.getSecondNumber());
        } else {
            mSecondNumberEditText.setText("");
        }

        if (mLocalSettings.getCounterNumber() != null) {
            mTimerDelayEditText.setText(mLocalSettings.getCounterNumber());
            mStartCounterButton.setText(mLocalSettings.getCounterNumber());
        } else {
            mTimerDelayEditText.setText("0");
            mStartCounterButton.setText("0");
        }

        if (mLocalSettings.getPreviousResult() != null) {
            mSumResultTextView.setText(String.format(getActivity().getString(R.string.PREVIOUS_SUM_RESULT), mLocalSettings.getPreviousResult()));
        } else {
            mSumResultTextView.setText(String.format(getActivity().getString(R.string.PREVIOUS_SUM_RESULT), UNKNOWN_PREVIOUS_RESULT));
        }

        mStartCounterButton.setEnabled(true);

        mFirstNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLocalSettings.setFirstNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mSecondNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLocalSettings.setSecondNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mTimerDelayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLocalSettings.setCounterNumber(s.toString());
                mStartCounterButton.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mStartCounterButton.setOnClickListener(this);

        mReceiver = new SumReceiver();
        IntentFilter filter = new IntentFilter();

        filter.addAction(SumService.FINISHED_COUNTING);
        filter.addAction(SumService.UPDATE_COUNTER_ACTION);

        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mLocalSettings.save();

        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startCounterButton) {
            if (mLocalSettings.isCorrect()) {
                mLocalSettings.setCouting(true);
                mStartCounterButton.setEnabled(false);
                Intent intent = new Intent(getActivity(), SumService.class);
                intent.putExtra(SumService.COUNTER_NUMBER, Integer.parseInt(mLocalSettings.getCounterNumber()));
                intent.putExtra(SumService.FIRST_NUMBER, Integer.parseInt(mLocalSettings.getFirstNumber()));
                intent.putExtra(SumService.SECOND_NUMBER, Integer.parseInt(mLocalSettings.getSecondNumber()));
                getActivity().startService(intent);
            } else {
                Toast.makeText(getActivity(), R.string.CHECK_YOUR_NUMBERS, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SumReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SumService.UPDATE_COUNTER_ACTION.equals(intent.getAction())) {
                mLocalSettings.setCouting(true);
                mStartCounterButton.setEnabled(false);
                mStartCounterButton.setText(String.valueOf(intent.getIntExtra(SumService.COUNTER_NUMBER, 0)));
            } else if (SumService.FINISHED_COUNTING.equals(intent.getAction())) {
                int sumResult = intent.getIntExtra(SumService.RESULT_NUMBER, -1);

                mLocalSettings.setCouting(false);
                mStartCounterButton.setEnabled(true);
                mStartCounterButton.setText(mLocalSettings.getCounterNumber());

                if (sumResult != -1) {
                    mLocalSettings.setPreviousResult(String.valueOf(sumResult));
                    mSumResultTextView.setText(String.format(getActivity().getString(R.string.SUM_RESULT), mLocalSettings.getPreviousResult()));
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.WRONG_RESULT, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }
}
