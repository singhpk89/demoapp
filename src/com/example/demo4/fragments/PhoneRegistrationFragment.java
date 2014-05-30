package com.example.demo4.fragments;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.example.demo4.DemoApp;
import com.example.demo4.R;
import com.example.demo4.activity.CountrySelectActivity;
import com.example.demo4.phoneformat.PhoneFormat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneRegistrationFragment extends Fragment{

	private TextView login_coutry_textview;
	private EditText login_county_code_field;
	private EditText login_phone_field;
	public  String country,cc,shortname;

	private int countryState = 0;

	private ArrayList<String> countriesArray = new ArrayList<String>();
	private HashMap<String, String> countriesMap = new HashMap<String, String>();
	private HashMap<String, String> codesMap = new HashMap<String, String>();
	private HashMap<String, String> languageMap = new HashMap<String, String>();

	private boolean ignoreSelection = false;
	private boolean ignoreOnTextChange = false;
	private boolean ignoreOnPhoneChange = false;

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

		//******************************************************************

		login_county_code_field.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (ignoreOnTextChange) {
					ignoreOnTextChange = false;
					return;
				}
				ignoreOnTextChange = true;
				String text = PhoneFormat.stripExceptNumbers(login_county_code_field.getText().toString());
				login_county_code_field.setText(text);
				if (text.length() == 0) {
					login_coutry_textview.setText(getActivity().getApplicationContext().getString( R.string.ChooseCountry));
					countryState = 1;
				} else {
					String country = codesMap.get(text);
					if (country != null) {
						int index = countriesArray.indexOf(country);
						if (index != -1) {
							ignoreSelection = true;
							login_coutry_textview.setText(countriesArray.get(index));

							updatePhoneField();
							countryState = 0;
						} else {
							login_coutry_textview.setText(getActivity().getApplicationContext().getString( R.string.WrongCountry));
							countryState = 2;
						}
					} else {
						login_coutry_textview.setText(getActivity().getApplicationContext().getString( R.string.WrongCountry));
						countryState = 2;
					}
					login_county_code_field.setSelection(login_county_code_field.getText().length());
				}
			}
		});
		login_county_code_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
				if (i == EditorInfo.IME_ACTION_NEXT) {
					login_phone_field.requestFocus();
					return true;
				}
				return false;
			}
		});

		login_phone_field.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (ignoreOnPhoneChange) {
					return;
				}
				if (count == 1 && after == 0 && s.length() > 1) {
					String phoneChars = "0123456789";
					String str = s.toString();
					String substr = str.substring(start, start + 1);
					if (!phoneChars.contains(substr)) {
						ignoreOnPhoneChange = true;
						StringBuilder builder = new StringBuilder(str);
						int toDelete = 0;
						for (int a = start; a >= 0; a--) {
							substr = str.substring(a, a + 1);
							if(phoneChars.contains(substr)) {
								break;
							}
							toDelete++;
						}
						builder.delete(Math.max(0, start - toDelete), start + 1);
						str = builder.toString();
						if (PhoneFormat.strip(str).length() == 0) {
							login_phone_field.setText("");
						} else {
							login_phone_field.setText(str);
							updatePhoneField();
						}
						ignoreOnPhoneChange = false;
					}
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (ignoreOnPhoneChange) {
					return;
				}
				updatePhoneField();
			}
		});

		if(!login_county_code_field.isInEditMode()) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] args = line.split(";");
					countriesArray.add(0, args[2]);
					countriesMap.put(args[2], args[0]);
					codesMap.put(args[0], args[2]);
					languageMap.put(args[1], args[2]);
				}
			} catch (Exception e) {
			}

			Collections.sort(countriesArray, new Comparator<String>() {
				@Override
				public int compare(String lhs, String rhs) {
					return lhs.compareTo(rhs);
				}
			});

			String country = null;

			try {
				TelephonyManager telephonyManager = (TelephonyManager)DemoApp.applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
				if (telephonyManager != null) {
					country = telephonyManager.getSimCountryIso().toUpperCase();
				}
			} catch (Exception e) {
			}

			if (country != null) {
				String countryName = languageMap.get(country);
				if (countryName != null) {
					int index = countriesArray.indexOf(countryName);
					if (index != -1) {
						login_county_code_field.setText(countriesMap.get(countryName));
						countryState = 0;
					}
				}
			}
			if (login_county_code_field.length() == 0) {
				login_coutry_textview.setText(getActivity().getApplicationContext().getString( R.string.ChooseCountry));
				countryState = 1;
			}
		}

		if (login_county_code_field.length() != 0) {
			// Utilities.showKeyboard(phoneField);
			login_phone_field.requestFocus();
		} else {
			// Utilities.showKeyboard(login_county_code_field);
			login_county_code_field.requestFocus();
		}
		login_phone_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
				if (i == EditorInfo.IME_ACTION_NEXT) {
					//delegate.onNextAction();
					return true;
				}
				return false;
			}
		});


		//********************************************************************


		return fragmentView;
	}




	public void selectCountry(String name) {
		int index = countriesArray.indexOf(name);
		if (index != -1) {
			ignoreOnTextChange = true;
			login_county_code_field.setText(countriesMap.get(name));
			login_coutry_textview.setText(name);
			countryState = 0;
		}
	}

	private void updatePhoneField() {
		ignoreOnPhoneChange = true;
		String codeText = login_county_code_field.getText().toString();
		String phone = PhoneFormat.getInstance().format("+" + codeText + login_phone_field.getText().toString());
		int idx = phone.indexOf(" ");
		if (idx != -1) {
			String resultCode = PhoneFormat.stripExceptNumbers(phone.substring(0, idx));
			if (!codeText.equals(resultCode)) {
				phone = PhoneFormat.getInstance().format(login_phone_field.getText().toString()).trim();
				login_phone_field.setText(phone);
				int len = login_phone_field.length();
				login_phone_field.setSelection(login_phone_field.length());
			} else {
				login_phone_field.setText(phone.substring(idx).trim());
				int len = login_phone_field.length();
				login_phone_field.setSelection(login_phone_field.length());
			}
		} else {
			login_phone_field.setSelection(login_phone_field.length());
		}
		ignoreOnPhoneChange = false;
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
		login_phone_field.requestFocus();
	}

}
