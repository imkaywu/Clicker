package com.example.clickerviewpager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class Account extends Activity{
	private static final int SERVER_PORT=8080;
	private static final String FLAG="account";
	EditText idET,passwordET;
	Button cancelB,loginB/*,anonyLoginB*/;
	CheckBox remCode,remLog;
	String ID="",password="",passwordInit="111111",IP="",result="";
	private SharedPreferences sharedPreferences;    
    private SharedPreferences.Editor editor; 
    NetThread netThread;
    Handler netHandler;
    Intent intent;
    private Socket client=null;
	private PrintWriter out;
	private BufferedReader in;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  //去除标题  
        setContentView(R.layout.account);
        
        sharedPreferences = this.getSharedPreferences("info",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        intent=new Intent(Account.this,Clicker.class);
        
        idET=(EditText)findViewById(R.id.idET);
        passwordET=(EditText)findViewById(R.id.passwordET);
        cancelB=(Button)findViewById(R.id.cancelB);
        loginB=(Button)findViewById(R.id.loginB);
        //anonyLoginB=(Button)findViewById(R.id.anonyLoginB);
        remLog=(CheckBox)findViewById(R.id.remLog);
        remCode=(CheckBox)findViewById(R.id.remCode);
       //实现按钮和checkbox的监听器 
        cancelB.setOnClickListener(new bListener());
        loginB.setOnClickListener(new bListener());
        
        idET.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float startx=0,offset;
	    		switch(event.getAction()){
	    		case MotionEvent.ACTION_DOWN:
	    			startx=event.getX();
	    			break;
	    		case MotionEvent.ACTION_MOVE:
	    			offset=event.getX()-startx;
	    			if(Math.abs(offset)>10){
	    				idET.setText("");
	    				break;
	    			}
	    		}
				return false;
			}
        });
        
        passwordET.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				float startx=0,offset;
	    		switch(event.getAction()){
	    		case MotionEvent.ACTION_DOWN:
	    			startx=event.getX();
	    			break;
	    		case MotionEvent.ACTION_MOVE:
	    			offset=event.getX()-startx;
	    			if(Math.abs(offset)>10){
	    				passwordET.setText("");
	    			}
	    		}
				return false;
			}
        });
        
        remLog.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
                if (remLog.isChecked()) {  
                    System.out.println("自动登录已选中");  
                    sharedPreferences.edit().putBoolean("logIsChecked", true).commit();  
  
                } else {  
                    System.out.println("自动登录没有选中");  
                    sharedPreferences.edit().putBoolean("logIsChecked", false).commit();  
                }  
            }  
        });  
        
        remCode.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
                if (remCode.isChecked()) {  
                    System.out.println("记住密码已选中");  
                    sharedPreferences.edit().putBoolean("codeIsChecked", true).commit();  
  
                } else {  
                    System.out.println("记住密码没有选中");  
                    sharedPreferences.edit().putBoolean("codeIsChecked", false).commit();  
                }  
            }  
        });  
        
        if(sharedPreferences.getBoolean("codeIsChecked", false)){
        	remCode.setChecked(true);
        	idET.setText(sharedPreferences.getString("ID",""));
        	passwordET.setText(sharedPreferences.getString("password",""));
        }
        
        if(sharedPreferences.getBoolean("logIsChecked",false)){
        	remLog.setChecked(true);
        	startActivity(intent);
        	finish();//跳转到第二个activity后，将这个activity destroy掉
        }
        
        netThread=new NetThread();
        netHandler=new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (result != ""&&result.equals("TRANSFER_SUCCESS")) {
					Toast.makeText(Account.this, "登录成功",
							Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(Account.this, "登录失败",
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
    }

    class bListener implements OnClickListener{
    	public void onClick(View view){
    		switch((Integer)view.getId()){
    		case R.id.cancelB:
    			idET.setText("");
    			passwordET.setText("");
    		case R.id.loginB:
    			ID=idET.getText().toString();
    			password=passwordET.getText().toString();
    			if("".equals(ID)||"".equals(password)){
    				Toast.makeText(Account.this, "请输入登录信息！",
							Toast.LENGTH_SHORT).show();
    			}
    			else{
    				if(!sharedPreferences.contains("password")){
    					if(password.equals(passwordInit)){
    						new Thread(netThread).start();
    						editor.putString("ID", ID);
    						editor.putString("password", password);
    						editor.commit();
    						startActivity(intent);
    						finish();
    					}
    					else{
    						passwordET.setText("");
    						Toast.makeText(Account.this, "密码错误，请重试！",
    								Toast.LENGTH_SHORT).show();
    					}
    				}
    				else{
    					String passwordTemp=sharedPreferences.getString("password",null);
    					if(password.equals(passwordTemp)){
    						new Thread(netThread).start();
    						editor.putString("ID",ID);
    						editor.putString("password", password);
    						editor.commit();
    						startActivity(intent);
    						finish();
    					}
    					else{
    						passwordET.setText("");
    						Toast.makeText(Account.this, "密码错误，请重试！",
    								Toast.LENGTH_SHORT).show();
    					}
    				}
    			}
    		}
    	}
    }
    
    class NetThread implements Runnable{
    	public void run(){
    		sendViaSocket();
			Message m = netHandler.obtainMessage(); 
			netHandler.sendMessage(m);
    	}
    }
    
    private void sendViaSocket(){
    	result="";
		SharedPreferences preferences=this.getSharedPreferences("info",0);  
		IP=preferences.getString("ip","192.168.1.100");
		try {
			client=new Socket (IP,SERVER_PORT);
			out=new PrintWriter(client.getOutputStream(),true);
		    in =new BufferedReader(new InputStreamReader(client.getInputStream()));
		    out.println(FLAG+":"+ID+":");
			result=in.readLine();
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
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_account, menu);
        return true;
    }*/
    
    public boolean onTouchEvent(MotionEvent event) {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    	if(idET.isFocused()){
    		idET.clearFocus();
    		imm.hideSoftInputFromWindow(idET.getWindowToken(), 0);
    	}
    	else if(passwordET.isFocused()){
    		passwordET.clearFocus();
    		imm.hideSoftInputFromWindow(idET.getWindowToken(), 0);
    	}
        return super.onTouchEvent(event);
    }
}
