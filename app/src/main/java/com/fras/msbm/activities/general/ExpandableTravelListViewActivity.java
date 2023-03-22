package com.fras.msbm.activities.general;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;


import com.fras.msbm.R;
import com.fras.msbm.font.AnimatedExpandableListView;
import com.fras.msbm.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpandableTravelListViewActivity extends AppCompatActivity {
	public static final String TAG = ExpandableTravelListViewActivity.class.getName();
	List<GroupItem> items;
	private AnimatedExpandableListView listView;
	private ExampleAdapter adapter;
	public static String moodleLocation;
	private FirebaseUser user;
	final FirebaseDatabase database = FirebaseDatabase.getInstance();
	private Bundle extras;

	final DatabaseReference userRef = database.getReference("users");

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expandable_list_view_travel);
		items = new ArrayList<GroupItem>();
		extras = getIntent().getExtras();
		moodleLocation = extras.getString("LOCATION");
//		user = FirebaseAuth.getInstance().getCurrentUser();
//
//		if (user != null ) findUserById(user.getUid());

		if(moodleLocation != null && !moodleLocation.isEmpty()) {
			Log.e(TAG, "Expandable Found Location: " + moodleLocation);
			switch(moodleLocation){
				case "mona":
					items = fillData(items);
					break;
				case "wcj":
					items = fillWesternData(items);
					break;
			}
		}else{
			items = fillData(items);
		}

//		items = fillData(items);


		adapter = new ExampleAdapter(this);
		adapter.setData(items);

		listView = (AnimatedExpandableListView) findViewById(R.id.activity_expandable_travel_list_view);
		View headerView = getLayoutInflater().inflate(
				R.layout.header_expandable_list_view_travel, listView, false);
		listView.addHeaderView(headerView);
		listView.setDividerHeight(0);
		listView.setAdapter(adapter);

		// In order to show animations, we need to use a custom click handler
		// for our ExpandableListView.
		listView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
										int groupPosition, long id) {
				// We call collapseGroupWithAnimation(int) and
				// expandGroupWithAnimation(int) to animate group
				// expansion/collapse.
				if (listView.isGroupExpanded(groupPosition)) {
					listView.collapseGroupWithAnimation(groupPosition);
				} else {
					listView.expandGroupWithAnimation(groupPosition);
				}
				return true;
			}

		});

		listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
//				Toast.makeText(getBaseContext(),"Test!!!" + " int i " + Integer.toString(groupPosition) + " int i1 " + Integer.toString(childPosition),Toast.LENGTH_SHORT)
//						.show();
				GroupItem groupItem = items.get(groupPosition);
				ChildItem location = groupItem.items.get(childPosition);
//				Toast.makeText(getBaseContext(),location.title,Toast.LENGTH_SHORT)
//						.show();

				Intent intent = new Intent(ExpandableTravelListViewActivity.this, HomeActivity.class);
				intent.putExtra("FROM_LOCATIONS",groupItem.title);
				intent.putExtra("FROM_LOCATIONS_CHILD",location.title);
				startActivity(intent);
//				expandableListView.
//				child
				return true;
			}
		});

//		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view,
//									int position, long id) {
//				//As you are using Default String Adapter
//				Toast.makeText(getBaseContext(),"Test!!!",Toast.LENGTH_SHORT).show();
//			}
//		});

		// Set indicator (arrow) to the right
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// Log.v("width", width + "");
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				50, r.getDisplayMetrics());
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			listView.setIndicatorBounds(width - px, width);
		} else {
			listView.setIndicatorBoundsRelative(width - px, width);
		}

	}

//	private void findUserById(@NonNull String userId) {
//		userRef.child(userId).child("moodle")
//				.addListenerForSingleValueEvent(new ValueEventListener() {
//					@Override
//					public void onDataChange(DataSnapshot dataSnapshot) {
//						final User user = dataSnapshot.getValue(User.class);
//						Log.i("TAG", "userCourses:" + user.toString());
//						moodleLocation = user.getMoodleLocation();
//						Log.e(TAG, "Location:" + moodleLocation);
////                        Toast.makeText(BookingsActivity.this, "Token: " + user.getToken(), Toast.LENGTH_SHORT).show();
////                        loadUserCourses(user);
//					}
//
//					@Override
//					public void onCancelled(DatabaseError databaseError) {
////                        showErrorViews();
//						FirebaseCrash.report(databaseError.toException());
//					}
//				});
//	}

	private static class GroupItem {
		String title;
		int icon;
		List<ChildItem> items = new ArrayList<ChildItem>();
	}

	private static class ChildItem {
		String title;
	}

	private static class ChildHolder {
		TextView title;
	}

	private static class GroupHolder {
		TextView title;
		TextView icon;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private List<GroupItem> fillWesternData(List<GroupItem> items){
		GroupItem item = new GroupItem();
		item.title = "Classes";
		item.icon = R.string.material_icon_bookmark;
		ChildItem child;

		child = new ChildItem();
		child.title = "All";
		item.items.add(child);

		child = new ChildItem();
		child.title = "E202 Classroom";
		item.items.add(child);

		child = new ChildItem();
		child.title = "E203 Classroom";
		item.items.add(child);

		child = new ChildItem();
		child.title = "W103 Classroom";
		item.items.add(child);

		child = new ChildItem();
		child.title = "W202 Classroom";
		item.items.add(child);

		items.add(item);
//		--------------------------
		item = new GroupItem();
		item.title = "Faculties/Departments";
		item.icon = R.string.material_icon_city;

		child = new ChildItem();
		child.title = "All";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Bookshop";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Computer Lab";
		item.items.add(child);

		child = new ChildItem();
		child.title = "IT Office";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Library";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Main Office";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Msbm Office";
		item.items.add(child);

		child = new ChildItem();
		child.title = "OSSD";
		item.items.add(child);



		items.add(item);
//		----------------------------------

		item = new GroupItem();
		item.title = "Where to eat";
		item.icon = R.string.material_icon_eat;

		child = new ChildItem();
		child.title = "Cafeteria";
		item.items.add(child);

		items.add(item);

//		-----------------------------------

		item = new GroupItem();
		item.title = "Banks/ATM";
		item.icon = R.string.material_icon_dollar;

		child = new ChildItem();
		child.title = "All";
		item.items.add(child);

		child = new ChildItem();
		child.title = "ATM";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Credit Union";
		item.items.add(child);

//		child = new ChildItem();
//		child.title = "All";
//		item.items.add(child);

		items.add(item);

// 		-----------------------------------




//		-----------------------------------

		return items;
	}

	//Mona Campus
	private List<GroupItem> fillData(List<GroupItem> items) {
		GroupItem item = new GroupItem();
		item.title = "Classes";
		item.icon = R.string.material_icon_bookmark;
		ChildItem child;

		child = new ChildItem();
		child.title = "All";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Ashcroft";
		item.items.add(child);

		child = new ChildItem();
		child.title = "BLT";
		item.items.add(child);

		child = new ChildItem();
		child.title = "CHEM/Phys";
		item.items.add(child);

		child = new ChildItem();
		child.title = "CHEM2 / C2";
		item.items.add(child);

		child = new ChildItem();
		child.title = "CHEM3 /C3";
		item.items.add(child);

		child = new ChildItem();
		child.title = "CHEM4 /C4";
		item.items.add(child);

		child = new ChildItem();
		child.title = "CHEM5 /C5";
		item.items.add(child);

		child = new ChildItem();
		child.title = "CHEM6/C6";
		item.items.add(child);

		child = new ChildItem();
		child.title = "CHEM7 /C7";
		item.items.add(child);

		child = new ChildItem();
		child.title = "DOMS Lab/DOMS Main";
		item.items.add(child);

		child = new ChildItem();
		child.title = "GCR";
		item.items.add(child);

		child = new ChildItem();
		child.title = "GLT1/D101";
		item.items.add(child);

		child = new ChildItem();
		child.title = "GLT2/I101";
		item.items.add(child);

		child = new ChildItem();
		child.title = "GLT3/I102";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Grad lab A";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Grad lab B";
		item.items.add(child);

		child = new ChildItem();
		child.title = "IFLT";
		item.items.add(child);

		child = new ChildItem();
		child.title = "IFSR1";
		item.items.add(child);

		child = new ChildItem();
		child.title = "IFSR2";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Lab 6";
		item.items.add(child);

		child = new ChildItem();
		child.title = "LangLab1";
		item.items.add(child);

		child = new ChildItem();
		child.title = "LangLab2";
		item.items.add(child);

		child = new ChildItem();
		child.title = "LangLab3";
		item.items.add(child);

		child = new ChildItem();
		child.title = "LLSRM";
		item.items.add(child);

		child = new ChildItem();
		child.title = "N1";
		item.items.add(child);

		child = new ChildItem();
		child.title = "N2";
		item.items.add(child);

		child = new ChildItem();
		child.title = "N3";
		item.items.add(child);

		child = new ChildItem();
		child.title = "N4";
		item.items.add(child);

		child = new ChildItem();
		child.title = "N5";
		item.items.add(child);

		child = new ChildItem();
		child.title = "NELT";
		item.items.add(child);

		child = new ChildItem();
		child.title = "OELT";
		item.items.add(child);

		child = new ChildItem();
		child.title = "PCLT/SLT2";
		item.items.add(child);

		child = new ChildItem();
		child.title = "PLT/SLT3";
		item.items.add(child);//---

		child = new ChildItem();
		child.title = "SLT/SLT1";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR4";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR5";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR6";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR8";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SSLT";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR10";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR11";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR12";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR14";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR15";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR16";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR22";
		item.items.add(child);

		child = new ChildItem();
		child.title = "SR23";
		item.items.add(child);

		child = new ChildItem();
		child.title = "TR11";
		item.items.add(child);

		child = new ChildItem();
		child.title = "TR12";
		item.items.add(child);

		child = new ChildItem();
		child.title = "TR20";
		item.items.add(child);

//		child = new ChildItem();
//		child.title = "SR4";
//		item.items.add(child);
//
//		child = new ChildItem();
//		child.title = "SR4";
//		item.items.add(child);


		items.add(item);
//		--------------------------------------------- material_icon_bookmark
		item = new GroupItem();
		item.title = "Where to go";
		item.icon = R.string.material_icon_go;

		child = new ChildItem();
		child.title = "All";
		item.items.add(child);
//
//		child = new ChildItem();
//		child.title = "Sightseeing";
//		item.items.add(child);
//
//		child = new ChildItem();
//		child.title = "Historical";
//		item.items.add(child);
//
//		child = new ChildItem();
//		child.title = "Sport";
//		item.items.add(child);
//
//		items.add(item);

		item = new GroupItem();
		item.title = "Halls";
		item.icon = R.string.material_icon_sleep;

//		child = new ChildItem();
//		child.title = "All";
//		item.items.add(child);

		child = new ChildItem();
		child.title = "Irvine Hall";
		item.items.add(child);
//
		child = new ChildItem();
		child.title = "Chancellor Hall";
		item.items.add(child);
//
		child = new ChildItem();
		child.title = "Taylor Hall";
		item.items.add(child);
//
		child = new ChildItem();
		child.title = "Marlene Hamilton Hall";
		item.items.add(child);

		child = new ChildItem();
		child.title = "AZ Preston Hall";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Mary Seacole Hall";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Elsa-Leo Rhynie Hall";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Rex Nettleford Hall";
		item.items.add(child);

		items.add(item);
//   ------------------------------
		item = new GroupItem();
		item.title = "Where to eat";
		item.icon = R.string.material_icon_eat;

		child = new ChildItem();
		child.title = "All";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Kentucky Fried Chicken";
		item.items.add(child);
//
		child = new ChildItem();
		child.title = "Juici Patties";
		item.items.add(child);
//
		child = new ChildItem();
		child.title = "Yoa's Chinese Restaurant";
		item.items.add(child);
//
		child = new ChildItem();
		child.title = "Bee Hive";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Mary Seacole Cafeteria";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Hi-Lo Food Store";
		item.items.add(child);

		child = new ChildItem();
		child.title = "The Spot";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Senior Common Room Club and Restaurant";
		item.items.add(child);

		items.add(item);
//   ------------------------------
		item = new GroupItem();
		item.title = "Where to drink";
		item.icon = R.string.material_icon_drink;

		child = new ChildItem();
		child.title = "All";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Senior Common Room Club and Restaurant";
		item.items.add(child);

		child = new ChildItem();
		child.title = "The Spot";
		item.items.add(child);

//		child = new ChildItem();
//		child.title = "Caffes";
//		item.items.add(child);
//
//		child = new ChildItem();
//		child.title = "Bars";
//		item.items.add(child);
//
//		child = new ChildItem();
//		child.title = "Pubs";
//		item.items.add(child);
//
//		child = new ChildItem();
//		child.title = "Clubs";
//		item.items.add(child);

		items.add(item);
//   ------------------------------
		item = new GroupItem();
		item.title = "Faculties/Departments";
		item.icon = R.string.material_icon_city;

		child = new ChildItem();
		child.title = "All";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Faculty of Humanities";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Faculty of Social Sciences";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Faculty Of Medical Science";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Mona School of Business and Management (North)";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Mona School of Business and Management (South)";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Faculty of Law";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Norman Manley Law School";
		item.items.add(child);

		child = new ChildItem();
		child.title = "Faculty of Science and Technology";
		item.items.add(child);

		child = new ChildItem();
		child.title = "UWI Old Library";
		item.items.add(child);

//		child = new ChildItem();
//		child.title = "Clubs";
//		item.items.add(child);

		items.add(item);
//   ------------------------------
		item = new GroupItem();
		item.title = "Banks/ATM";
		item.icon = R.string.material_icon_dollar;

		child = new ChildItem();
		child.title = "All";
		item.items.add(child);

		child = new ChildItem();
		child.title = "EduCom Credit Union Branch";
		item.items.add(child);

		child = new ChildItem();
		child.title = "ScotiaBank UWI-Branch";
		item.items.add(child);

		child = new ChildItem();
		child.title = "JNBS UWI-Branch";
		item.items.add(child);

		child = new ChildItem();
		child.title = "NCB ATM";
		item.items.add(child);

		child = new ChildItem();
		child.title = "NCB UWI-Branch";
		item.items.add(child);

//		child = new ChildItem();
//		child.title = "All";
//		item.items.add(child);

		items.add(item);
//   ------------------------------


		return items;
	}

	private class ExampleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
		private LayoutInflater inflater;

		private List<GroupItem> items;

		public ExampleAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void setData(List<GroupItem> items) {
			this.items = items;
		}

		@Override
		public ChildItem getChild(int groupPosition, int childPosition) {
			return items.get(groupPosition).items.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getRealChildView(int groupPosition, int childPosition,
									 boolean isLastChild, View convertView, ViewGroup parent) {
			ChildHolder holder;
			ChildItem item = getChild(groupPosition, childPosition);
			if (convertView == null) {
				holder = new ChildHolder();
				convertView = inflater.inflate(
						R.layout.list_item_expandable_travel_child, parent,
						false);
				holder.title = (TextView) convertView
						.findViewById(R.id.textTitle);
				convertView.setTag(holder);
			} else {
				holder = (ChildHolder) convertView.getTag();
			}

			holder.title.setText(item.title);

			return convertView;
		}

		@Override
		public int getRealChildrenCount(int groupPosition) {
			return items.get(groupPosition).items.size();
		}

		@Override
		public GroupItem getGroup(int groupPosition) {
			return items.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return items.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			GroupHolder holder;
			GroupItem item = getGroup(groupPosition);
			if (convertView == null) {
				holder = new GroupHolder();
				convertView = inflater.inflate(
						R.layout.list_item_expandable_travel, parent, false);
				holder.title = (TextView) convertView
						.findViewById(R.id.list_item_expandable_travel_textTitle);
				holder.icon = (TextView) convertView
						.findViewById(R.id.list_item_expandable_travel_icon);
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}

			holder.title.setText(item.title);
			holder.icon.setText(item.icon);
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}
	}
}
