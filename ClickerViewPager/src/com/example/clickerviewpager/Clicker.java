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
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Clicker extends FragmentActivity {
	private static final int SERVER_PORT=8080;
	private static final String FLAG="idontknow";
	/**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private List<Fragment> listFragment=new ArrayList<Fragment>();
    private Handler handler;
    private Socket client=null;
    private PrintWriter out;
	private BufferedReader in;
    private DotMarks dotMarks;
    private Button idontknowB;
    private TextView usrInfo;
    private String ID="",IP="",result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clicker);
        SharedPreferences preferences=this.getSharedPreferences("info",0);  
        ID = preferences.getString("ID", "");

        //LayoutInflater layoutInflater=LayoutInflater.from(this);
        //View mainScreen=layoutInflater.inflate(R.layout.activity_screen_slide_pager, null);就是这句让圆点不会改变，为什么？

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager)findViewById(R.id.pager);
        
        listFragment.add(new Letter());
        listFragment.add(new Text());
        listFragment.add(new Settings());
        
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),listFragment);
        mPager.setAdapter(mPagerAdapter);
        
        dotMarks=(DotMarks)findViewById(R.id.dotMarks);
        
        usrInfo=(TextView)this.findViewById(R.id.usrInfo);
        usrInfo.setText(ID);
        
        idontknowB=(Button)this.findViewById(R.id.button);
        idontknowB.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					public void run() {
						sendViaSocket();
						Message m = handler.obtainMessage(); // 获取一个Message
						handler.sendMessage(m); // 发送消息
					}
				}).start();
			}
        	
        });
        
        handler=new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (result != ""&&result.equals("TRANSFER_SUCCESS")) {
					Toast.makeText(Clicker.this, "发送成功",
							Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(Clicker.this, "发送失败",
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
        
        usrInfo=(TextView)this.findViewById(R.id.usrInfo);
        usrInfo.setText(ID);
        /** 
         * 翻页监听事件，用于更新索引小图片 
         */  
        mPager.setOnPageChangeListener(new OnPageChangeListener(){
        	public void onPageSelected(int position){
        		dotMarks.updateDotMark(position);
        	}
        	
        	public void onPageScrolled(int arg0, float arg1, int arg2){
        		// TODO Auto-generated method stub 
        	}
        	
        	public void onPageScrollStateChanged(int arg0) {  
                // TODO Auto-generated method stub  
            }  
        });
    }
    
    private void sendViaSocket(){
		SharedPreferences preferences=this.getSharedPreferences("info",0);  
		IP=preferences.getString("ip","192.168.1.100");
		String outputString="";
		try {
			client=new Socket (IP,SERVER_PORT);
			out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream(),"UTF-8")),true);
		    in =new BufferedReader(new InputStreamReader(client.getInputStream()));
		    outputString=FLAG+":"+ID+":";
		    out.println(outputString);
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

    //下面的语句导致在ipfragment等时要按3次倒退键才能回到主界面，需要修改这段代码！
    /*
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }*/
    
  //DialogFragment的回调函数
    public void doPositiveClick(){
    	//finish();
    	Intent intent = new Intent(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_HOME);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
    }
    public void doNegativeClick(){
    	FragmentManager fragmentManager=getSupportFragmentManager();
    	fragmentManager.popBackStackImmediate();
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mListFragment;
        
    	public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> mListFragment) {
            super(fm);
            this.mListFragment=mListFragment;
        }

    	
        @Override
        public Fragment getItem(int position) {
        	
			return (mListFragment==null||mListFragment.size()==0)?null:mListFragment.get(position);
        }

        @Override
        public int getCount() {
            return mListFragment==null? 0:mListFragment.size();
        }
    }
    
}
