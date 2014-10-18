package com.adamschalmers.shoplimono;

import java.util.ArrayList;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "Recipes")
public class Recipe extends Model {
	@Column(name = "url")
	private String url;
	@Column(name = "name")
	private String name;
	private ArrayList<Ingredient> ingredients;
	
	// Default constructor for Model - don't use for making Recipes!
	public Recipe() {
		super();
	}
	
	/*
	 * Create a new recipe with all fields
	 */
	public Recipe(String url, String name, ArrayList<Ingredient> ingredients) {
		super();
		this.url = Url.addHttp(url);
		this.name = name;
		this.ingredients = ingredients;
	}
	
	/*
	 * New recipe, remotely scraping URL for ingredients.
	 */
	public Recipe(String url) {
		this.url = Url.addHttp(url);
		
		// TODO: turn this stub into a scraper call
		this.ingredients = new ArrayList<Ingredient>();
		this.ingredients.add(Ingredient.makeNew("RecipeItem", 2, "tsp"));
		this.name = url.substring(33);
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
