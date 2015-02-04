package cz.kinst.jakub.diploma.offloading.deeco.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.kinst.jakub.diploma.offloading.OffloadingConfig;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class NfpDataHolder implements Serializable {
    private HashMap<String, HashMap<String, NFPDataWithLastActiveTime>> nfpData = new HashMap<>();

    public void put(String appComponentId, String deviceIp, NFPData nfpData) {
        HashMap<String, NFPDataWithLastActiveTime> appComponentData = getByAppComponentId(appComponentId);
        if (appComponentData == null)
            appComponentData = new HashMap<>();
        appComponentData.put(deviceIp, new NFPDataWithLastActiveTime(nfpData, new Date().getTime()));
        this.nfpData.put(appComponentId, appComponentData);
    }

    public HashMap<String, NFPDataWithLastActiveTime> getByAppComponentId(String appComponentId) {
        return nfpData.get(appComponentId);
    }

    /**
     * Returns filtered NFPData for devices implementing appComponentId,
     * but only those, that are not older than OffloadingConfig.NFP_DATA_COLLECTING_INTERVAL_MS
     *
     * @param appComponentId
     * @return
     */
    public HashMap<String, NFPData> getActiveByAppComponentId(String appComponentId) {
        HashMap<String, NFPData> data = new HashMap<>();
        for (Map.Entry<String, NFPDataWithLastActiveTime> entry : getByAppComponentId(appComponentId).entrySet()) {
            long now = new Date().getTime();
            if (now - entry.getValue().getLastActiveTime() < (2 * OffloadingConfig.NFP_DATA_COLLECTING_INTERVAL_MS)) {
                data.put(entry.getKey(), entry.getValue().getNfpData());
            }
        }
        return data;
    }

    public Set<String> getAppComponentIds() {
        return nfpData.keySet();
    }
}
