package com.adamschalmers.shoplimono;

public class Ingredient {
	private double amount;
	private String unit;
	private String name;
	
	public Ingredient(String n, double a, String u) {
		name = n;
		amount = a;
		unit = u;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
