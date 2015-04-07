package cz.kinst.jakub.diploma.offloading.model;

/**
 * NFPData carying only single float value
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class SingleValueNFPData extends NFPData {
	private float mPerformance;


	public SingleValueNFPData(float performance) {
		this.mPerformance = performance;
	}


	public float getPerformance() {
		return mPerformance;
	}


	@Override
	public String toString() {
		return String.valueOf(getPerformance());
	}
}
