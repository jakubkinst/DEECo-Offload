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
public class NFPDataHolder implements Serializable {
    private HashMap<String, HashMap<String, NFPData>> nfpData = new HashMap<>();

    public void put(String backendId, String deviceIp, NFPData nfpData) {
        HashMap<String, NFPData> backendData = getByBackendId(backendId);
        if (backendData == null)
            backendData = new HashMap<>();
        nfpData.setTimestamp(System.currentTimeMillis());
        backendData.put(deviceIp, nfpData);
        this.nfpData.put(backendId, backendData);
    }

    public HashMap<String, NFPData> getByBackendId(String backendId) {
        return nfpData.get(backendId);
    }

    /**
     * Returns filtered NFPData for devices implementing backendId,
     * but only those, that are not older than OffloadingConfig.NFP_DATA_COLLECTING_INTERVAL_MS
     *
     * @param backendId
     * @return
     */
    public HashMap<String, NFPData> getActiveByBackendId(String backendId) {
        HashMap<String, NFPData> data = new HashMap<>();
        for (Map.Entry<String, NFPData> entry : getByBackendId(backendId).entrySet()) {
            long now = new Date().getTime();
            if (now - entry.getValue().getTimestamp() < (2 * OffloadingConfig.NFP_DATA_COLLECTING_INTERVAL_MS)) {
                data.put(entry.getKey(), entry.getValue());
            }
        }
        return data;
    }

    public Set<String> getBackendIds() {
        return nfpData.keySet();
    }
}
