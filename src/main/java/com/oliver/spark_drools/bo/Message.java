package com.oliver.spark_drools.bo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {

	private String id;

	private int numMessage;

	private Map<String, Object> input = new HashMap<String, Object>();

	private Map<String, Object> output = new HashMap<String, Object>();

	public Message(String id, int numMessage) {
		this.id = id;
		this.setNumMessage(numMessage);
	}

	public Message(String id, int numMessage, Map<String, Object> input) {
		this.id = id;
		this.setNumMessage(numMessage);
		this.input = input;
	}

	public Map<String, Object> getOutput() {
		return output;
	}

	public void setOutput(Map<String, Object> output) {
		this.output = output;
	}

	public Map<String, Object> getInput() {
		return input;
	}

	public void setInput(Map<String, Object> input) {
		this.input = input;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void addOutput(String key, Object value) {
		output.put(key, value);
	}

	public Object getInput(String key) {
		return input.get(key);
	}

	public int getNumMessage() {
		return numMessage;
	}

	public void setNumMessage(int numMessage) {
		this.numMessage = numMessage;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", numMessage=" + numMessage + ", input=" + input + ", output=" + output + "]";
	}

	
}
