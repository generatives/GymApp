package com.example.gymapp;

import java.util.ArrayList;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView timer;
	Button startStopButton, resetButton;

	private Handler customHandler = new Handler();
	long lastTime = 0L;
	long totalTime = 0L;
	
	boolean timerRunning = false;
	boolean timerReset = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		timer = (TextView) findViewById(R.id.Timer);
		startStopButton = (Button) findViewById(R.id.StartStopButton);
		resetButton = (Button) findViewById(R.id.Reset);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private String buildPlateReport(ArrayList<Double> weights) {
		if (weights.size() == 0) {
			return "Use the bar.";
		}

		String message = "Use ";

		for (int i = 0, N = weights.size(); i < N; i++) {
			message = message + Double.toString(weights.get(i));
			if (i != weights.size() - 1) {
				if (i == weights.size() - 2) {
					message = message + ", and ";
				} else {
					message = message + ", ";
				}
			}
		}
		message = message + ".";
		return message;
	}

	private String findPlates(double weight) {
		double[] possibleWeights = { 45, 35, 25, 10, 5, 2.5 };
		ArrayList<Double> weights = new ArrayList<Double>();

		double remainingWeightPerSide = (weight - 45) / 2;

		while (remainingWeightPerSide >= possibleWeights[possibleWeights.length - 1]) {
			for (int i = 0, N = possibleWeights.length; i < N; i++) {
				if (remainingWeightPerSide >= possibleWeights[i]) {
					remainingWeightPerSide = remainingWeightPerSide
							- possibleWeights[i];
					weights.add(possibleWeights[i]);
					break;
				}
			}
		}

		return buildPlateReport(weights);

	}

	public void calculateOutput(View view) {
		EditText weightInput = (EditText) findViewById(R.id.Weight);
		CheckBox mode = (CheckBox) findViewById(R.id.Mode);
		TextView output = (TextView) findViewById(R.id.Output);

		double weight = Double.parseDouble(weightInput.getText().toString());

		if (weight < 45) {
			output.setText("Impossible!");
			return;
		}

		if (mode.isChecked()) {
			output.setText(findPlates(weight));
		} else {
			double[] weightForSets = { weight * 0.4, weight * 0.6,
					weight * 0.7, weight * 0.8, weight * 0.9, weight };
			String[] repsForSets = { "8", "5", "3", "1", "1", "1" };
			String[] breakForSets = { "2 mins", "2 mins", "3 mins", "3 mins",
					"5 mins", "5-10 mins" };

			String outputMessage = "";

			for (int i = 0, N = weightForSets.length; i < N; i++) {
				outputMessage = outputMessage + "-"
						+ findPlates(weightForSets[i]) + "  Then do "
						+ repsForSets[i] + " reps and rest for "
						+ breakForSets[i] + "\n";
			}

			output.setText(outputMessage);
		}
	}

	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			if(timerReset) {
				lastTime = SystemClock.uptimeMillis();
				timerReset = false;
			}
			if(timerRunning) {
				totalTime += SystemClock.uptimeMillis() - lastTime;
			}
			lastTime = SystemClock.uptimeMillis();
			int secs = (int) (totalTime / 1000);
			int mins = secs / 60;
			secs = secs % 60;
			timer.setText("" + mins + ":" + String.format("%02d", secs));
			customHandler.postDelayed(this, 0);
		}

	};
	
	

	public void toggleTimer(View view) {
		if (!timerRunning) {
			timerRunning = true;
			customHandler.postDelayed(updateTimerThread, 0);
			
		} else {
			timerRunning = false;
		}

	}
	
	public void resetTimer(View view) {
		totalTime = 0L;
		timerReset = true;
	}
}
