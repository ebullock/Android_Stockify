package com.example.lab6;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity implements OnClickListener{

	Button bStockify;
	EditText etStockSymbol;
	TextView tvPrice;
	TextView tvTitle;
	String urlString;
	URL url;
	JSONObject JSONObject;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bStockify = (Button) findViewById(R.id.bStockify);
        bStockify.setOnClickListener(this);
        etStockSymbol = (EditText) findViewById(R.id.etStockSymbol);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
    }

	@Override
	public void onClick(View v) {
		Thread httpThread = new Thread(){
			public void run(){
	    		try{
	    			url = new URL( "http://finance.yahoo.com/webservice/v1/symbols/" 
	    					+ etStockSymbol.getText().toString()
	    					+ "/quote?format=json");
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					//read JSON from URL and place it into a string
					String jsonString = "",tempJson = "";
					tempJson = in.readLine();
					while(tempJson != null){
						jsonString = jsonString + tempJson;
						tempJson = in.readLine();	
					}
					//get a message from the pool, put the JSON string into it, and send it back to the UI thread
					Message msg = Message.obtain();
					msg.obj = jsonString;
					msg.setTarget(handler);
					msg.sendToTarget();
	    		}catch(Exception e)
	    		{
	    			Log.e("CATCH", e.toString());
	    			e.printStackTrace();
	    		}
	    		finally{}
	    	}
		};
		//start the thread
		httpThread.start();
	}
	
	Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
        	String JSON = (String) msg.obj;
        	Log.i("JSON",JSON);
        	try{
        		JSONObject = new JSONObject(JSON);
        		JSONObject = (JSONObject) JSONObject.get("list");
        		JSONArray jArray = new JSONArray(JSONObject.get("resources").toString());
        		Log.i("JSON ARRAY", jArray.getString(0));
        		JSONObject = (JSONObject) jArray.get(0);
        		JSONObject = (JSONObject) JSONObject.get("resource");
        		JSONObject = (JSONObject) JSONObject.get("fields");
        		Log.i("JSON OBJECT", JSONObject.getString("name"));
        		
        		tvTitle.setText(JSONObject.getString("name"));
    			tvPrice.setText(JSONObject.getString("price"));
        	}catch(JSONException e){
        		e.printStackTrace();
        	}
        }
    };

}
