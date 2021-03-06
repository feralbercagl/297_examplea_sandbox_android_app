/*
//  TabbedNavigationMainNavigationView.java
//  
//
//  Generated by AnyPresence, Inc on 2014-12-19
//  Copyright (c) 2014 . All rights reserved.
 */

package com.anypresence.android.examplea;

import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.anypresence.rails_droid.RemoteObject;

import com.anypresence.library.*;

import android.support.v7.app.ActionBar;

/**
 * An activity with tabs. This is the main point of entry into the app.
 */
public class TabbedNavigationMainNavigationView extends AnyPresenceActivity
		implements
			ISimpleSwapFragmentListener,
			ActionBar.TabListener,
			FragmentManager.OnBackStackChangedListener {

	// Keep a reference to the active tab. We add/remove tabs based off authorization, so we need this to keep track of the dynamic tabs.
	private String mActiveTab;

	/**
	 * Initial set up for the activity. Load views and restore state here.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabbed_navigation);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportFragmentManager().addOnBackStackChangedListener(this);

		if (savedInstanceState != null) {
			mActiveTab = savedInstanceState.getString("active_tab");
		} else {
			mActiveTab = getString(R.string.list_page_examaobjectmodels_page_title);
		}
		buildTabs();
	}

	/**
	 * Save variables you want to restore later (when the app goes in the background)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("active_tab", mActiveTab);
	}

	/**
	 * Swap the main layout in this Activity with a different fragment.
	 * ie: Switched tabs, or drilled in to a list
	 */
	@Override
	public void swapFragment(Fragment fragment) {
		swapFragment(fragment, true);
	}

	/**
	 * See swapFragment(Fragment)
	 *
	 * Boolean option for giving the fragment a backstack. If true, pressing the back button will return to the previous fragment.
	 */
	public void swapFragment(Fragment fragment, boolean backstack) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.realtabcontent, fragment);
		if (backstack)
			transaction.addToBackStack(null);
		transaction.commitAllowingStateLoss();
	}

	/**
	 * Called when a tab is tapped. Switch to that tab.
	 */
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		Tag tag = (Tag) tab.getTag();
		mActiveTab = tag.getId();
		swapFragment(tag.getFragment(), false);
	}

	/**
	 * Called when the active tab is tapped again. We don't need to do anything here.
	 */
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	}

	/**
	 * Called when the active tab is no longer active. onTabSelected also gets called. We don't need to do anything here.
	 */
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	}

	/**
	 * Called when a fragment with a backstack has been added (Or the back button has been pressed).
	 *
	 * We update the actionbar to display the Home button when there's a backstack.
	 */
	@Override
	public void onBackStackChanged() {
		getSupportActionBar().setHomeButtonEnabled(
				getSupportFragmentManager().getBackStackEntryCount() != 0);
		getSupportActionBar().setDisplayHomeAsUpEnabled(
				getSupportFragmentManager().getBackStackEntryCount() != 0);
	}

	/**
	 * Called when a menu item in the actionbar is tapped. Fragments also have this method, so in the activity we just handle the Home button.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (android.R.id.home == item.getItemId()) {
			getSupportFragmentManager().popBackStack();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Build the dynamic tabs. Create tabs for the pages the user is authorized to see.
	 */
	private void buildTabs() {
		getSupportActionBar().removeAllTabs();

		// Add tab for list_page_examaobjectmodels_page_title
		createTab("icon_square",
				getString(R.string.list_page_examaobjectmodels_page_title),
				new ListPageExamaobjectmodelsPageView());

	}

	/**
	 * Creates a tab. Icon and title are for the ui. The fragment is saved in a tag and loaded when the tab is tapped.
	 */
	private void createTab(String icon, String title, Fragment fragment) {
		// Create the layout
		View layout = getLayoutInflater().inflate(R.layout.custom_tab, null);
		IconTextView iconView = (IconTextView) layout.findViewById(R.id.icon);
		iconView.setText(IconTextView.getIcon(icon));
		TextView textView = (TextView) layout.findViewById(R.id.text);
		textView.setText(title);

		// Create the tab
		ActionBar.Tab tab = getSupportActionBar().newTab();
		tab.setCustomView(layout);
		tab.setTabListener(this);
		tab.setTag(new Tag(title, fragment));

		// Add the tab to the actionbar
		getSupportActionBar().addTab(tab, title.equals(mActiveTab));
	}

	/**
	 * A wrapper class so we can give tabs an id and a fragment to load when clicked.
	 */
	static class Tag {
		final String id;
		final Fragment fragment;

		Tag(String id, Fragment fragment) {
			this.id = id;
			this.fragment = fragment;
		}

		String getId() {
			return id;
		}

		Fragment getFragment() {
			return fragment;
		}
	}
}
