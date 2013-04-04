package com.yangli907.newandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	private String connUrl = "http://www.simsimi.com/talk.htm";
	private String baseUrl = "http://www.simsimi.com/func/req";
	private EditText inputField = null;
	private EditText outputField = null;
	private String inputText = "";
	private String response = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		inputField = (EditText)findViewById(R.id.inputField);
		outputField = (EditText)findViewById(R.id.outputField);
		
	}
	
	public void onSubmit(View v){
		inputText = inputField.getText().toString();
		response = sendRequest(inputText);
		outputField.setText(response);
	}
	
	public void cleanInput(View v){
		inputField.setText("");
		outputField.setText("");
	}
	
	
	private String sendRequest(String inputText){
		List<String> cookies = getConnCookie(connUrl);
		String msg = "天气不错 right?";
		String locale = "zh";
		try {
			msg = URLEncoder.encode(inputText,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String request = "?msg="+msg+"&lc="+locale;
		
		return getResponse(cookies.get(0),baseUrl,request);
	}
	private String getResponse(String cookie, String baseUrl, String request){
		StringBuilder builder = new StringBuilder();
		String result = "";
		try {
			URL url = new URL(baseUrl+request);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty("X-Forwarded-For", "10.2.0.124");
			con.addRequestProperty("Cookie", cookie);
			con.addRequestProperty("Referer", "http://www.simsimi.com/talk.htm");
			BufferedReader reader = readStream(con.getInputStream());
			
			try {
				String line = "";
				while ((line = reader.readLine()) != null) {
					builder.append(line).append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject req;
		try {
			req = new JSONObject(builder.toString());
			result = req.getString("response");
			return result;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
		
	}
	private List<String> getConnCookie(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			List<String> cookies = con.getHeaderFields().get("Set-Cookie");
			return cookies;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private BufferedReader readStream(InputStream in) {
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(in));
		return reader;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
