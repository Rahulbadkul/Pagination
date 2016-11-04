package com.votesocracy.pagination;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	private final static String url_page1 = "http://www.json-generator.com/api/json/get/ckDtZPkgJe?indent=2";
	private final static String url_page2 = "http://www.json-generator.com/api/json/get/bVifgkcmxu?indent=2";
	private final static String TAG = MainActivity.class.getSimpleName();
	
	private int pageCount = 0;
	
	private CountryAdapter adapter;
	private ListView listView;
	private ProgressDialog dialog;
	private ArrayList<CountryInfor> countries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.list);
		setListViewAdapter();
		getDataFromUrl(url_page1);
		listView.setOnScrollListener(onScrollListener());
	}

	private void setListViewAdapter() {
		countries = new ArrayList<CountryInfor>();
		adapter = new CountryAdapter(this, R.layout.item_listview, R.id.country_name, countries);
		listView.setAdapter(adapter);
	}
	
	// calling asynctask to get json data from internet
	private void getDataFromUrl(String url) {
		new LoadCountriesFromUrlTask(this, url).execute();
	}

	private OnScrollListener onScrollListener() {
		return new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int threshold = 1;
				int count = listView.getCount();

				if (scrollState == SCROLL_STATE_IDLE) {
					if (listView.getLastVisiblePosition() >= count - threshold && pageCount < 2) {
						Log.i(TAG, "loading more data");
						// Execute LoadMoreDataTask AsyncTask
						getDataFromUrl(url_page2);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
			}

		};
	}

	//parsing json after getting from Internet
	public void parseJsonResponse(String result) {
		Log.i(TAG, result);
		pageCount++;
		try {
			JSONObject json = new JSONObject(result);
			JSONArray jArray = new JSONArray(json.getString("message"));
			for (int i = 0; i < jArray.length(); i++) {
				
				JSONObject jObject = jArray.getJSONObject(i);
				CountryInfor country = new CountryInfor();
				country.setId(jObject.getString("id"));
				country.setName(jObject.getString("name"));
				countries.add(country);
			}
			
			adapter.notifyDataSetChanged();
			if (dialog != null) {
				dialog.dismiss();
			}
		} catch (JSONException e) {
			e.printStackTrace();  
		}
	}

}
