package com.adamschalmers.shoplimono;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/* 
 * Model for Ingredients. 
 * Ingredients have a name (e.g "potato"), 
 * amount ("50") and unit of measurement 
 * ("grams")
 */
@Table(name = "Ingredients")
public class Ingredient extends Model {
	@Column(name = "amount")
	private double amount;
	@Column(name = "unit")
	private String unit;
	@Column(name = "name")
	private String name;
	
	/*
	 * ActiveAndroid models need default constructors. 
	 * Shouldn't actually be used.
	 */
	public Ingredient() {
		super();
	}
	
	public static Ingredient makeNew(String n, double a, String u) {
		Ingredient ingredient = new Ingredient();
		ingredient.name = n;
		ingredient.amount = a;
		ingredient.unit = u;
		return ingredient;
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
