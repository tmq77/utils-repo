package cn.t.test.bean;

import java.io.Serializable;

public class Pojo implements Serializable{
	
	private static final long serialVersionUID = -304545525954457096L;

	private String name;
	
	private String id;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
