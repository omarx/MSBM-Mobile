package com.fras.msbm.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fras.msbm.R;
import com.fras.msbm.events.clicks.ContactCallEvent;
import com.fras.msbm.events.clicks.ContactDetailEvent;
import com.fras.msbm.models.directory.Contact;
import com.fras.msbm.models.directory.DirectoryEntry;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shane on 7/7/2016.
 */

public class ContactAdapter extends BaseAdapter<Contact, ContactAdapter.ContactViewHolder> {
    public static final String TAG = ContactAdapter.class.getSimpleName();
    private List<Contact> mDirectoryEntries;
    private List<Contact> filteredList;
    private CustomFilter mFilter;
    private LinkedHashSet<Contact> linkedHashSet;

    public ContactAdapter(Context context, List<Contact> entities) {
        super(context, entities);
        this.mDirectoryEntries = entities;
//        mFilter = new CustomFilter(this, );
        Log.e(TAG, "Binded");
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_contacts, parent, false);
        return new ContactViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
//        final Contact contact = getEntities().get(position);
        Contact contact = mDirectoryEntries.get(position);
//        Log.e(TAG, "Binded");
        holder.textViewName.setText(contact.getName());
        holder.textViewName.setOnClickListener(view -> {
            Log.i(TAG, "in textview on click");
            bus.post(new ContactDetailEvent(contact));
        });
        holder.buttonCall.setOnClickListener(view -> {
            Log.i(TAG, "in button on click");
            bus.post(new ContactCallEvent(contact));
        });
    }


    public Filter getFilter() {
        Log.e(TAG, "Filter Called");
        if (mFilter == null)
            mFilter = new CustomFilter(this, mDirectoryEntries);
        return mFilter;
    }

    @Override
    public int getItemCount() {
        return mDirectoryEntries.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relative_layout_container) RelativeLayout mRelativeLayoutContainer;
        @BindView(R.id.text_name) TextView textViewName;
        @BindView(R.id.button_call) Button buttonCall;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class CustomFilter extends Filter{
        private ContactAdapter mAdapter;
        private List<Contact> originalList;
        private CustomFilter(ContactAdapter mAdapter, List<Contact> entities) {
            super();
            this.mAdapter = mAdapter;
            this.originalList = entities;
            filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            linkedHashSet = new LinkedHashSet<>();
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                filteredList.clear();
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (Contact mWords : originalList) {
                    if (mWords.getFirstname().toLowerCase().startsWith(filterPattern) || mWords.getLastname().toLowerCase().startsWith(filterPattern)) {
                        Log.e(TAG, "Name Matched: " + mWords.getName());
                        if(!filteredList.contains(mWords))
                            filteredList.add(mWords);


                    }
                }
            }
            System.out.println("Count Number " + filteredList.size());
            Log.e(TAG,"Filtered: " + filteredList.size());
            linkedHashSet.addAll(filteredList);
            Log.e(TAG,"LinkedHash: " + linkedHashSet.size());
            filteredList.clear();
            filteredList.addAll(linkedHashSet);
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((List<Contact>) results.values).size());
            update((List<Contact>) results.values);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void update(List<Contact> directoryEntries){
        this.mDirectoryEntries = directoryEntries;
    }
}
