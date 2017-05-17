package com.oliver.spark_drools.utils;

public class DroolsRule {
	private String type;
	private String path;
	private String templatePath;
	private int templateStartRow = -1;
	private int templateStartCol = -1;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public int getTemplateStartRow() {
		return templateStartRow;
	}

	public void setTemplateStartRow(int templateStartRow) {
		this.templateStartRow = templateStartRow;
	}

	public int getTemplateStartCol() {
		return templateStartCol;
	}

	public void setTemplateStartCol(int templateStartCol) {
		this.templateStartCol = templateStartCol;
	}

}
