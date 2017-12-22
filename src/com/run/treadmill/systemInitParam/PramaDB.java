package com.run.treadmill.systemInitParam;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class PramaDB extends DataSupport {

	@Column(unique = true)
	private int id;

	private String name;

	private String value;

	public PramaDB() {
		super();
	}

	public PramaDB(int id, String value) {
		this.id = id;
		this.value = value;
	}

	public PramaDB(String id, String value) {
		this.id = Integer.parseInt(id);
		this.value = value;
	}

	public PramaDB(String id, String name, String value) {
		this.id = Integer.parseInt(id);
		this.name = name;
		this.value = value;
	}

	public String getId() {
		return String.valueOf(id);
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(int value) {
		this.value = String.valueOf(value);
	}

	public String getValue() {
		return value;
	}

	public int getValueInt() {
		return Integer.parseInt(value);
	}

}
