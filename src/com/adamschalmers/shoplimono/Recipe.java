package com.adamschalmers.shoplimono;
import java.util.ArrayList;



public class Recipe {
	private String url;
	private String name;
	private ArrayList<Ingredient> ingredients;
	
	public Recipe(String url, String name, ArrayList<Ingredient> ingredients) {
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		this.url = url;
		this.name = name;
		this.ingredients = ingredients;
	}
	
	public Recipe(String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		this.url = url;
		
		// TODO: turn this stub into a scraper call
		this.ingredients = new ArrayList<Ingredient>();
		this.ingredients.add(new Ingredient("RecipeItem", 2, "tsp"));
		this.name = "New Recipe";
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Ingredient> getIngredients() {
		return ingredients;
	}
}
