package com.example.demo4.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.demo4.DemoApp;
import com.example.demo4.interfaces.FragmentControlClicks;

public class BaseActivity extends SherlockFragmentActivity implements FragmentControlClicks{
	FragmentManager fragmentManager;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		fragmentManager = getSupportFragmentManager();
		
		DemoApp.registerFragmentControlClicks(this);
	}
	@Override
	public void nextClick() {
		
	}
	@Override
	public void cancelClicks() {
		
	}
	@Override
	public void otherClicks(int id) {
		
		
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DemoApp.unregisterFragmentControlClicks();
	}
	

}
