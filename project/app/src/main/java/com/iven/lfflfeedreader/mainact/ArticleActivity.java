package com.iven.lfflfeedreader.mainact;

import android.annotation.SuppressLint;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.iven.lfflfeedreader.R;
import com.iven.lfflfeedreader.domparser.RSSFeed;
import com.iven.lfflfeedreader.utils.Preferences;

@SuppressLint("InlinedApi")
public class ArticleActivity extends AppCompatActivity {

	RSSFeed feed;
	int pos;
	private ViewPager pager;
	private PagerAdapter mPagerAdapter;

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Preferences.applyTheme(this);
		setContentView(R.layout.article_activity);

		feed = (RSSFeed) getIntent().getExtras().get("feed");
		pos = getIntent().getExtras().getInt("pos");
		context = getBaseContext();
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(mPagerAdapter);
        pager.setCurrentItem(pos);
        pager.setClipToPadding(false);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		if (Preferences.navTintEnabled(this)) {
			getWindow().setNavigationBarColor(getResources().getColor(R.color.iven2));
		}

        if (Preferences.immersiveEnabled(this)) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }

	    setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
				overridePendingTransition(0, 0);
			}
		});
		}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {

						public MyPagerAdapter(FragmentManager fm) {

							super(fm);
						}

						@Override
						public Fragment getItem(int position) {

							if (Preferences.WebViewEnabled(context)) {
								ArticleFragmentWebView fragwb = new ArticleFragmentWebView();
								Bundle bundle = new Bundle();
								bundle.putSerializable("feed", feed);
								bundle.putInt("pos", position);
								fragwb.setArguments(bundle);

								return fragwb;
							} else {
								ArticleFragment frag = new ArticleFragment();
								Bundle bundle = new Bundle();
								bundle.putSerializable("feed", feed);
								bundle.putInt("pos", position);
								frag.setArguments(bundle);

								return frag;

						}

                            }

		@Override
		public int getCount() {
			return feed.getItemCount();
		}
			}
		}




