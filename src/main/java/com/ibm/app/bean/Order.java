package com.ibm.app.bean;

import java.util.Date;

public class Order {
	String orderId, status, description;
	Double amount;
	Integer numItems;
	Date date;

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Integer getNumItems() {
		return numItems;
	}
	public void setNumItems(Integer numItems) {
		this.numItems = numItems;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
