package com.example.clickerviewpager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Text extends Fragment{
	private static final int SERVER_PORT=8080;
	private static final String FLAG="question";
	private EditText editText;
	private ListView myListView;
	private ArrayList<String> arrayList;
	private ArrayAdapter<String> adapter;
	private Button sentB;
	private Handler handler;
	private String questionS,result="",ID="",IP="";
	private Socket client=null;
	private PrintWriter out;
	private BufferedReader in;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = this.getActivity().getSharedPreferences("info",0);
        ID = preferences.getString("ID",""); 
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text, container, false);
       
        setupUI(view.findViewById(R.id.textfragment));
        
        editText=(EditText)view.findViewById(R.id.editText); 
       
        editText.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float starty=0,offset;
	    		switch(event.getAction()){
	    		case MotionEvent.ACTION_DOWN:
	    			starty=event.getY();
	    			
	    			break;
	    		case MotionEvent.ACTION_MOVE:
	    			offset=event.getX()-starty;
	    			if(Math.abs(offset)>5){
	    				editText.setText("");
	    			}
	    		}
				return false;
			}
        	
        });

		myListView=(ListView)view.findViewById(R.id.listview);
		arrayList=new ArrayList<String>();
		adapter=new ArrayAdapter<String>(this.getActivity(),R.layout.listview_text,R.id.listViewContentText,arrayList);
		myListView.setAdapter(adapter);
		
		sentB=(Button)view.findViewById(R.id.sentB1);
		sentB.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				questionS=editText.getText().toString();//若不输入任何，则questionS是空字符串
				if ("".equals(questionS)) {
					Toast.makeText(getActivity(), "请输入问题！",
							Toast.LENGTH_SHORT).show();
				}
				else{
					// 创建一个新线程，用于从网络上获取文件
					new Thread(new Runnable() {
						public void run() {
							sendViaSocket();
							Message m = handler.obtainMessage(); // 获取一个Message
							handler.sendMessage(m); // 发送消息
						}
					}).start();
				}
			}
		});
		
		handler=new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (result.equals("TRANSFER_SUCCESS")) {
					arrayList.add(questionS);
					editText.setText("");
					adapter.notifyDataSetChanged();
				}
				return false;
			}
		});
		
        return view;
	}
	
	private void sendViaSocket(){
		SharedPreferences preferences=this.getActivity().getSharedPreferences("info",0);  
		IP=preferences.getString("ip","192.168.1.100");
		String outputString="";
		try {
			client=new Socket (IP,SERVER_PORT);
			//out=new PrintWriter(client.getOutputStream(),true);
			out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8")),true);
		    in =new BufferedReader(new InputStreamReader(client.getInputStream()));
		    outputString=FLAG+":"+ID+":"+questionS;
		    //out.println(outputString.getBytes("UTF-8"));
		    out.println(outputString);
			result=in.readLine();
			//Log.i("logi","inputstream is:"+result);
			out.close();
			in.close();
			client.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void hideSoftKeyboard() {
	    InputMethodManager inputMethodManager = (InputMethodManager)this.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 0);
	}
	
	public void setupUI(View view) {
	    //Set up touch listener for non-text box views to hide keyboard.
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                hideSoftKeyboard();
	                return false;
	            }
	        });
	    }
	    
	    //If a layout container, iterate over children and seed recursion.
	    if (view instanceof ViewGroup) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            View innerView = ((ViewGroup) view).getChildAt(i);
	            setupUI(innerView);
	        }
	    }
	}
}
