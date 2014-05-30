package com.example.demo4.fragments;

import com.example.demo4.R;
import com.example.demo4.activity.CountrySelectActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationFragment extends Fragment{

	private TextView login_coutry_textview;
	private EditText login_county_code_field;
	private EditText login_phone_field;
	public  String country,cc,shortname;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.registration, null);
		login_coutry_textview = (TextView)fragmentView.findViewById(R.id.login_coutry_textview);
		login_county_code_field = (EditText)fragmentView.findViewById(R.id.login_county_code_field);
		login_phone_field = (EditText)fragmentView.findViewById(R.id.login_phone_field);
		login_coutry_textview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent chooseCountry = new Intent(getActivity(), CountrySelectActivity.class);
				startActivityForResult(chooseCountry, 1);
				
			}
		});
		return fragmentView;
	}
	
	public void afterActivityResult(Intent intent) {

		country = intent.getExtras().getString("country");
		cc = intent.getExtras().getString("countrycode");
		shortname = intent.getExtras().getString("shortname");
		setText();
		
	}
	
	public void setText(){
		login_coutry_textview.setText(country);
		login_county_code_field.setText(cc);
	}

}
