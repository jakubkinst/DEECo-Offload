package cz.kinst.jakub.diploma.offloading.deeco;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.kinst.jakub.diploma.offloading.logger.Logger;
import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast;
import cz.kinst.jakub.diploma.udpbroadcast.UDPRuntimeBuilder;

/**
 * Created by jakubkinst on 23/01/15.
 */
public class DEECoManager {

    private final UDPRuntimeBuilder mBuilder;
    private final RuntimeMetadata mModel;
    private UDPBroadcast mUdpBroadcast;
    private RuntimeFramework mDEECoRuntime;
    private boolean mRunning;
    private AnnotationProcessor mProcessor;

    public DEECoManager(UDPBroadcast udpBroadcast) {
        this.mUdpBroadcast = udpBroadcast;

        mBuilder = new UDPRuntimeBuilder();
        mModel = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
        mProcessor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, mModel, new CloningKnowledgeManagerFactory());
    }

    public void initRuntime() {
        try {
            Logger.i("DEECo Runtime initialized. IP:" + mUdpBroadcast.getMyIpAddress());
            mDEECoRuntime = mBuilder.build(mUdpBroadcast.getMyIpAddress(), mModel, mUdpBroadcast);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
        }
    }

    public void registerComponent(Object component) {
        try {
            mProcessor.process(component);
            Logger.i("DEECo component " + component.getClass().getName() + " registered.");
        } catch (AnnotationProcessorException e) {
            e.printStackTrace();
        }
    }

    public void registerEnsemble(Class ensemble) {
        try {
            mProcessor.process(ensemble);
            Logger.i("DEECo ensemble " + ensemble.getName() + " registered.");
        } catch (AnnotationProcessorException e) {
            e.printStackTrace();
        }
    }

    public void startRuntime() {
        mUdpBroadcast.startReceivingInBackground();
        mDEECoRuntime.start();
        mRunning = true;
        Logger.i("DEECo runtime started.");
    }

    public void stopRuntime() {
        mUdpBroadcast.stopReceivingInBackground();
        mDEECoRuntime.stop();
        mRunning = false;
        Logger.i("DEECo runtime stopped.");
    }

    public boolean isRunning() {
        return mRunning;
    }
}