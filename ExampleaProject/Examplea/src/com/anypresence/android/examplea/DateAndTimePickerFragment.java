package com.anypresence.android.examplea;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import java.util.Date;
import android.widget.*;
import android.util.Log;

public abstract class DateAndTimePickerFragment extends DialogFragment {
	private final static String TAG = DateAndTimePickerFragment.class.getName();
	private Date date;

	private final Calendar cal = Calendar.getInstance();

	public DateAndTimePickerFragment(Date date) {
		if (date != null) {
			// Create a copy
			this.date = new Date(date.getTime());
		} else {
			this.date = new Date();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater factory = LayoutInflater.from(getActivity());
		final View v = factory.inflate(R.layout.date_and_time_fragment, null);
		Button dateButton = (Button) v.findViewById(R.id.change_date);
		dateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new DatePickerFragment(date) {
					@Override
					public void onDateSet(DatePicker arg0, int arg1, int arg2,
							int arg3) {
						cal.set(Calendar.YEAR, arg1);
						cal.set(Calendar.MONTH, arg2);
						cal.set(Calendar.DAY_OF_MONTH, arg3);

						// Update the date
						Log.d(TAG, "Updated date to: " + date.toString());
						date = cal.getTime();
					}
				}.show(getFragmentManager(), "datePicker");
			}
		});

		Button timeButton = (Button) v.findViewById(R.id.change_time);
		timeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new TimePickerFragment(date) {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
						cal.set(Calendar.MINUTE, minute);

						Log.d(TAG, "Updated time to: " + date.toString());
						date = cal.getTime();
					}
				}.show(getFragmentManager(), "timePicker");
			}
		});

		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						onDateAndTimeSet(new Date(date.getTime()));
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 
							}
						}).create();
	}

	public abstract void onDateAndTimeSet(Date newDate);

}
