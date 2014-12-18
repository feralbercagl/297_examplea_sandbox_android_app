package com.anypresence.android.examplea;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Date;

public abstract class DatePickerFragment extends DialogFragment
		implements
			DatePickerDialog.OnDateSetListener {
	public final Calendar cal = Calendar.getInstance();

	private int year;
	private int month;
	private int day;

	public DatePickerFragment(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public DatePickerFragment(Date date) {
		if (date == null)
			return;

		cal.setTime(date);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
	}

	public DatePickerFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		if (year <= 0)
			year = cal.get(Calendar.YEAR);
		if (month < 0)
			month = cal.get(Calendar.MONTH);
		if (day <= 0)
			day = cal.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of TimePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public abstract void onDateSet(DatePicker arg0, int year, int month, int day);

}
