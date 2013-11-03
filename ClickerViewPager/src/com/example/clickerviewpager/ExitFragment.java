package com.example.clickerviewpager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExitFragment extends DialogFragment{
	static ExitFragment newInstance(String title){
		ExitFragment exitFragment=new ExitFragment();
		Bundle args=new Bundle();
		args.putString("title",title);
		exitFragment.setArguments(args);
		return exitFragment;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        
        return view;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {  
	    String title = getArguments().getString("title");  
	    return new AlertDialog.Builder(getActivity())  
	    .setIcon(R.drawable.ic_launcher)  
	    .setTitle(title)  
	    .setPositiveButton("确定",  
	            new DialogInterface.OnClickListener() {  
	        public void onClick(DialogInterface dialog,   
	                int whichButton) {  
	            ((Clicker)  
	                    getActivity()).doPositiveClick();  
	        }  
	    })  
	    .setNegativeButton("取消",  
	            new DialogInterface.OnClickListener() {  
	        public void onClick(DialogInterface dialog,   
	                int whichButton) {  
	            ((Clicker)  
	                    getActivity()).doNegativeClick();  
	        }  
	    }).create();  
	}          
}
