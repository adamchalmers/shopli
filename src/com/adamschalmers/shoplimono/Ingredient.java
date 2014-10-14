package com.adamschalmers.shoplimono;

public class Ingredient {
	private double amount;
	private String unit;
	public String name;
	
	public Ingredient(String n) {
		name = n;
		amount = 0;
		unit = "";
	}
	
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
}
