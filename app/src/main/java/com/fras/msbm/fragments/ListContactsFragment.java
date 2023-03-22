package com.fras.msbm.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fras.msbm.R;
import com.fras.msbm.activities.general.ContactDetailActivity;
import com.fras.msbm.adapter.DirectoryAdapter;
import com.fras.msbm.adapter.recycler.ContactAdapter;
import com.fras.msbm.adapter.recycler.SimpleSectionedRecyclerViewAdapter;
import com.fras.msbm.events.clicks.ContactCallEvent;
import com.fras.msbm.events.clicks.ContactDetailEvent;
import com.fras.msbm.models.directory.Contact;
import com.fras.msbm.models.directory.DirectoryEntry;
import com.fras.msbm.views.recyclers.SimpleDividerItemDecoration;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.Subscribe;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 7/7/2016.
 */

public class ListContactsFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    public static final String TAG = ListContactsFragment.class.getSimpleName();
    private final int CALL_PERMISSION = 123;

    @BindView(R.id.recycler_contacts) RecyclerView contactsRecyclerView;

    private ContactAdapter contactAdapter;
    private String tempNum;
    private ProgressDialog pDialog;

    ArrayList<DirectoryEntry> directoryArray;
    private List<DirectoryEntry> sortDirList;
    private List<Contact> sortConList;

    private final String USER_AGENT = "Mozilla/5.0";

    List<Contact> entries = new ArrayList<>();

    DirectoryEntry[] dirArray;
    Contact[] conArray;
    private DirectoryAdapter mDirectoryAdapter;

    List<SimpleSectionedRecyclerViewAdapter.Section> sections = new ArrayList<>();

    private static String POST_API = "http://mobile.antoniofearon.com/api/v1/directory.php";

    public ListContactsFragment() {
        // needed ...
    }

    public static ListContactsFragment newInstance() {
        return new ListContactsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_contacts, container, false);
        ButterKnife.bind(this, rootView);
        setToolbarTitle("Directory");
        setupViews();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contacts, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        Log.e(TAG, "Got SearchView");
        if (searchView != null) {
            Log.e(TAG,"SearchView Binded");
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        Log.e(TAG,"Query Change Activated....");
        contactAdapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e(TAG,"Query Submit Activated....");
        contactAdapter.getFilter().filter(query);
        return true;
    }

    private void setupViews() {
//        final List<Contact> contacts;// = loadStaticContacts();
        new LoadDirectory().execute();
        contactAdapter = new ContactAdapter(getContext(), entries);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        contactsRecyclerView.setAdapter(contactAdapter);
//        contactsRecyclerView.setHasFixedSize(true);

    }

    private void setupDividers(){
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Quick Contacts"));

        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(7,"Faculty Numbers"));

        Log.e(TAG,"Session size:" + Integer.toString(sections.size()));

        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(getActivity(),R.layout.section, R.id.section_text,contactAdapter);

        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        mSectionedAdapter.setSections(sections.toArray(dummy));

        contactsRecyclerView.setAdapter(mSectionedAdapter);
    }

    private List<Contact> loadStaticContacts() {
        return new ArrayList<>(Arrays
                .asList(new Contact("MSBM-N Reception", "876-977-6035"),
                        new Contact("MSBM-S Reception", "876-977-3775"),
                        new Contact("Western Jamaica Campus", "876-940-5561"),
                        new Contact("UWI Operator", "876-927-1660"),
                        new Contact("Mona Campus Security", "876-935-8748"),
                        new Contact("Office of Graduate Studies & Research", "876-935-8263"),
                        new Contact("Student Records", "876-970-4472")));
    }

    @Subscribe
    public void onEventMainThread(ContactDetailEvent event) {
        final Contact contact = event.getContact();
        Toast.makeText(getActivity(), contact.getPhone(), Toast.LENGTH_SHORT)
                .show();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("Options")
                .setItems(new String[]{"Copy to Clipboard", "Call", "View Details"}, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            final ClipboardManager clipboard = (ClipboardManager)
                                    getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            final ClipData clipData = ClipData.newPlainText(contact.getName(), contact.getPhone());
                            clipboard.setPrimaryClip(clipData);
                            break;
                        case 1:
                            createCallIntent(contact.getPhone());
                            break;
                        case 2:
                            Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
                            intent.putExtra("TITLE", contact.getJobtitle());
                            intent.putExtra("NAME", contact.toString());
                            intent.putExtra("UNIT", contact.getUnit());
                            intent.putExtra("EMAIL", contact.getEmail());
                            intent.putExtra("OFFICE", contact.getOffice());
                            intent.putExtra("NUMBER", contact.getPhone());
                            Toast.makeText(getActivity(), contact.getPhone(), Toast.LENGTH_SHORT)
                                    .show();
                            startActivity(intent);
                            break;
                    }
                }).show();
    }

    @Subscribe
    public void onEvenMainThread(ContactCallEvent event) {
        final Contact contact = event.getContact();
        createCallIntent(contact.getPhone());
    }

    private void createCallIntent(String number) {
        tempNum = number;
        Log.e("NUMBER",number);
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
//
//                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
//                alertBuilder.set
//
//            }

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION);

        } else if(!Strings.isNullOrEmpty(number)) {
            sendCallIntent(number);
        }else{
            Toast.makeText(getContext(), "Contact Number Out of Service", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendCallIntent(String number) {
        final String uri = "tel:" + number;
        final Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(uri));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendCallIntent(tempNum);
                    Log.i(TAG, "onRequestPermission:here");
                } else {
                    Log.i(TAG, "onRequestPermission:revoked");
                    Toast.makeText(getContext(), "Permission to make call denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadDirectory extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading contacts. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
//            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
            Log.e("Start  ","Start Background");
//            try{
//                sortDirList = DirectoryEntry.listAll(DirectoryEntry.class);
//                if(!sortDirList.isEmpty())
//                    DirectoryEntry.deleteAll(DirectoryEntry.class);
//                Log.e("XXXXX  ","Works");
//            }catch(Exception e){
//                Log.e("XXXXX","Exception thrown");
//            }
//            sortDirList = DirectoryEntry.listAll(DirectoryEntry.class);
            try {
//                directoryResponse = new DirectoryResponse();
                Log.e("Start  ","Test 1 ---------------------");
                URL url = new URL(POST_API);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                Log.e("Start  ","Test 2 ---------------------");
                //add request header
                urlConnection.setRequestProperty("User-Agent", USER_AGENT);
                Log.e("Start  ","Test 3 ---------------------");
                Integer responseCode = urlConnection.getResponseCode();
                Log.e("XYXYX",responseCode.toString());
                Log.e("Start  ","Test 4 ---------------");
//                Log.e("Start  ","Test 5 ---------------");
                Reader reader = new InputStreamReader(urlConnection.getInputStream());
//
                Gson gson = new GsonBuilder().create();
//
                Log.e("Size", "Threr");
//                dirArray = gson.fromJson(reader, DirectoryEntry[].class);
                conArray = gson.fromJson(reader, Contact[].class);
//
//                Log.e("Size", Integer.toString(dirArray.length));
                Log.e("Size^^^", Integer.toString(conArray.length));

//                Collections.sort(Arrays.asList(dirArray), DirectoryEntry.NameComparator);
                Collections.sort(Arrays.asList(conArray), Contact.NameComparator);

                Log.e("Test ", "After assign");
//
                if(responseCode == 200) {
                    Log.e("Data Returned","Get");

                } else {
                    Log.e("YYY  ",responseCode.toString());

                }

            }catch(MalformedURLException e) {
                e.printStackTrace();
                Log.e("XXXX","Mal");
            }catch(Exception e) {
                Log.e("XXXX","Except");
                e.printStackTrace();
            }

            try{ //check if directory has been updated
//                sortDirList = DirectoryEntry.listAll(DirectoryEntry.class);
                sortConList = Contact.listAll(Contact.class);
                Log.e(TAG, "conArray: " + Integer.toString(conArray.length));
                Log.e(TAG, "sortConList: " + sortConList.size());
                if((conArray != null) && (sortConList.size() != conArray.length)){
                    Log.e(TAG, "New List");
                    if(!sortConList.isEmpty()){
                        Log.e(TAG, "Wiping List");
                        Contact.deleteAll(Contact.class);
                    }
                    List<Contact> tempContact = loadStaticContacts();

                    tempContact.addAll(Arrays.asList(conArray));
                    Log.e(TAG, "tempContact: " + tempContact.size());
//                    for(Contact entry: loadStaticContacts()){
//                        Arrays.asList(conArray).add(0,entry);
//                    }

                    for(Contact entry: tempContact){
//                    Log.e("Check", entry.toString());
                        entry.save();
//                    Log.e("Saved", entry.lastname);
                    }
                }

            }catch (Exception e){
                Log.e(TAG, "I got an error", e);
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

//          Log.e("After", dirArray[1].lastname);
//          entries = Arrays.asList(dirArray);
            Log.e("XYXYX","iN POST Exce");
//            sortDirList = DirectoryEntry.listAll(DirectoryEntry.class);
            sortConList = Contact.listAll(Contact.class);
            entries = sortConList;
            Log.e(TAG,"Contact List: " + sortConList.size());
//            Log.e("Excecute", Integer.toString(sortConList.size()));
//            Log.e("Cehck", sortConList.get(0).toString());
//            Log.e("Cehck", sortConList.get(0).getName());
//            Log.e("Cehck", sortConList.get(0).getUnit());
//            Log.e("Cehck", sortConList.get(0).getEmail());


            getActivity().runOnUiThread(new Runnable() {
                public void run() {
//                    mDirectoryAdapter.update(sortDirList);
//                    mDirectoryAdapter.notifyDataSetChanged();
                    contactAdapter.update(sortConList);
                    contactAdapter.notifyDataSetChanged();
                    Log.e("Excecute", "Update contract Adapter");
                    setupDividers();
                    Log.e(TAG," Dividers Set");
                }
            });


            pDialog.dismiss();

        }

    }
}
