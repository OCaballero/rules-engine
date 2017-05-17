package com.oliver.spark_drools.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListRules implements Serializable {

	private List<DroolsRule> rules;
	private int version;

	public ListRules() {
		rules = new ArrayList<DroolsRule>();
		version = 0;
	}

	public ListRules(List<DroolsRule> _rules, int _version) {
		rules = _rules;
		version = _version;
	}

	public List<DroolsRule> getRules() {
		return rules;
	}

	public void setRules(List<DroolsRule> rules) {
		this.rules = rules;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
