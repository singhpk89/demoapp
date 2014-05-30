package com.example.demo4.fragments;

import com.example.demo4.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UserRegistrationFragments extends Fragment{
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.userinfo, null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
