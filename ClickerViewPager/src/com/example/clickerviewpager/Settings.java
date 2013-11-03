package com.example.clickerviewpager;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Settings extends ListFragment{
	//private ListView listView;
	private SimpleAdapter adapter;
	private SharedPreferences sharedPreferences;    
	//private OnItemSelectedListener myCallBack;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		sharedPreferences=this.getActivity().getSharedPreferences("info",0);
        
		adapter=new SimpleAdapter(getActivity(),getData(),R.layout.listview_settings,
				new String[]{"image","content"},
				new int[]{R.id.imageView,R.id.listViewContentSettings});
		setListAdapter(adapter);
		
		//intent=new Intent(getActivity(),Account.this);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//View view = inflater.inflate(R.layout.settings, container, false);
  
        return super.onCreateView(inflater, container, savedInstanceState);
		//return view;
	}
	/*实现和activity通信的接口，在listfragment里面貌似并不用得到
	public interface OnItemSelectedListener{
		public void OnItemSelected(int number);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		
		try{
			myCallBack=(OnItemSelectedListener)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + "must implement OnHeadlineSelectedListener");
		}
	}
	*/
	public void onListItemClick(ListView parent, View v, int position, long id){
		super.onListItemClick(parent, v, position, id);
		FragmentManager fragmentManager=getFragmentManager();
    	FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
    	switch(position){
    	case 0:
    		Intent intent=new Intent();
    		sharedPreferences.edit().putBoolean("logIsChecked", false).commit();
    		intent.setClass(getActivity(),Account.class);
    		startActivity(intent);
    		break;
    	case 1:
    		PasswordFragment passwordFragment=new PasswordFragment();
    		fragmentTransaction.replace(R.id.contentFragment, passwordFragment);
    		fragmentTransaction.addToBackStack(null);
    		fragmentTransaction.commit();
    		break;
    	case 2:
    		IPFragment ipFragment=new IPFragment();
    		fragmentTransaction.replace(R.id.contentFragment, ipFragment);
    		fragmentTransaction.addToBackStack(null);
    		fragmentTransaction.commit();
    		break;
    	case 3:
    		ExitFragment exitFragment=ExitFragment.newInstance("Exit?");
    		exitFragment.show(getFragmentManager(), "dialog"); 
    	}
}    
	
	private ArrayList<HashMap<String,Object>> getData(){
		ArrayList<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> map;
		
		map=new HashMap<String,Object>();
		map.put("image",R.drawable.user);
		map.put("content","切换用户");
		list.add(map);
		
		map=new HashMap<String,Object>();
		map.put("image",R.drawable.password);
		map.put("content","更改密码");
		list.add(map);
		
		map=new HashMap<String,Object>();
		map.put("image",R.drawable.ip);
		map.put("content","更改IP地址");
		list.add(map);
		
		map=new HashMap<String,Object>();
		map.put("image", R.drawable.exit);
		map.put("content", "退出");
		list.add(map);	
		
		return list;
	}
	
	public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
	
}
