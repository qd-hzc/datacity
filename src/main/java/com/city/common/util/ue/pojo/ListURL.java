package com.city.common.util.ue.pojo;

import java.util.List;

public class ListURL {

	private String state;
	private Integer total;
	private Integer start;
	private List<URL> list;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public List<URL> getList() {
		return list;
	}

	public void setList(List<URL> list) {
		this.list = list;
	}

}
