package com.example.demo4.activity;

import com.example.demo4.R;

import com.example.demo4.fragments.RegistrationFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends BaseActivity{

	private RegistrationFragment registrationFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addResgistrationFragment();
		
	}
	
	
	private void addResgistrationFragment() {
		 registrationFragment = new RegistrationFragment();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.fragmetContainer, registrationFragment);
		fragmentTransaction.commit();
	}
	
	private void countrySelectFragment() {
		

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1==RESULT_OK)
		{
			if(registrationFragment!=null)
			registrationFragment.afterActivityResult(arg2);
		}
	}


}
