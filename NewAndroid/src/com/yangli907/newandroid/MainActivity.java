package com.yangli907.newandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {
	private String connUrl = "http://www.simsimi.com/talk.htm";
	private String baseUrl = "http://www.simsimi.com/func/req";
	private EditText inputField = null;
	private EditText outputField = null;
	private ProgressBar progressBar = null;
	private TextToSpeech tts = null;
	private TextView debugField = null;
	private RadioGroup langOpt = null;
	private ListView lv = null;
	private Typeface type = null;
			
	private String inputText = "";
	private String response = "";
	private String language = "";
	private int lastVisiblePosition = 0;
	
	private static boolean debugMode = true;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private com.yangli907.newandroid.MessageArrayAdapter adapter;
	
	private Properties prop = new Properties();
	private String[] dontKnow = {};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		langOpt = (RadioGroup)findViewById(R.id.langOpt);
		inputField = (EditText)findViewById(R.id.inputField);
	    type = Typeface.createFromAsset(getAssets(),"fonts/KOMTXKBI.ttf");  
		inputField.setTypeface(type);
		outputField = (EditText)findViewById(R.id.outputField);
		outputField.setVisibility(View.INVISIBLE); //temp
		langOpt.setVisibility(View.INVISIBLE); //temp
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.INVISIBLE);
		tts = new TextToSpeech(this, null);
		adapter = new MessageArrayAdapter(getApplicationContext(), R.layout.listitem);
		lv = (ListView)findViewById(R.id.listView1);
		lv.setAdapter(adapter);
		try {
			dontKnow=loadAppProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String[] loadAppProperties() throws IOException{
		Resources resources = this.getResources();
		AssetManager assetManager = resources.getAssets();
	    InputStream inputStream = assetManager.open("dontknow.properties");
	    Reader reader = new InputStreamReader(inputStream, "UTF-8");
		prop.load(reader);
		dontKnow = prop.getProperty("dontKnowChn").split(",");
		return dontKnow;
	}
	
	Object lock = new Object(); //For synchronization for UI/background thread
	public void onSubmit(View v){		
		Thread thread = new SendReqThread();
		thread.start();
	}
	
	public void cleanInput(View v){
		inputField.setText("");
		outputField.setText("");
	}
	
	public class SendReqThread extends Thread{
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					inputText = inputField.getText().toString();
					adapter.add(new Response(true,inputText));
					progressBar.setVisibility(View.VISIBLE);
					Log.i("*****Text from UI******",inputText);
					synchronized(lock){lock.notify();}
				}
			});
			try{
				synchronized(lock){lock.wait();}
				response = sendRequest(inputText);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					outputField.setText(response);
					progressBar.setVisibility(View.INVISIBLE);
					}
			});
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.add(new Response(false, response));
					lastVisiblePosition = lv.getLastVisiblePosition();
					//lv.smoothScrollToPosition(lastVisiblePosition);
					lv.smoothScrollToPosition(lv.getCount()-1);
					inputField.setText("");
					}
			});
			
		}
	}
	
	private String sendRequest(String inputText){
		List<String> cookies = getConnCookie(connUrl);
		SharedPreferences prefs = PreferenceManager
			    .getDefaultSharedPreferences(MainActivity.this);
		String locale = prefs.getString("listpref", "en");
		String msg = "";
		Log.i("Language", langOpt.getCheckedRadioButtonId()==R.id.eng?"ENGLISH":"CHINESE");
		//String locale = "zh";
		//String locale = langOpt.getCheckedRadioButtonId()==R.id.eng?"en":"zh";
		try {
			Log.i("*****inputText*****", inputText);
			msg = URLEncoder.encode(inputText,"UTF-8");
		} catch (UnsupportedEncodingException e) {
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
			if(debugMode==true){
				Log.i("*****Request******", url.toString());
			}
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			String host = "10.2.0."+(int)(Math.random()*1000%255);
			con.addRequestProperty("X-Forwarded-For", host);
			Log.i("Sender IP", host);
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
			} 
			finally {
					reader.close();
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
			result = dontKnow[(int) (Math.random()*100%dontKnow.length)];
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

	public void onSpeak(View v){
		//tts.speak("测试小黄鸡", TextToSpeech.QUEUE_FLUSH, null);
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		 
		  // Specify the calling package to identify your application
		  intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
		    .getPackage().getName());
		 
		  // Display an hint to the user about what he should say.
		 
		  // Given an hint to the recognizer about what the user is going to say
		  //There are two form of language model available
		  //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
		  //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		 
		  int noOfMatches = 5;
		  // Specify how many results you want to receive. The results will be
		  // sorted where the first result is the one with higher confidence.
		  intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
		  //Start the Voice recognizer activity for the result.
		  startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)
	 
	   //If Voice recognition is successful then it returns RESULT_OK
	   if(resultCode == RESULT_OK) {
	 
	    ArrayList<String> textMatchList = data
	    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	    inputField.setText(textMatchList.get(0).toString());
	   }
	  
	  super.onActivityResult(requestCode, resultCode, data);
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case R.id.action_settings:
		    startActivity(new Intent(this, PrefsActivity.class));
		    return true;
		    default:
		    return super.onOptionsItemSelected(item);
		}
	}
}
