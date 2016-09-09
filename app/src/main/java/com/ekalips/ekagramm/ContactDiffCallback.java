package com.ekalips.ekagramm;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by ekalips on 9/8/16.
 */

public class ContactDiffCallback extends DiffUtil.Callback {

    private List<InstagramMedia> mOldList;
    private List<InstagramMedia> mNewList;

    public ContactDiffCallback(List<InstagramMedia> oldList, List<InstagramMedia> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }
    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // add a unique ID property on Contact and expose a getId() method
        return mOldList.get(oldItemPosition).getMediaID().equals(mNewList.get(newItemPosition).getMediaID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        InstagramMedia oldContact = mOldList.get(oldItemPosition);
        InstagramMedia newContact = mNewList.get(newItemPosition);

        return oldContact.getUsername() == newContact.getUsername() && oldContact.getDescription() == newContact.getDescription();
    }
}