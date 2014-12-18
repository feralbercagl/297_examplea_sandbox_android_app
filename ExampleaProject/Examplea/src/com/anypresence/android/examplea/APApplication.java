package com.anypresence.android.examplea;

import android.app.Application;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import android.view.*;
import android.app.Activity;
import java.lang.ref.WeakReference;

import com.anypresence.sdk.callbacks.APAuthCallback;
import com.anypresence.sdk.*;
import com.anypresence.sdk.acl.*;
import com.anypresence.sdk.config.Config;
import com.anypresence.sdk.http.*;
import com.anypresence.*;
import com.anypresence.rails_droid.RemoteRailsConfig;
import com.anypresence.rails_droid.RemoteRequestException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.*;

import com.anypresence.library.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import com.anypresence.rails_droid.http.BaseRestClient;
import com.anypresence.rails_droid.http.RestClientFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;

public class APApplication extends Application {
	private static final String TAG = APApplication.class.getName();

	public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
	public static final String SIMPLE_TIME_FORMAT = "HH:mm";
	public static final String SIMPLE_DATE_AND_TIME_FORMAT = "yyyy-MM-dd HH:mm";

	private static Context context;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(getClass().getName(), "Creating application...");

		// Sets up the SDK, i.e. api endpoints, etc.
		APSetup.setup(this);

		// Change the base URL here if you moved the server
		APSetup.setBaseUrl("https://obscure-harbor-6682.herokuapp.com");
		// You can specify a different version endpoint here:
		// Config.getInstance().setAppUrl("http://fake.local:3000/api/v1");
		// You can specify a different auth url endpoint here:
		// Config.getInstance().setAuthUrl("http://fake.local:3000/auth/password/callback");

		Config.getInstance().setStrictQueryFieldCheck(false);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			FroyoCustomCookie customCookieManager = new FroyoCustomCookie(
					android.webkit.CookieManager.getInstance());
			RemoteRailsConfig.getInstance().setCustomCookieManager(
					customCookieManager);
		}

		if (!android.os.Build.MANUFACTURER.equals("unknown")
				&& !APSDKSetup.TEST_MODE) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
		}

		APApplication.context = this.getApplicationContext();

		pinCert();
	}

	// Pins the cert to the server.cert file found in /assets. If not found, this does nothing.
	public void pinCert() {
		RestClientFactory
				.registerJSONRestClientImplementation(new BaseRestClient() {
					@Override
					protected HttpURLConnection getConnection(URI uri)
							throws IOException {
						HttpURLConnection c = super.getConnection(uri);

						try {
							// Cast the connection
							HttpsURLConnection connection = (HttpsURLConnection) c;

							// Load CAs from an InputStream
							CertificateFactory cf = CertificateFactory
									.getInstance("X.509");
							InputStream caInput = new BufferedInputStream(
									getAssets().open("server.cert"));
							Certificate ca;
							try {
								ca = cf.generateCertificate(caInput);
								System.out.println("ca="
										+ ((X509Certificate) ca).getSubjectDN());
							} finally {
								caInput.close();
							}

							// Create a KeyStore containing our trusted CAs
							String keyStoreType = KeyStore.getDefaultType();
							KeyStore keyStore = KeyStore
									.getInstance(keyStoreType);
							keyStore.load(null, null);
							keyStore.setCertificateEntry("ca", ca);

							// Create a TrustManager that trusts the CAs in our KeyStore
							String tmfAlgorithm = TrustManagerFactory
									.getDefaultAlgorithm();
							TrustManagerFactory tmf = TrustManagerFactory
									.getInstance(tmfAlgorithm);
							tmf.init(keyStore);

							// Create an SSLContext that uses our TrustManager
							SSLContext context = SSLContext.getInstance("TLS");
							context.init(null, tmf.getTrustManagers(), null);

							// Tell the URLConnection to use a SocketFactory from our SSLContext
							connection.setSSLSocketFactory(context
									.getSocketFactory());
						} catch (CertificateException e) {
							e.printStackTrace();
						} catch (KeyStoreException e) {
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (KeyManagementException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// Do nothing. No cert was found.
						} catch (ClassCastException e) {
							// Not using an https url
						}

						return c;
					}
				});
	}

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm:ss");
	// List of views that requires authentication before it can be made visible
	private static List<WeakReference<IAuthorizable>> views = new ArrayList<WeakReference<IAuthorizable>>();
	private static List<WeakReference<IAuthorizable>> activities = new ArrayList<WeakReference<IAuthorizable>>();

	private static void toggleVisibility(
			List<WeakReference<IAuthorizable>> views) {
		if (Auth.isLoggedIn()) {
			showAuthenticatedView(views);
		} else {
			showUnauthenticatedView(views);
		}
	}

	private static void showAuthenticatedView(
			List<WeakReference<IAuthorizable>> views) {
		for (WeakReference<IAuthorizable> v : views) {
			if (v.get() != null && v.get().isAuthorized()) {
				// Change the view if necessary
				v.get().setAuthenticatedView();
			}
		}
	}

	private static void showUnauthenticatedView(
			List<WeakReference<IAuthorizable>> views) {
		for (WeakReference<IAuthorizable> v : views) {
			if (v.get() != null)
				v.get().setUnauthenticatedView();
		}
	}

	public static Context getAppContext() {
		return APApplication.context;
	}

	public static void addActivityRequiringAuth(IAuthorizable v) {
		synchronized (activities) {
			activities.add(new WeakReference<IAuthorizable>(v));
		}
	}

	public static void removeActivityRequiringAuth(IAuthorizable v) {
		synchronized (activities) {
			for (WeakReference<IAuthorizable> view : views) {
				if (view.get() != null && view.get() == v) {
					views.remove(view);
					break;
				}
			}
		}
	}

	public static void addViewRequiringAuth(IAuthorizable v) {
		synchronized (views) {
			views.add(new WeakReference<IAuthorizable>(v));
		}
	}

	public static void toggleVisibilityOfActivities() {
		toggleVisibility(activities);
	}

	public static void toggleVisibilityOfViews() {
		toggleVisibility(views);
	}

	/**
	 * Checks to see if authorization should be granted.
	 *
	 * @param roles Roles to to check against
	 * @return <tt>true</tt> if authorized, otherwise, return <tt>false</tt>
	 */
	public static Boolean isAuthorized(List<String> roles) {
		if (Auth.isLoggedIn()) {
			if (roles == null || roles.size() == 0) {
				return true;
			} else if (AuthManagerFactory.getInstance()
					.getAuthenticatableObject() != null) {
				IAuthenticatable user = AuthManagerFactory.getInstance()
						.getAuthenticatableObject();

				if (user.getRoles() == null)
					return true;

				Set<String> roleNames = user.getRoles().keySet();
				for (String role : roles) {
					if (roleNames.contains(role)) {
						Log.i(TAG, "Found matching role: " + role);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void logOff(final FragmentActivity activity) {
		Log.d(TAG, "Logging off...");
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity);
		alertDialogBuilder.setMessage("Sign out?").setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Auth.logout(activity, activity.getPackageName(),
								new Auth.OnLogoutListener() {
									@Override
									public void onLogoutSuccess() {
										APApplication.toggleVisibilityOfViews();
										APApplication
												.toggleVisibilityOfActivities();
									}

									@Override
									public void onLogoutFailed(Exception e) {
										e.printStackTrace();
									}
								});
					}
				});
		alertDialogBuilder.show();
	}

	public static Boolean isNetworkConnected(FragmentActivity activity) {
		ConnectivityManager cm = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		} else
			return true;
	}

	/**
	 * Gets the screen size
	 *
	 * @param context the context
	 * @return integer array where first element is width and second element is height
	 */
	public static int[] getScreenMeasurement(Context context) {
		int[] measurements = new int[2];

		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		measurements[0] = outMetrics.widthPixels;
		measurements[1] = outMetrics.heightPixels;

		return measurements;
	}

	public static void handleError(int statusCode) {
		if (statusCode == 401) {
			Log.i(TAG, "Not authenticated. Resetting views");
			try {
				AuthManagerFactory.getInstance().deauthenticateUser(null);
			} catch (RemoteRequestException e) {
				Log.e(TAG, "Unable to log out.", e);
			}
			APApplication.showUnauthenticatedView(activities);
			APApplication.showUnauthenticatedView(views);
		} else if (statusCode == 403) {
			Toast.makeText(APApplication.getAppContext(), "Not authorized!",
					Toast.LENGTH_SHORT).show();
		}
	}
}
