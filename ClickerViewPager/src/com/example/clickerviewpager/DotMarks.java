package com.example.clickerviewpager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class DotMarks extends LinearLayout{
	private ImageView dotMark1,dotMark2,dotMark3;
	private Drawable dotMarkFocus=getResources().getDrawable(R.drawable.dotmarkfocus);
	private Drawable dotMarkUnfocus=getResources().getDrawable(R.drawable.dotmarkunfocus);
	
	public DotMarks(Context context){
		super(context);
		init(context);
	}
	
	public DotMarks(Context context, AttributeSet attrs){
		super(context,attrs);
		init(context);
	}
	
	private void init(Context context){
		LayoutInflater layoutInflater=LayoutInflater.from(context);
		View dotMarks=layoutInflater.inflate(R.layout.dotmarks, null);
		this.addView(dotMarks);
		dotMark1=(ImageView)dotMarks.findViewById(R.id.dotMark1);
		dotMark2=(ImageView)dotMarks.findViewById(R.id.dotMark2);
		dotMark3=(ImageView)dotMarks.findViewById(R.id.dotMark3);
		
		//set a default
		dotMark1.setImageResource(R.drawable.dotmarkfocus);
		dotMark2.setImageResource(R.drawable.dotmarkunfocus);
		dotMark3.setImageResource(R.drawable.dotmarkunfocus);
	}
	
	public void updateDotMark(int position){
		switch(position){
		case 0:
			dotMark1.setImageDrawable(dotMarkFocus);
			dotMark2.setImageDrawable(dotMarkUnfocus);
			dotMark3.setImageDrawable(dotMarkUnfocus);
			break;
		case 1:
			dotMark1.setImageDrawable(dotMarkUnfocus);
			dotMark2.setImageDrawable(dotMarkFocus);
			dotMark3.setImageDrawable(dotMarkUnfocus);
			break;
		case 2:
			dotMark1.setImageDrawable(dotMarkUnfocus);
			dotMark2.setImageDrawable(dotMarkUnfocus);
			dotMark3.setImageDrawable(dotMarkFocus);
			break;
		default:break;
		}
	}
}
