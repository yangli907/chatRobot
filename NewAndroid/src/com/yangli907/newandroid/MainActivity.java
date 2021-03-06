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
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	private String connUrl = "http://www.simsimi.com/talk.htm";
	private String baseUrl = "http://www.simsimi.com/func/req";
	private EditText inputField = null;
	private EditText outputField = null;
	private ProgressBar progressBar = null;
	private String inputText = "";
	private String response = "";
	private String[] dontKnow = {    
			"我不明白你的意思。",
		    "我不太懂你在说什么。",
		    "我们能换一个话题吗？",
		    "你高估我了，我没有你想的那么聪明。",
		    "我不懂你在说什么。",
		    "我对你的问题不太感兴趣。",
		    "我还没想好怎么回答你的问题呢。",
		    "我应该怎么回答你这个奇怪的问题呢？",
		    "我不太懂你的话。",
		    "你能跟我解释一下吗？",
		    "你的问题让我很纠结。",
		    "你的问题让我真的很纠结。",
		    "你的问题难倒我了。",
		    "我没听说过那个东西。",
		    "天天被你们拉去聊天，我都很少了解时事了。",
		    "不要问我那么刁钻古怪的问题啦！",
		    "我对那个不感兴趣，跟我说说你最喜欢的明星吧。",
		    "我不关心那个，跟我说说你最喜欢的明星吧。",
		    "我对那个不感兴趣，跟我说说最近的新闻吧。",
		    "我不关心那个，跟我说说最近的新闻吧。"
		    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		inputField = (EditText)findViewById(R.id.inputField);
		outputField = (EditText)findViewById(R.id.outputField);
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.INVISIBLE);
		
	}
	
	public void onSubmit(View v){
		
		Thread thread = new Thread() {
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						progressBar.setVisibility(View.VISIBLE);
						inputText = inputField.getText().toString();
					}
				});
				try{
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
			}
		};
		
		thread.start();
		

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
