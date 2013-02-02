package com.flyingh.phonenumberquery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.os.Build;
import android.os.StrictMode;
import android.test.AndroidTestCase;
import android.util.Log;

public class XMLTest extends AndroidTestCase {
	private static final String TAG = "XMLTest";

	public void test() throws IOException {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
		}
		InputStream is = getClass().getResourceAsStream("/persons.xml");
		byte[] bytes = getBytes(is);
		HttpURLConnection conn = (HttpURLConnection) new URL("http://10.1.79.23:8080/News/XmlServlet").openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(bytes);
		Log.i(TAG, new String(bytes));
		if (conn.getResponseCode() == HttpStatus.SC_OK) {
			Log.i(TAG, "success");
		} else {
			Log.i(TAG, "failure:" + conn.getResponseCode());
		}
	}

	private byte[] getBytes(InputStream is) throws IOException {
		byte[] buf = new byte[1024];
		int len = -1;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		while ((len = is.read(buf)) != -1) {
			os.write(buf, 0, len);
		}
		return os.toByteArray();
	}
}
