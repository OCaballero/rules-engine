package com.oliver.spark_drools.utils;

import org.apache.log4j.Logger;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

public class DroolsRuleEventListener implements RuleRuntimeEventListener {

	private static Logger log = Logger.getLogger(DroolsRuleEventListener.class);

	public void objectInserted(ObjectInsertedEvent event) {
		log.info("inserted " + event);

	}

	public void objectUpdated(ObjectUpdatedEvent event) {
		log.info("updated " + event);

	}

	public void objectDeleted(ObjectDeletedEvent event) {
		log.info("deleted " + event);

	}

}
