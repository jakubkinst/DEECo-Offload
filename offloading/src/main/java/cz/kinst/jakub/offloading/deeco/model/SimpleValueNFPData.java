package cz.kinst.jakub.offloading.deeco.model;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class SimpleValueNFPData extends NFPData{
    private float performance;

    public SimpleValueNFPData(float performance) {
        this.performance = performance;
    }

    public float getPerformance() {
        return performance;
    }
}
