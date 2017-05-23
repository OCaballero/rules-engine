package com.oliver.spark_drools;

import org.jbpm.compiler.xml.XmlRuleFlowProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;

public class RuleCompiler {

	public static void main(String[] args) {
		RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.drools.HelloWorldJoinSplit");

		factory
				// Header
				.name("HelloWorldJoinSplit").version("1.0").packageName("org.drools")
				// Nodes
				.startNode(1).name("Start").done().splitNode(2).name("Split").type(Split.TYPE_AND).done().actionNode(3)
				.name("Action 1").action("mvel", "System.out.println(\"Inside Action 1\")").done().actionNode(4)
				.name("Action 2").action("mvel", "System.out.println(\"Inside Action 2\")").done().joinNode(5)
				.type(Join.TYPE_AND).done().endNode(6).name("End").done()
				// Connections
				.connection(1, 2).connection(2, 3).connection(2, 4).connection(3, 5).connection(4, 5).connection(5, 6);
		RuleFlowProcess process = factory.validate().getProcess();

		String newXml = XmlRuleFlowProcessDumper.INSTANCE.dump(process);
		System.out.println(newXml);

	}
}
