package cz.kinst.jakub.diploma.offloading.deeco.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class NfpDataHolder implements Serializable {
    private HashMap<String, HashMap<String, NFPData>> map = new HashMap<>();

    public void put(String appComponentId, String deviceIp, NFPData nfpData) {
        HashMap<String, NFPData> appComponentData = getByAppComponentId(appComponentId);
        if (appComponentData == null)
            appComponentData = new HashMap<>();
        appComponentData.put(deviceIp, nfpData);
        map.put(appComponentId, appComponentData);
    }

    public HashMap<String, NFPData> getByAppComponentId(String appComponentId) {
        return map.get(appComponentId);
    }

    public Set<String> getAppComponentIds() {
        return map.keySet();
    }
}
