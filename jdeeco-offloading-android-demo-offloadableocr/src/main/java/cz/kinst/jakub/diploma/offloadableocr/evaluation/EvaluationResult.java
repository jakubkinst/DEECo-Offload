package cz.kinst.jakub.diploma.offloadableocr.evaluation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public class EvaluationResult {
	float mBatteryPercentConsumed = 0;
	List<Long> mDurations = new ArrayList<>();
	String mExecutionHost;
	long mTotalTime;


	public EvaluationResult(String mExecutionHost) {
		this.mExecutionHost = mExecutionHost;
	}


	public float getBatteryPercentConsumed() {
		return mBatteryPercentConsumed;
	}


	public void setBatteryPercentConsumed(float mBatteryPercentConsumed) {
		this.mBatteryPercentConsumed = mBatteryPercentConsumed;
	}


	public List<Long> getDurations() {
		return mDurations;
	}


	public void setDurations(List<Long> mExecutionTimes) {
		this.mDurations = mExecutionTimes;
	}


	public long getTotalTime() {
		return mTotalTime;
	}


	public void setTotalTime(long mTotalTime) {
		this.mTotalTime = mTotalTime;
	}


	public String getExecutionHost() {
		return mExecutionHost;
	}


	public void setExecutionHost(String mExecutionHost) {
		this.mExecutionHost = mExecutionHost;
	}
}
