package cz.kinst.jakub.diploma.offloading;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class StateBundle implements Serializable {
    private Map<String, Object> mMap = new HashMap<>();

    public StateBundle() {
    }

    public void putInt(String key, int value) {
        mMap.put(key, String.valueOf(value));
    }

    public void putString(String key, String value) {
        mMap.put(key, value);
    }

    public void putLong(String key, long value) {
        mMap.put(key, String.valueOf(value));
    }

    public void putDouble(String key, double value) {
        mMap.put(key, String.valueOf(value));
    }

    public void putSerializable(String key, Serializable value) {
        mMap.put(key, new Gson().toJson(value));
    }

    public int getInt(String key, int defaultValue) {
        return mMap.containsKey(key) ? Integer.parseInt((String) mMap.get(key)) : defaultValue;
    }

    public String getString(String key, String defaultValue) {
        return mMap.containsKey(key) ? (String) mMap.get(key) : defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        return mMap.containsKey(key) ? Long.parseLong((String) mMap.get(key)) : defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        return mMap.containsKey(key) ? Double.parseDouble((String) mMap.get(key)) : defaultValue;
    }

    public <T> T getSerializable(String key, Class<T> objectClass) {
        return new Gson().fromJson((String) mMap.get(key), objectClass);
    }

    public Map<String, Object> getMap() {
        return mMap;
    }
}
