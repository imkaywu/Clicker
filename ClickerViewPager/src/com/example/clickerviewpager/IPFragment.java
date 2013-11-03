package com.example.clickerviewpager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class IPFragment extends Fragment{
	private EditText editText;
	private Button button;
	private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		sharedPreferences = this.getActivity().getSharedPreferences("info",0);
        editor = sharedPreferences.edit();
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ipfragment, container, false);
        //used to solve the problem of click through
        /*
        relativeLayout=(RelativeLayout) view.findViewById(R.id.ipfragment);
        relativeLayout.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
			
		});*/
        
        setupUI(view.findViewById(R.id.ipfragment));
        
        editText=(EditText)view.findViewById(R.id.ipET);
        button=(Button)view.findViewById(R.id.ipB);
        button.setOnClickListener(new ButtonListener());
        return view;
	}
	
	class ButtonListener implements OnClickListener{
		public void onClick(View v){
			String ip=editText.getText().toString();
			if(!("".equals(ip))){
				editor.putString("ip", ip);
				editor.commit();
				Toast.makeText(getActivity(), "IP地址修改成功!",
						Toast.LENGTH_SHORT).show();
				FragmentManager fragmentManager=getFragmentManager();
		    	fragmentManager.popBackStackImmediate();
			}
			else{
				Toast.makeText(getActivity(), "请输入IP地址!",
						Toast.LENGTH_SHORT).show();
			}
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