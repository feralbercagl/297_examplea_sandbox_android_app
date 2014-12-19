package com.anypresence.android.examplea;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class IconTextView extends TextView {
	private final static String TAG = "IconTextView";

	private static Typeface typeface = null;

	private static final int default_icon_id = APApplication
			.getAppContext()
			.getResources()
			.getIdentifier("icon_question", "string",
					APApplication.getAppContext().getPackageName());

	public IconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		if (typeface == null) {
			typeface = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/fontawesome_webfont.ttf");
		}
		setTypeface(typeface);
	}

	public static int getIcon(String icon) {
		int id = APApplication
				.getAppContext()
				.getResources()
				.getIdentifier(icon, "string",
						APApplication.getAppContext().getPackageName());

		return (id == 0) ? default_icon_id : id;
	}

}
