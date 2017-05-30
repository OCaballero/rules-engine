package com.oliver.spark_drools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

import com.oliver.spark_drools.bo.Message;

public class DroolsUtils {

	private static KieBase kieBase;

	private static CommandExecutor session;
	private static int version = 0;
	private static Logger log = Logger.getLogger(DroolsUtils.class);

	public static KieBase chargeRules(ListRules newRules) throws IOException {

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
			return null;
		}

		KieBase kieBase = knowledgeBuilder.newKnowledgeBase();

		return kieBase;

	}

	public static int getVersion() {
		return version;
	}

	public static void setVersion(int version) {
		DroolsUtils.version = version;
	}

	// cambiar por llamada a HBASE
	public static KieBase updateRules(String pathConfig) {
		BufferedReader br = null;
		try {
			File archivo = new File(pathConfig);
			FileReader fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			String linea = br.readLine();
			int newVersion = Integer.parseInt(linea);
			if (DroolsUtils.getVersion() < newVersion) {
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
				KieBase kie = chargeRules(new ListRules(list, newVersion));
				if (kie != null) {
					version = newVersion;
					return kie;
				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			log.error("Archivo de configuracion no encontrado ",e);
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

		return null;

	}

	public static void startDrools(List<Message> myList, String config, boolean statefull) {

		KieBase newKieBase = DroolsUtils.updateRules(config);

		if (newKieBase != null) {
			kieBase = newKieBase;
			if (statefull) {
				session = kieBase.newKieSession();
			} else {
				session = kieBase.newStatelessKieSession();
			}

			for (Process process : kieBase.getProcesses()) {
				session.execute(CommandFactory.newStartProcess(process.getId()));

			}
		}
		if (session != null) {
			List<Command<Message>> cmds = new ArrayList<Command<Message>>();
			for (Message droolsMessage : myList) {
				cmds.add(CommandFactory.newInsert(droolsMessage));
			}
			cmds.add(CommandFactory.newFireAllRules());
			session.execute(CommandFactory.newBatchExecution(cmds));
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
