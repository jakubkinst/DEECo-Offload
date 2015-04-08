package cz.kinst.jakub.diploma.udpbroadcast;

import java.util.Random;

import cz.cuni.mff.d3s.deeco.DeecoProperties;
import cz.cuni.mff.d3s.deeco.executor.Executor;
import cz.cuni.mff.d3s.deeco.executor.SameThreadExecutor;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerContainer;
import cz.cuni.mff.d3s.deeco.knowledge.KnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.TimeTriggerExt;
import cz.cuni.mff.d3s.deeco.network.KnowledgeDataManager;
import cz.cuni.mff.d3s.deeco.network.PublisherTask;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFrameworkImpl;
import cz.cuni.mff.d3s.deeco.scheduler.SingleThreadedScheduler;
import cz.kinst.jakub.diploma.udpbroadcast.jdeecoextension.NonRebroadcastingKnowledgeDataManager;
import cz.kinst.jakub.diploma.udpbroadcast.jdeecoextension.UDPBroadcastHost;

/**
 * Builder for the {@link cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework} equipped with
 * the UDP broadcast knowledge cloning capability.
 * It creates the builder using {@link #build(String, cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata, UDPBroadcast)}
 * method by using {@link cz.kinst.jakub.diploma.udpbroadcast.jdeecoextension.UDPBroadcastHost} and custom {@link cz.kinst.jakub.diploma.udpbroadcast.jdeecoextension.NonRebroadcastingKnowledgeDataManager}
 * <p/>
 * The rest is the same as standard JDEECo runtime initialization.
 * <p/>
 * ---------------------------
 * Created by Jakub Kinst 2014
 * E-mail: jakub@kinst.cz
 */
public class UDPBroadcastRuntimeBuilder {


	/**
	 * Build the UDP broadcast capable {@link cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework}
	 *
	 * @param ipAddress    local host's IP address
	 * @param model        runtime model containing Components and Ensemble definitions
	 * @param udpBroadcast UDP broadcast provider
	 * @return JDEECo runtime framework
	 */
	public RuntimeFramework build(String ipAddress, RuntimeMetadata model, UDPBroadcast udpBroadcast) {
		if (model == null) {
			throw new IllegalArgumentException("Model must not be null");
		}

		// create UDP broadcast-capable host for knowledge cloning
		UDPBroadcastHost host = new UDPBroadcastHost(ipAddress, udpBroadcast);

		// knowledge manager must not be rebroadcasting knowledge data
		KnowledgeDataManager knowledgeDataManager = new NonRebroadcastingKnowledgeDataManager();

		KnowledgeManagerFactory knowledgeManagerFactory = new CloningKnowledgeManagerFactory();

		// Set up the executor
		Executor executor = new SameThreadExecutor();

		// Set up the simulation scheduler
		SingleThreadedScheduler scheduler = new SingleThreadedScheduler();
		scheduler.setExecutor(executor);

		// Set up the host container
		KnowledgeManagerContainer container = new KnowledgeManagerContainer(knowledgeManagerFactory);
		knowledgeDataManager.initialize(container, host.getKnowledgeDataSender(), host.getHostId(), scheduler);
		host.setKnowledgeDataReceiver(knowledgeDataManager);
		// Set up the publisher task
		TimeTriggerExt publisherTrigger = new TimeTriggerExt();
		publisherTrigger.setPeriod(Integer.getInteger(
				DeecoProperties.PUBLISHING_PERIOD,
				PublisherTask.DEFAULT_PUBLISHING_PERIOD));
		long seed = 0;
		for (char c : host.getHostId().toCharArray())
			seed = seed * 32 + (c - 'a');
		Random rnd = new Random(seed);
		publisherTrigger.setOffset(rnd.nextInt((int) publisherTrigger
				.getPeriod()) + 1);
		PublisherTask publisherTask = new PublisherTask(scheduler, knowledgeDataManager,
				publisherTrigger, host.getHostId());

		// Add publisher task to the scheduler
		scheduler.addTask(publisherTask);

		return new RuntimeFrameworkImpl(model, scheduler, executor, container);
	}
}
