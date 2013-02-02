package com.flyingh.phonenumberquery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private EditText numberText;
	private TextView addressTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		numberText = (EditText) findViewById(R.id.phone_number);
		addressTextView = (TextView) findViewById(R.id.address);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
		}
	}

	public void query(View view) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx").openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			byte[] bytes = new String(readXmlFile()).replaceAll("number", numberText.getText().toString()).getBytes();
			conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
			conn.getOutputStream().write(bytes);
			if (conn.getResponseCode() == HttpStatus.SC_OK) {
				Log.i(TAG, "success");
				addressTextView.setText(parseSOAP(conn.getInputStream()));
			} else {
				Log.i(TAG, "failure:" + conn.getResponseCode());
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

	}

	private String parseSOAP(InputStream inputStream) throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "utf-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("getMobileCodeInfoResult".equals(parser.getName())) {
					return parser.nextText();
				}
				break;

			default:
				break;
			}
			eventType = parser.next();
		}

		return null;
	}

	private byte[] readXmlFile() {
		try {
			InputStream is = getClass().getResourceAsStream("/soap.xml");
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = is.read(buf)) != -1) {
				os.write(buf, 0, len);
			}
			return os.toByteArray();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return new byte[0];
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
