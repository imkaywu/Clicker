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

public class PasswordFragment extends Fragment{
	Button passwordB;
	EditText oldPasswordET,newPasswordET,confirmPasswordET;
	String oldPassword="",oldPasswordE="",newPassword="",confirmPassword="";
	private SharedPreferences sharedPreferences;    
    private SharedPreferences.Editor editor; 
    
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	sharedPreferences = this.getActivity().getSharedPreferences("info",0);
        editor = sharedPreferences.edit();
        oldPassword = sharedPreferences.getString("password", null);
    }
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.passwordfragment, container, false);
        
        setupUI(view.findViewById(R.id.passwordfragment));
        
        oldPasswordET=(EditText)view.findViewById(R.id.oldPassword);
        newPasswordET=(EditText)view.findViewById(R.id.newPassword);
        confirmPasswordET=(EditText)view.findViewById(R.id.confirmPassword);
        passwordB=(Button)view.findViewById(R.id.passwordB);
        
        passwordB.setOnClickListener(new ButtonListener());
        return view;
	}
	
	class ButtonListener implements OnClickListener{
		public void onClick(View v){
			oldPasswordE=oldPasswordET.getText().toString();
	        newPassword=newPasswordET.getText().toString();
	        confirmPassword=confirmPasswordET.getText().toString();
	        
	        FragmentManager fragmentManager=getFragmentManager();
	    	//FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
			if("".equals(oldPasswordE)||"".equals(newPassword)||"".equals(confirmPassword)){
				Toast.makeText(getActivity(), "请先输入密码!",
						Toast.LENGTH_SHORT).show();
			}
			else if(oldPassword.equals(oldPasswordE)){
				if(newPassword.equals(confirmPassword)){
					editor.putString("password", newPassword);
					editor.commit();
					Toast.makeText(getActivity(), "密码更改成功!",
							Toast.LENGTH_SHORT).show();
			    	fragmentManager.popBackStackImmediate();
					//Letter letter=new Letter();
					//fragmentTransaction.replace(R.id.pager, letter);
		    		//fragmentTransaction.addToBackStack(null);
		    		//fragmentTransaction.commit();
				}
				else{
					Toast.makeText(getActivity(), "密码验证错误，请重试!",
							Toast.LENGTH_SHORT).show();
					newPasswordET.setText("");
					confirmPasswordET.setText("");
				}
			}
			else{
				Toast.makeText(getActivity(), "密码输入有误，请重试!",
						Toast.LENGTH_SHORT).show();
				oldPasswordET.setText("");
				newPasswordET.setText("");
				confirmPasswordET.setText("");
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
