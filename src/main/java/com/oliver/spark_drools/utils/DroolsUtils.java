package com.oliver.spark_drools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

import com.oliver.spark_drools.bo.Message;

public class DroolsUtils {

	private static KieSession kieSession;

	private static StatelessKieSession kieStatelessSession;
	private static int version = 0;
	private static Logger log = Logger.getLogger(DroolsUtils.class);

	public static void chargeRules(ListRules newRules) throws IOException {

		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

		Resource resource = null;
		for (DroolsRule newRule : newRules.getRules()) {
			// carga del HDFS o HBASE
			resource = ResourceFactory.newFileResource(newRule.getPath());
			if (resource != null) {
				ResourceType type = ResourceType.getResourceType(newRule.getType());

				if (type.equals(ResourceType.DTABLE)) {
					DecisionTableConfiguration config = KnowledgeBuilderFactory.newDecisionTableConfiguration();

					if (newRule.getTemplatePath() != null && !newRule.getTemplatePath().isEmpty()
							&& newRule.getTemplateStartCol() != -1 && newRule.getTemplateStartRow() != -1) {

						// carga del HDFS o HBASE
						Resource resourceConfig = ResourceFactory.newFileResource(newRule.getTemplatePath());
						if (resourceConfig != null) {
							config.addRuleTemplateConfiguration(resourceConfig, newRule.getTemplateStartRow(),
									newRule.getTemplateStartCol());
							compileXLSWithTemplate(resource.getInputStream(), resourceConfig.getInputStream(),
									newRule.getTemplateStartRow(), newRule.getTemplateStartCol());
						}

					} else {
						compileXLS(resource.getInputStream());
					}
					knowledgeBuilder.add(resource, type, config);
				} else {
					knowledgeBuilder.add(resource, type);
				}

			}
		}

		if (knowledgeBuilder.hasErrors()) {
			log.error(knowledgeBuilder.getErrors().toString());
			return;
		}

		KieBase kieBase = knowledgeBuilder.newKnowledgeBase();
		kieStatelessSession = kieBase.newStatelessKieSession();
		kieSession = kieBase.newKieSession();

		version = newRules.getVersion();

		List<String> idProcesses = new ArrayList<String>();
		for (Process process : kieBase.getProcesses()) {
			kieSession.startProcess(process.getId());
			kieStatelessSession.execute(CommandFactory.newStartProcess(process.getId()));
		}

	}

	public static int getVersion() {
		return version;
	}

	public static void setVersion(int version) {
		DroolsUtils.version = version;
	}

	// cambiar por llamada a HBASE
	public static void updateRules(String pathConfig) {
		BufferedReader br = null;
		try {
			File archivo = new File(pathConfig);
			FileReader fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			String linea = br.readLine();
			int version = Integer.parseInt(linea);
			if (DroolsUtils.getVersion() < version) {
				List<DroolsRule> list = new ArrayList<DroolsRule>();
				while ((linea = br.readLine()) != null) {

					String[] words = linea.split(" ");

					if (words.length < 2) {
						throw new RuntimeException("No tiene dos variables");
					}

					DroolsRule rule = new DroolsRule();
					rule.setType(words[0]);
					rule.setPath(words[1]);
					if (words.length == 5) {
						rule.setTemplatePath(words[2]);
						rule.setTemplateStartRow(Integer.parseInt(words[3]));
						rule.setTemplateStartCol(Integer.parseInt(words[4]));
					}

					list.add(rule);

				}
				chargeRules(new ListRules(list, version));
			}
			br.close();

		} catch (FileNotFoundException e) {
			log.error("Archivo de configuracion no encontrado", e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}

	}

	public static void fireRules(List<Message> myList, String config) {

		DroolsUtils.updateRules(config);

		if (kieSession != null) {
			for (Message droolsMessage : myList) {
				kieSession.insert(droolsMessage);
			}

			if (kieSession.getProcessInstances().size() == 0) {
				kieSession.fireAllRules();
				// wait???
			}

		}

	}

	public static void fireStatelessRules(List<Message> myList, String config) {

		DroolsUtils.updateRules(config);

		if (kieStatelessSession != null) {
			List<Command<Message>> cmds = new ArrayList<Command<Message>>();
			for (Message droolsMessage : myList) {
				cmds.add(CommandFactory.newInsert(droolsMessage));
			}
			kieStatelessSession.execute(CommandFactory.newBatchExecution(cmds));
		}

	}

	public static void compileXLS(InputStream xls) {
		SpreadsheetCompiler converter = new SpreadsheetCompiler();
		String drl = converter.compile(xls, InputType.XLS);
		log.info(drl);
		System.out.println(drl);
	}

	public static void compileXLSWithTemplate(InputStream xls, InputStream template, int startRow, int startCol) {
		ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
		String drl = converter.compile(xls, template, startRow, startCol);
		log.info(drl);
		System.out.println(drl);
	}

}
