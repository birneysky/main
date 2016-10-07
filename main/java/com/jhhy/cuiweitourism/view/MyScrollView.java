package com.jhhy.cuiweitourism.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	GestureDetector gestureDetector;
	public MyScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		super.onTouchEvent(ev);
		if(gestureDetector != null){
//			Log.i("info", "result = " + gestureDetector.onTouchEvent(ev));
			return gestureDetector.onTouchEvent(ev);
		}else{
			return true; //add by myself
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		if(gestureDetector != null) {
			gestureDetector.onTouchEvent(ev);
		}
		super.dispatchTouchEvent(ev);
		return true;
	} 
	
}
