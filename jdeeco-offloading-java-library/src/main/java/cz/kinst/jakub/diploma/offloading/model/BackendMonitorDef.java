package cz.kinst.jakub.diploma.offloading.model;

import java.io.Serializable;

/**
 * Definition of a backend monitor (currently just backend id)
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class BackendMonitorDef implements Serializable {
	private final String mBackendId;


	public BackendMonitorDef(String backendId) {
		mBackendId = backendId;
	}


	public String getBackendId() {
		return mBackendId;
	}
}
