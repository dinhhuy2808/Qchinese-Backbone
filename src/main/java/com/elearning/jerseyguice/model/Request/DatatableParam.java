package com.elearning.jerseyguice.model.Request;

import java.util.List;

public class DatatableParam {
	private int draw;
	private List<Column> columns;
	private List<Order> order;
	private int start;
	private int length;
	private Search search;
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public List<Order> getOrder() {
		return order;
	}
	public void setOrder(List<Order> orders) {
		this.order = orders;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public Search getSearch() {
		return search;
	}
	public void setSearch(Search search) {
		this.search = search;
	}
}
