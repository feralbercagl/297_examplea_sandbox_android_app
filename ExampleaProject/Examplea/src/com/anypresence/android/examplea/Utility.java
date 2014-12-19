package com.anypresence.android.examplea;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.*;
import android.content.res.Resources;

import com.anypresence.sdk.*;
import com.anypresence.sdk.acl.*;

import android.text.format.DateFormat;
import android.util.Log;
import android.content.Context;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.*;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.anypresence.rails_droid.*;

public class Utility {
	private static final String TAG = Utility.class.getName();

	public static Class getTypeForClass(Class clazz, String field)
			throws SecurityException, NoSuchFieldException {
		Field f = clazz.getField(field);
		return f.getType();
	}

	/**
	 * Interpolates string with attributes on <tt>object</tt>.
	 *
	 * @param object The object to interpolate on
	 * @param text The text to be interpolated where the interpolated variable is of the form <tt>{{value}}</tt>
	 * @param onlyKeepLastVariable if <tt>true</tt>, just keep the last variable; otherwise, resolve all.
	 * @return interpolated text
	 */
	public static String interpolateText(Object object, String text,
			Boolean onlyKeepLastVariable) {
		if (text == null)
			return "";

		Pattern p = Pattern.compile("\\{\\{((?!user).*?)\\}\\}");
		String interpolatedText = text;
		Matcher m = p.matcher(text);

		while (m.find()) {
			interpolatedText = peformInterpolation(object, m, interpolatedText,
					onlyKeepLastVariable);
		}

		p = Pattern.compile("\\{\\{(user\\..*?)\\}\\}");
		m = p.matcher(interpolatedText);

		while (m.find()) {
			interpolatedText = peformInterpolation(AuthManagerFactory
					.getInstance().getAuthenticatableObject(), m,
					interpolatedText, true);
		}

		return interpolatedText;
	}

	/**
	 * Interpolates string with attributes on <tt>object</tt>.
	 *
	 * @param object The object to interpolate on
	 * @param text The text to be interpolated where the interpolated variable is of the form <tt>{{value}}</tt>
	 * @return interpolated text
	 */
	public static String interpolateText(Object object, String text) {
		return interpolateText(object, text, false);
	}

	/**
	 * Gets text or object by trying to invoke getter method on <tt>object</tt>.
	 *
	 * Example:
	 * if fieldName := "image", then the method to invoke on <tt>object</tt> will be getImage().
	 *
	 * @param object object to invoke method on
	 * @param fieldName getter method to invoke on object via reflection
	 * @return result of invoking getter method
	 */
	private static Object getTextOrObject(Object object, String fieldName) {
		Object interpolatedObject = null;
		try {
			if (object != null) {
				String methodName = Inflector.camelize(fieldName, true);
				if (methodName.equals("Id"))
					methodName = "ObjectId";
				Method method = object.getClass().getMethod("get" + methodName);
				Object result = method.invoke(object);
				if (result != null) {
					interpolatedObject = result;
				}
			}
		} catch (SecurityException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (InvocationTargetException e) {
			Log.e(TAG, e.getMessage(), e);
		}

		if (interpolatedObject != null) {
			if (interpolatedObject.getClass() == java.util.Date.class) {
				interpolatedObject = DateFormat.format("MMM dd, yyyy",
						(java.util.Date) interpolatedObject);
			}
		}

		return interpolatedObject;
	}

	/**
	 * Perform the interpolation.
	 */
	private static String peformInterpolation(Object object, Matcher matcher,
			String interpolatedText, Boolean onlyKeepLastVariable) {
		String interpolatedVariable = matcher.group(1);

		String[] splittedText = interpolatedVariable.split("\\.");

		Method method = null;
		String methodName = null;

		Object currentObject = object;
		if (onlyKeepLastVariable) {
			currentObject = getTextOrObject(currentObject,
					splittedText[splittedText.length - 1]);
		} else {
			for (String fieldName : splittedText) {
				currentObject = getTextOrObject(currentObject, fieldName);
			}
		}

		if (currentObject != null) {
			interpolatedText = interpolatedText.replace(matcher.group(0),
					currentObject.toString());
		} else {
			interpolatedText = interpolatedText.replace(matcher.group(0), "");
		}

		return interpolatedText;
	}

	/**
	 * Computes offset for query.
	 */
	public static Integer computeOffset(Integer currentPage, Integer limit) {
		if (currentPage == null || limit == null)
			return null;

		return currentPage * limit;
	}

	/**
	 * Encodes string into application/x-www-form-urlencoded format.
	 */
	public static String cleanQuery(String query) {
		String cleanedQuery = query;
		try {
			cleanedQuery = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return cleanedQuery;
	}

	/**
	 * Bitmap decode method borrowed from Android docs.
	 */
	public static Bitmap decodeSampledBitmapFromResource(Context context,
			Resources res, Integer resId, Integer reqWidth, Integer reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(context, options,
				reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static int calculateInSampleSize(Context context,
			BitmapFactory.Options options, Integer reqWidth, Integer reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		int[] measurements = APApplication.getScreenMeasurement(context);

		if ((reqWidth != null && reqHeight != null)
				&& (height > reqHeight || width > reqWidth)) {
			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		} else if (height > measurements[1] || width > measurements[0]) {
			Log.d(TAG, "Image has dimension larger than screen");
			final int heightRatio = (int) Math.ceil((float) height
					/ (float) (measurements[1] / 2));
			final int widthRatio = (int) Math.ceil((float) width
					/ (float) (measurements[0] / 2));

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

}
