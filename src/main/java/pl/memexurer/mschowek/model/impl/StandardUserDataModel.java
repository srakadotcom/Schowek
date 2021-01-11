package pl.memexurer.mschowek.model.impl;

import pl.memexurer.mschowek.model.UserDataModel;

import java.util.Map;

public class StandardUserDataModel implements UserDataModel {
    protected final Map<Integer, Integer> itemCount;

    public StandardUserDataModel(Map<Integer, Integer> itemCountMap) {
        this.itemCount = itemCountMap;
    }

    @Override
    public void setItemCount(int id, int i) {
        itemCount.put(id, i);
    }

    @Override
    public int getItemCount(int id) {
         return itemCount.getOrDefault(id, 0);
    }
}
