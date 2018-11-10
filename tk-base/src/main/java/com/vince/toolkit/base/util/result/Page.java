package com.vince.toolkit.base.util.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

	private static final long serialVersionUID = 4324750940668524799L;

	@JSONField(name = "current_page")
	private int currentPage = 1; // 当前页

	@JSONField(name = "page_size")
	private int pageSize = 10; // 每页多少行

	@JSONField(name = "total")
	private int total; // 共多少行

	@JSONField(name = "total_page")
	private int totalPage; // 共多少页

	@JSONField(name = "rows")
	private List<T> rows;// 数据

	@JSONField(name = "start")
	private int start = 0;// 数据库查询开始行数

	public Page() {
		this(1, 10);
	}

	public Page(int currentPage, int pageSize) {
		setPageSize(pageSize);
		setCurrentPage(currentPage);
	}

	public Page(int currentPage, int pageSize, int total) {
		this.setPageSize(pageSize);
		this.setCurrentPage(currentPage);
		this.setTotal(total);
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		if (currentPage < 1) {
			currentPage = 1;
		}
		this.currentPage = currentPage;
		this.start = this.pageSize * (this.currentPage - 1);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		if (pageSize < 1) {
			pageSize = 10;
		}
		if (pageSize > 100) {
			pageSize = 100;
		}
		this.pageSize = pageSize;
		this.start = this.pageSize * (this.currentPage - 1);
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.totalPage = (total + pageSize - 1) / pageSize;
		this.total = total;
		this.start = this.pageSize * (this.currentPage - 1);
	}

	public int getTotal_page() {
		return totalPage;
	}

	public List<? extends Object> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public int getStart() {
		return start;
	}

}