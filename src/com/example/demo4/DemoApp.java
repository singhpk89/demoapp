package com.example.demo4;


import com.example.demo4.interfaces.FragmentControlClicks;

import android.app.Application;

public class DemoApp extends Application{
	
	private static FragmentControlClicks fragmentControlClicks;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	
	public static void registerFragmentControlClicks(FragmentControlClicks clicks)
	{
		fragmentControlClicks = clicks;
	}
	
	public static void unregisterFragmentControlClicks()
	{
		fragmentControlClicks = null;
	}
	
	
}
