package com.adamschalmers.shoplimono;

public class Merger {
	
	public static Ingredient normalise(Ingredient ing) {
		switch (ing.getUnit()) {
		case "kg":
			return new Ingredient(ing.getName(), ing.getAmount()*1000, "g");
		case "mg":
			return new Ingredient(ing.getName(), ing.getAmount()/1000, "g");
		case "mL":
			return new Ingredient(ing.getName(), ing.getAmount()/1000, "L");
		case "tbsp":
			return new Ingredient(ing.getName(), ing.getAmount()*0.0625, "tsp");
		}
		return ing;
	}
	private static boolean compatible(Ingredient a, Ingredient b) {
		a = normalise(a);
		b = normalise(b);
		return a.getUnit() == b.getUnit();
	}
	
	// Returns null if the ingredients can't be merged.
	public static Ingredient mergeTwo(Ingredient a, Ingredient b) {
		
		// If the ingredients can be merged, merge them.
		if (compatible(a, b)) {
			// If they share a name, keep it.
			// Otherwise, concatenate the names.
			String name = a.getName();
			if (a.getName() != b.getName()) {
				name += "/" + b.getName();
			}
			return new Ingredient(name, normalise(a).getAmount() + normalise(b).getAmount(), normalise(a).getUnit());
		}
		
		return null;
	}
}
