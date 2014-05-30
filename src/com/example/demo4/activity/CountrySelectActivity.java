package com.example.demo4.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.widget.SearchView;
import com.example.demo4.R;
import com.example.demo4.adapter.SectionedBaseAdapter;
import com.example.demo4.views.CustomHeaderListView;

public class CountrySelectActivity extends SherlockActivity {
    private SearchView searchView;
    private SectionedBaseAdapter listViewAdapter;
    private CustomHeaderListView listView;
    private boolean searchWas;
    private boolean searching;
    private BaseAdapter searchListViewAdapter;
    private TextView emptyTextView;
    private HashMap<String, ArrayList<Country>> countries = new HashMap<String, ArrayList<Country>>();
    private ArrayList<String> sortedCountries = new ArrayList<String>();

    private Timer searchTimer;
    public ArrayList<Country> searchResult;

    public static class Country {
        public String name;
        public String code;
        public String shortname;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        searching = false;
        searchWas = false;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] args = line.split(";");
                Country c = new Country();
                c.name = args[2];
                c.code = args[0];
                c.shortname = args[1];
                String n = c.name.substring(0, 1).toUpperCase();
                ArrayList<Country> arr = countries.get(n);
                if (arr == null) {
                    arr = new ArrayList<Country>();
                    countries.put(n, arr);
                    sortedCountries.add(n);
                }
                arr.add(c);
            }
        } catch (Exception e) {
            //FileLog.e("tmessages", e);
        }

        Collections.sort(sortedCountries, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        for (ArrayList<Country> arr : countries.values()) {
            Collections.sort(arr, new Comparator<Country>() {
                @Override
                public int compare(Country country, Country country2) {
                    return country.name.compareTo(country2.name);
                }
            });
        }

        setContentView(R.layout.country_select_layout);

        emptyTextView = (TextView)findViewById(R.id.searchEmptyView);
        searchListViewAdapter = new SearchAdapter(this);

        listView = (CustomHeaderListView)findViewById(R.id.listView);
        listView.setEmptyView(emptyTextView);
        listView.setVerticalScrollBarEnabled(false);

        listView.setAdapter(listViewAdapter = new ListAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (searching && searchWas) {
                    if (i < searchResult.size()) {
                        Country c = searchResult.get(i);
                        Intent intent = new Intent();
                        intent.putExtra("country", c.name);
                        intent.putExtra("countrycode", c.code);
                        intent.putExtra("shortname", c.shortname);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    int section = listViewAdapter.getSectionForPosition(i);
                    int row = listViewAdapter.getPositionInSectionForPosition(i);
                    if (section < sortedCountries.size()) {
                        String n = sortedCountries.get(section);
                        ArrayList<Country> arr = countries.get(n);
                        if (row < arr.size()) {
                            Country c = arr.get(row);
                            Intent intent = new Intent();
                            intent.putExtra("country", c.name);
                            intent.putExtra("countrycode", c.code);
                            intent.putExtra("shortname", c.shortname);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            }
        });

        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        getWindow().setFormat(PixelFormat.RGB_565);
    }

    public void applySelfActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setCustomView(null);
        actionBar.setSubtitle(null);
        actionBar.setTitle(getString(R.string.ChooseCountry));
        fixBackButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applySelfActionBar();
    }

   
  

    public void fixBackButton() {
        if(android.os.Build.VERSION.SDK_INT == 19) {
            //workaround for back button dissapear
            try {
                Class firstClass = getSupportActionBar().getClass();
                Class aClass = firstClass.getSuperclass();
                if (aClass == ActionBar.class) {

                } else {
                    Field field = aClass.getDeclaredField("mActionBar");
                    field.setAccessible(true);
                    android.app.ActionBar bar = (android.app.ActionBar)field.get(getSupportActionBar());

                    field = bar.getClass().getDeclaredField("mActionView");
                    field.setAccessible(true);
                    View v = (View)field.get(bar);
                    aClass = v.getClass();

                    field = aClass.getDeclaredField("mHomeLayout");
                    field.setAccessible(true);
                    v = (View)field.get(v);
                    v.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void search(final String query) {
        if (query == null) {
            searchResult = null;
        } else {
            try {
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
            } catch (Exception e) {
               // FileLog.e("tmessages", e);
            }
            searchTimer = new Timer();
            searchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        searchTimer.cancel();
                        searchTimer = null;
                    } catch (Exception e) {
                      //  FileLog.e("tmessages", e);
                    }
                    processSearch(query);
                }
            }, 100, 300);
        }
    }

    private void processSearch(final String query) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String q = query.trim().toLowerCase();
                if (q.length() == 0) {
                    updateSearchResults(new ArrayList<Country>());
                    return;
                }
                long time = System.currentTimeMillis();
                ArrayList<Country> resultArray = new ArrayList<Country>();

                String n = query.substring(0, 1);
                ArrayList<Country> arr = countries.get(n.toUpperCase());
                if (arr != null) {
                    for (Country c : arr) {
                        if (c.name.toLowerCase().startsWith(query)) {
                            resultArray.add(c);
                        }
                    }
                }

                updateSearchResults(resultArray);
            }
        });
    }

    private void updateSearchResults(final ArrayList<Country> arrCounties) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchResult = arrCounties;
                searchListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    private class SearchAdapter extends BaseAdapter {
        private Context mContext;

        public SearchAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            if (searchResult == null) {
                return 0;
            }
            return searchResult.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.country_row_layout, viewGroup, false);
            }
            TextView textView = (TextView)view.findViewById(R.id.settings_row_text);
            TextView detailTextView = (TextView)view.findViewById(R.id.settings_row_text_detail);
            View divider = view.findViewById(R.id.settings_row_divider);

            Country c = searchResult.get(i);
            textView.setText(c.name);
            detailTextView.setText("+" + c.code);
            if (i == searchResult.size() - 1) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
            }

            return view;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return searchResult == null || searchResult.size() == 0;
        }
    }

    private class ListAdapter extends SectionedBaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object getItem(int section, int position) {
            return null;
        }

        @Override
        public long getItemId(int section, int position) {
            return 0;
        }

        @Override
        public int getSectionCount() {
            return sortedCountries.size();
        }

        @Override
        public int getCountForSection(int section) {
            String n = sortedCountries.get(section);
            ArrayList<Country> arr = countries.get(n);
            return arr.size();
        }

        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = li.inflate(R.layout.country_row_layout, parent, false);
            }
            TextView textView = (TextView)convertView.findViewById(R.id.settings_row_text);
            TextView detailTextView = (TextView)convertView.findViewById(R.id.settings_row_text_detail);
            View divider = convertView.findViewById(R.id.settings_row_divider);

            String n = sortedCountries.get(section);
            ArrayList<Country> arr = countries.get(n);
            Country c = arr.get(position);
            textView.setText(c.name);
            detailTextView.setText("+" + c.code);
            if (position == arr.size() - 1) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        @Override
        public int getItemViewType(int section, int position) {
            return 0;
        }

        @Override
        public int getItemViewTypeCount() {
            return 1;
        }

        @Override
        public int getSectionHeaderViewType(int section) {
            return 0;
        }

        @Override
        public int getSectionHeaderViewTypeCount() {
            return 1;
        }

        @Override
        public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = li.inflate(R.layout.settings_section_layout, parent, false);
                convertView.setBackgroundColor(0xfffafafa);
            }
            TextView textView = (TextView)convertView.findViewById(R.id.settings_section_text);
            textView.setText(sortedCountries.get(section).toUpperCase());
            return convertView;
        }
    }
}
