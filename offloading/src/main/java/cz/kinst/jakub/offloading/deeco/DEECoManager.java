package cz.kinst.jakub.offloading.deeco;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.kinst.jakub.offloading.deeco.components.DeviceComponent;
import cz.kinst.jakub.diploma.udpbroadcast.UDPBroadcast;
import cz.kinst.jakub.diploma.udpbroadcast.UDPRuntimeBuilder;
import cz.kinst.jakub.offloading.logger.Logger;

/**
 * Created by jakubkinst on 23/01/15.
 */
public class DEECoManager {

    private UDPBroadcast mUdpBroadcast;
    private RuntimeFramework mDEECoRuntime;
    private boolean mRunning;
    private AnnotationProcessor mProcessor;

    public DEECoManager(UDPBroadcast udpBroadcast) {
        this.mUdpBroadcast = udpBroadcast;
    }

    public void initRuntime() {
        try {
            UDPRuntimeBuilder builder = new UDPRuntimeBuilder();

            RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
            mProcessor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model, new CloningKnowledgeManagerFactory());

            // addComponents and ensembles
            // TODO: move to upper layer if it is possible to do this after builder.build()
            registerComponent(new DeviceComponent(mUdpBroadcast.getMyIpAddress()));

            Logger.i("DEECo Runtime initialized. IP:" + mUdpBroadcast.getMyIpAddress());
            mDEECoRuntime = builder.build(mUdpBroadcast.getMyIpAddress(), model, mUdpBroadcast);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
        }
    }

    public void registerComponent(Object component) {
        try {
            mProcessor.process(component);
            Logger.i("DEECo component " + component.getClass().getName() + "registered.");
        } catch (AnnotationProcessorException e) {
            e.printStackTrace();
        }
    }

    public void registerEnsemble(Class ensemble) {
        try {
            mProcessor.process(ensemble);
            Logger.i("DEECo ensemble " + ensemble.getName() + "registered.");
        } catch (AnnotationProcessorException e) {
            e.printStackTrace();
        }
    }

    public void startRuntime() {
        mUdpBroadcast.startReceiving();
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
