package cz.kinst.jakub.diploma.offloading.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.kinst.jakub.diploma.offloading.utils.OffloadingConfig;

/**
 * Wrapper around {@link java.util.HashMap} to store {@link cz.kinst.jakub.diploma.offloading.model.NFPData}
 * for multiple backends from multiple devices
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class NFPDataHolder implements Serializable {
    private HashMap<String, HashMap<String, NFPData>> nfpData = new HashMap<>();

    /**
     * Save NFPData measured on device with deviceIp for backend with backendId
     *
     * @param backendId Backend ID
     * @param deviceIp  Device IP address
     * @param nfpData   NFPData
     */
    public void put(String backendId, String deviceIp, NFPData nfpData) {
        HashMap<String, NFPData> backendData = getByBackendId(backendId);
        if (backendData == null)
            backendData = new HashMap<>();
        nfpData.setTimestamp(System.currentTimeMillis());
        backendData.put(deviceIp, nfpData);
        this.nfpData.put(backendId, backendData);
    }

    /**
     * Returns NFPData by all devices for specific backend
     *
     * @param backendId Backend ID
     * @return
     */
    public HashMap<String, NFPData> getByBackendId(String backendId) {
        return nfpData.get(backendId);
    }

    /**
     * Returns filtered NFPData for devices implementing backendId,
     * but only those, that are not older than OffloadingConfig.NFP_DATA_COLLECTING_INTERVAL_MS
     *
     * @param backendId Backend ID
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

    /**
     * Get set of backend IDs from which we have at least one instance of NFPData
     *
     * @return Set of backend IDs
     */
    public Set<String> getBackendIds() {
        return nfpData.keySet();
    }
}
