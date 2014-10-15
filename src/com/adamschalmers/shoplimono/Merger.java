package com.adamschalmers.shoplimono;

public class Merger {
	public static Ingredient mergeTwo(Ingredient a, Ingredient b) {
		Ingredient merged;
		String name;
		Double amount;
		String unit;
		if (a.getName() == b.getName()) name = a.getName();
		else name = a.getName() + "/" + b.getName();
		
		merged = new Ingredient(name, amount, unit);
		return merged;
	}
}
