package com.example.clickerviewpager;

import android.support.v4.app.Fragment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.content.SharedPreferences; 
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Letter extends Fragment{
	private static final int SERVER_PORT=8080;
	private static final String FLAG="answer";
	private Button aB,bB,cB,dB,eB,fB,sentB;
	private TextView textView;
	private Handler netHandler,uiHandler; 
	private String result="",choiceS="",ID="",IP="";
	private int[] stas={0,0,0,0,0,0};
	private char[] charS=new char[6];
	private NetThread netThread;
	private UIThread uiThread;
	private ButtonListener buttonListener;
	private Socket client=null;
	private PrintWriter out;
	private BufferedReader in;
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreferences preferences=this.getActivity().getSharedPreferences("info",0);  
        ID = preferences.getString("ID", "");
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.letter, container, false);
        
        aB=(Button)view.findViewById(R.id.buttonA);
        bB=(Button)view.findViewById(R.id.buttonB);
        cB=(Button)view.findViewById(R.id.buttonC);
        dB=(Button)view.findViewById(R.id.buttonD);
        eB=(Button)view.findViewById(R.id.buttonE);
        fB=(Button)view.findViewById(R.id.buttonF);
        sentB=(Button)view.findViewById(R.id.sendB);
        textView=(TextView)view.findViewById(R.id.textView);
        textView.setOnTouchListener(new OnTouchListener(){
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
	    				textView.setText("");
	    				choiceS="";
	    				for(int i=0;i<6;i++){
	    					if(stas[i]==1){
	    						switch (i){
	    						case 0:setBackgroundSmart(aB,getActivity().getResources().getDrawable(R.drawable.a));break;
	        					case 1:setBackgroundSmart(bB,getActivity().getResources().getDrawable(R.drawable.b));break;
	        					case 2:setBackgroundSmart(cB,getActivity().getResources().getDrawable(R.drawable.c));break;
	        					case 3:setBackgroundSmart(dB,getActivity().getResources().getDrawable(R.drawable.d));break;
	        					case 4:setBackgroundSmart(eB,getActivity().getResources().getDrawable(R.drawable.e));break;
	        					case 5:setBackgroundSmart(fB,getActivity().getResources().getDrawable(R.drawable.f));break;
	    						}
	    						stas[i]=0;
	    					}
	    				}
	    			}
	    		}
				return false;
			}
        	
        });
        
        buttonListener=new ButtonListener();
        aB.setOnClickListener(buttonListener);
        bB.setOnClickListener(buttonListener);
        cB.setOnClickListener(buttonListener);
        dB.setOnClickListener(buttonListener);
        eB.setOnClickListener(buttonListener);
        fB.setOnClickListener(buttonListener);
        sentB.setOnClickListener(buttonListener);

        netThread=new NetThread();
        uiThread=new UIThread();
        
        //显示网络连接和传输是否成功
        netHandler=new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (result != ""&&result.equals("TRANSFER_SUCCESS")) {
					textView.setText(choiceS);
					choiceS="";
					Toast.makeText(getActivity(), "发送成功",
							Toast.LENGTH_SHORT).show();
				}
				else{
					choiceS="";
					Toast.makeText(getActivity(), "发送失败",
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
		
		//动态地显示用户的选择
		uiHandler=new Handler(new Handler.Callback(){
	        public boolean handleMessage(Message msg){
	        	textView.setText(choiceS);
	        	
	        	return false;
	        }
	       });
	       
        return view;
	}
	
	//按钮监听器，需要简化这一段代码
	class ButtonListener implements OnClickListener{
    	int buttonID;
    	Button[] button=new Button[6];
    	
		public void onClick(View v){
			buttonID=(Integer) v.getId();
			switch(buttonID){
			case R.id.buttonA:
				button[0]=(Button)v;
				buttonPerform(v,button[0],0);
				break;
			case R.id.buttonB:
				button[1]=(Button)v;
				buttonPerform(v,button[1],1);
				break;
			case R.id.buttonC:
				button[2]=(Button)v;
				buttonPerform(v,button[2],2);
				break;
			case R.id.buttonD:
				button[3]=(Button)v;
				buttonPerform(v,button[3],3);
				break;
			case R.id.buttonE:
				button[4]=(Button)v;
				buttonPerform(v,button[4],4);
				break;
			case R.id.buttonF:
				button[5]=(Button)v;
				buttonPerform(v,button[5],5);
				break;
			case R.id.sendB:
        		if (choiceS.length()==0){
					Toast.makeText(getActivity(), "请选择！",
							Toast.LENGTH_SHORT).show();
				}
        		else{
        			for (int i=0;i<6;i++){
        				if(stas[i]==1){
        					switch(i){
        					case 0:setBackgroundSmart(button[i],getActivity().getResources().getDrawable(R.drawable.a));break;
        					case 1:setBackgroundSmart(button[i],getActivity().getResources().getDrawable(R.drawable.b));break;
        					case 2:setBackgroundSmart(button[i],getActivity().getResources().getDrawable(R.drawable.c));break;
        					case 3:setBackgroundSmart(button[i],getActivity().getResources().getDrawable(R.drawable.d));break;
        					case 4:setBackgroundSmart(button[i],getActivity().getResources().getDrawable(R.drawable.e));break;
        					case 5:setBackgroundSmart(button[i],getActivity().getResources().getDrawable(R.drawable.f));break;
        					}
        				stas[i]=0;
        				}
        			}
    				new Thread(netThread).start();
        		}
        		break;
			}
		}
	}

	
    private void buttonPerform(View v,Button button,int i){
		stas[i]=(stas[i]==1)?0:1;
		sendAnswer();
		new Thread(uiThread).start();
		changeBackground(stas[i],v);
    }
    
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setBackgroundSmart(Button b,Drawable drawable){
    	int sdk = android.os.Build.VERSION.SDK_INT;
    	if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
    	    b.setBackgroundDrawable(drawable);
    	} else {
    	    b.setBackground(drawable);
    	}
    }
    //动态记录按键
    private void sendAnswer(){
    	int j=0;
		for(int i=0;i<6;i++){
			if(stas[i]==1){
				charS[j]=(char)(i+65);
				j++;
				}
			}
		choiceS=new String(charS,0,j);//空字符串或者非空的字符串
    }
    
	private void changeBackground(int state, View v){
    	Button b=(Button) v;
    	switch(v.getId()){
    	case R.id.buttonA:
    		switch(state){
    		case 0:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.a));break;
    	    case 1:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.a1));break;
    		}
    		break;
    	case R.id.buttonB:
    		switch(state){
    		case 0:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.b));break;
    	    case 1:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.b1));break;
    		}
    		break;
    	case R.id.buttonC:
    		switch(state){
    		case 0:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.c));break;
    	    case 1:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.c1));break;
    		}
    		break;
    	case R.id.buttonD:
    		switch(state){
    		case 0:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.d));break;
    	    case 1:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.d1));break;
    		}
    		break;
    	case R.id.buttonE:
    		switch(state){
    		case 0:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.e));break;
    	    case 1:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.e1));break;
    		}
    		break;
    	case R.id.buttonF:
    		switch(state){
    		case 0:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.f));break;
    	    case 1:setBackgroundSmart(b,getActivity().getResources().getDrawable(R.drawable.f1));break;
    		}
    		break;
    	}
	}
    
  //创建一个新线程，用于从网络上获取文件
    class NetThread implements Runnable{
    	public void run(){
    		sendViaSocket();
			Message m = netHandler.obtainMessage(); 
			netHandler.sendMessage(m); 
    	}
    }
    
  //申明一个线程用来动态的保存用户的按键并将其传给主线程以显示在ui界面上
    class UIThread implements Runnable{
    	public void run(){
    		Message msg=new Message();
    		uiHandler.sendMessage(msg);
    	}
    }
    
    private void sendViaSocket(){
		SharedPreferences preferences=this.getActivity().getSharedPreferences("info",0);  
		IP=preferences.getString("ip","192.168.1.100");
		try {
			client=new Socket (IP,SERVER_PORT);
			out=new PrintWriter(client.getOutputStream(),true);
		    in =new BufferedReader(new InputStreamReader(client.getInputStream()));
		    out.println(FLAG+":"+ID+":"+choiceS);
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
    
}
