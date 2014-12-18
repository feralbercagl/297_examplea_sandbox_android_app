package com.anypresence.android.examplea;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import java.util.Date;

public abstract class TimePickerFragment extends DialogFragment
		implements
			TimePickerDialog.OnTimeSetListener {
	public final Calendar cal = Calendar.getInstance();

	private int hour;
	private int minute;

	public TimePickerFragment(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}

	public TimePickerFragment(Date date) {
		if (date == null)
			return;

		this.cal.setTime(date);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
	}

	public TimePickerFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		if (hour < 0)
			hour = cal.get(Calendar.HOUR_OF_DAY);
		if (minute < 0)
			minute = cal.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public abstract void onTimeSet(TimePicker view, int hourOfDay, int minute);
}
