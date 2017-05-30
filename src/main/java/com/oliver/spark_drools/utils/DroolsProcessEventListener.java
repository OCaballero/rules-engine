package com.oliver.spark_drools.utils;

import org.apache.log4j.Logger;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

public class DroolsProcessEventListener implements ProcessEventListener {
	
	private static Logger log = Logger.getLogger(DroolsProcessEventListener.class);

	public void beforeProcessStarted(ProcessStartedEvent event) {
		log.info("beforeProcessStarted " + event);

	}

	public void afterProcessStarted(ProcessStartedEvent event) {
		log.info("afterProcessStarted " + event);

	}

	public void beforeProcessCompleted(ProcessCompletedEvent event) {
		log.info("beforeProcessCompleted " + event);

	}

	public void afterProcessCompleted(ProcessCompletedEvent event) {
		log.info("afterProcessCompleted " + event);
	}

	public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
		log.info("beforeNodeTriggered " + event);
	}

	public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
		log.info("afterNodeTriggered " + event);
	}

	public void beforeNodeLeft(ProcessNodeLeftEvent event) {
		log.info("beforeNodeLeft " + event);
	}

	public void afterNodeLeft(ProcessNodeLeftEvent event) {
		log.info("afterNodeLeft " + event);
	}

	public void beforeVariableChanged(ProcessVariableChangedEvent event) {
		log.info("beforeVariableChanged " + event);
	}

	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		log.info("afterVariableChanged " + event);
	}

}
