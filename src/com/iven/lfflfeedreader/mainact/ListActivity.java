package com.iven.lfflfeedreader.mainact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iven.lfflfeedreader.R;
import com.iven.lfflfeedreader.domparser.DOMParser;
import com.iven.lfflfeedreader.domparser.RSSFeed;
import com.iven.lfflfeedreader.imageparserutils.ImageLoader;
import com.iven.lfflfeedreader.infoact.InfoActivity;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import com.baoyz.widget.PullRefreshLayout;
import com.baoyz.widget.PullRefreshLayout.OnRefreshListener;

public class ListActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener, OnRefreshListener {
	 
	  private static final long DRAWER_CLOSE_DELAY_MS = 250;
	  private static final String NAV_ITEM_ID = "navItemId";
	 
	  private final Handler mDrawerActionHandler = new Handler();
	  private DrawerLayout mDrawerLayout;
	  private ActionBarDrawerToggle mDrawerToggle;
	  private int mNavItemId;

	RSSFeed feed;
	ListView list;
	CustomListAdapter adapter;
	String feedURL;
	Intent intent;
	PullRefreshLayout pulltorefresh;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lffl_feed_list);
	      mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	      // load saved navigation state if present
	         if (null == savedInstanceState) {
	           mNavItemId = R.id.about_option;
	         } else {
	           mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
	         }
	      // listen for navigation events
	         NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
	         navigationView.setNavigationItemSelectedListener(this);
	      
	         // select the correct nav menu item
	         navigationView.getMenu().findItem(mNavItemId).setChecked(true);
	      
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
		    setSupportActionBar(toolbar);
		} 
		
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.activity_main);
        // set up the hamburger icon to open and close the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
            R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
       
        navigate(mNavItemId);

        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                    	switch (item.getItemId()) {
                    	case R.id.share_option:
                    	Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.iven.lfflfeedreader");
                        i.putExtra(android.content.Intent.EXTRA_SUBJECT, ("Lffl Feed Reader"));
                        startActivity(Intent.createChooser(i, getString(R.string.share)));

                        return true;
                    }

            			switch (item.getItemId()) {
            			case R.id.rate:
            				rate(list);
            				return (true);
            			}
						return false;
                    }	
            	});


		feedURL = new SplashActivity().LFFLFEEDURL;

		feed = (RSSFeed) getIntent().getExtras().get("feed");

		pulltorefresh = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		pulltorefresh.setOnRefreshListener(this);
		
		list = (ListView) findViewById(android.R.id.list);
		adapter = new CustomListAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int pos = arg2;

				Bundle bundle = new Bundle();
				bundle.putSerializable("feed", feed);
				Intent intent = new Intent(ListActivity.this,
						ArticleActivity.class);
				intent.putExtras(bundle);
				intent.putExtra("pos", pos-1);
				startActivity(intent);

			}
		});

	}
	
	private void setSupportActionBar(Toolbar toolbar) {
	// TODO Auto-generated method stub
	
}

	private void navigate(int mNavItemId2) {
	// TODO Auto-generated method stub
	
}

	public void rate(View view) {
		  Intent intent = new Intent(Intent.ACTION_VIEW);
		  intent.setData(Uri.parse("market://details?id=com.iven.lfflfeedreader"));
		  startActivity(intent);
		}
	
	public void onRefresh() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				DOMParser tmpDOMParser = new DOMParser();
				feed = tmpDOMParser.parseXml(feedURL);

				ListActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (feed != null && feed.getItemCount() > 0) {
							adapter.notifyDataSetChanged();
							
							pulltorefresh.setRefreshing(false);
						}
					}
				});
			}
		});
		thread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.imageLoader.clearCache();
		adapter.notifyDataSetChanged();
	}

	class CustomListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;
		public ImageLoader imageLoader;

		public CustomListAdapter(ListActivity activity) {

			layoutInflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageLoader = new ImageLoader(activity.getApplicationContext());
		}

		@Override
		public int getCount() {

			return feed.getItemCount();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View listItem = convertView;
			int pos = position;
			if (listItem == null) {
				listItem = layoutInflater.inflate(R.layout.items, null);
			}

			ImageView lfflImage = (ImageView) listItem.findViewById(R.id.thumb);
			TextView lfflTitle = (TextView) listItem.findViewById(R.id.title);
			TextView pubDate = (TextView) listItem.findViewById(R.id.date);

			imageLoader.DisplayImage(feed.getItem(pos).getImage(), lfflImage);
			lfflTitle.setText(feed.getItem(pos).getTitle());
			pubDate.setText(feed.getItem(pos).getDate());

			return listItem;
		}

	}

	@Override
	 public boolean onNavigationItemSelected(final MenuItem menuItem) {
	    // update highlighted item in the navigation menu
	    menuItem.setChecked(true);
	    mNavItemId = menuItem.getItemId();
	     
	    // allow some time after closing the drawer before performing real navigation
	    // so the user can see what is happening
	    mDrawerLayout.closeDrawer(GravityCompat.START);
	    mDrawerActionHandler.postDelayed(new Runnable() {
	      @Override
	      public void run() {
	    	  switch (menuItem.getItemId()) {
	        case R.id.about_option:
	        	Intent ii = new Intent(ListActivity.this,InfoActivity.class);
	    		startActivity(ii);
	    	  }
	    		 switch (menuItem.getItemId()) {
	        case R.id.cuties:
	        	Intent ii2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/itcuties/ITCutiesApp-1.0"));
	        	startActivity(ii2);
	    		 }
		        	 switch (menuItem.getItemId()) {
	        case R.id.source_code:
	        	Intent ii4 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/enricocid/lffl-feed-reader"));
	        	startActivity(ii4);
		        	 }
		        	 switch (menuItem.getItemId()) {
	        case R.id.developer1:
	        	Intent ii5 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forum.xda-developers.com/member.php?u=4884893"));
	        	startActivity(ii5);
	        	
		        	 }
		        	 switch (menuItem.getItemId()) {
	        case R.id.developer2:
	        	Intent ii6 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://disqus.com/by/enricodchem/"));
	        	startActivity(ii6);
	        	
		        	 }
		        	 switch (menuItem.getItemId()) {
	        case R.id.mail:
	        	intent = new Intent(android.content.Intent.ACTION_SEND);			
				intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "ivandorte@gmail.com" });			
				intent.setType("message/rfc822");
				if(intent != null) {
					startActivity(Intent.createChooser(intent, getString(R.string.emailC)));

	    	  }

	      }
	    }
	    }, DRAWER_CLOSE_DELAY_MS);
	    return true;
	    
    	}
		
	}
	