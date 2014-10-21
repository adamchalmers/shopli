package com.adamschalmers.shoplimono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import org.json.*;

//{"name": "Oven-baked chicken and chorizo paella", "ingredients": [{"amount": "1/2", "name": "saffron threads ", "unit": "teaspoon"}, {"amount": "2", "name": "boiling water ", "unit": "tablespoons"}, {"amount": "8", "name": "salt-reduced chicken stock ", "unit": "cups"}, {"amount": "2", "name": "olive oil ", "unit": "teaspoons"}, {"amount": "2", "name": "chorizo, sliced ", "unit": "(110g)"}, {"amount": "1", "name": "chicken drumettes ", "unit": "kg"}, {"amount": "2", "name": "smoked paprika ", "unit": "tablespoons"}, {"amount": "2", "name": "onions, chopped ", "unit": "brown"}, {"amount": "3", "name": "cloves, crushed ", "unit": "garlic"}, {"amount": "4", "name": "SunRice Arborio Risotto Rice ", "unit": "cups"}, {"amount": "2", "name": "frozen peas ", "unit": "cups"}, {"amount": "200", "name": "chargrilled capsicum, sliced ", "unit": "g"}, {"amount": "2", "name": "tomatoes, diced ", "unit": "medium"}, {"amount": "1/2", "name": "fresh flat-leaf parsley, roughly chopped ", "unit": "cup"}, {"amount": "2", "name": "cut into 12 wedges ", "unit": "lemons,"}]}

@Table(name = "Recipes")
public class Recipe extends Model {
	@Column(name = "url")
	private String url;
	@Column(name = "name")
	private String name;
	private ArrayList<Ingredient> ingredients;
	
	private String parserService = "http://ec2-54-66-196-222.ap-southeast-2.compute.amazonaws.com/recipe/";
	
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
		
		String json = getJson(url);
		if (json == null) {
			this.name = "ERROR";
			this.ingredients = new ArrayList<Ingredient>();
		} else {
			this.name = parseName(json);
			this.ingredients = parseIngredients(json);
			this.ingredients.add(Ingredient.makeNew("RecipeItem", 2, "tsp"));
		}
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
	
	/*
	 * Looks up the recipe URL on our scraper service.
	 * Returns the ingredients in JSON form.
	 */
	private String getJson(String url) {
		/*
		// Parser service can't handle slashes in recipe URL,
		// so replace them with ~'s
		url = this.parserService + url.replace("/", "~");
		URL request;
		try {
			request = new URL(url);
			Log.e("recipe", url.toString());
			try {
				
				// Get the service's response
				 BufferedReader in = new BufferedReader(new InputStreamReader(request.openStream()));
				String response = in.readLine();
				return response;
				
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		}*/
		return "{'name': 'Oven-baked chicken and chorizo paella', 'ingredients': [{'amount': '1/2', 'name': 'saffron threads ', 'unit': 'teaspoon'}, {'amount': '2', 'name': 'boiling water ', 'unit': 'tablespoons'}, {'amount': '8', 'name': 'salt-reduced chicken stock ', 'unit': 'cups'}, {'amount': '2', 'name': 'olive oil ', 'unit': 'teaspoons'}, {'amount': '2', 'name': 'chorizo, sliced ', 'unit': '(110g)'}, {'amount': '1', 'name': 'chicken drumettes ', 'unit': 'kg'}, {'amount': '2', 'name': 'smoked paprika ', 'unit': 'tablespoons'}, {'amount': '2', 'name': 'onions, chopped ', 'unit': 'brown'}, {'amount': '3', 'name': 'cloves, crushed ', 'unit': 'garlic'}, {'amount': '4', 'name': 'SunRice Arborio Risotto Rice ', 'unit': 'cups'}, {'amount': '2', 'name': 'frozen peas ', 'unit': 'cups'}, {'amount': '200', 'name': 'chargrilled capsicum, sliced ', 'unit': 'g'}, {'amount': '2', 'name': 'tomatoes, diced ', 'unit': 'medium'}, {'amount': '1/2', 'name': 'fresh flat-leaf parsley, roughly chopped ', 'unit': 'cup'}, {'amount': '2', 'name': 'cut into 12 wedges ', 'unit': 'lemons,'}]}";
	}
	
	/*
	 * Parses the recipe's name from JSON
	 */
	private String parseName(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			String recipeName = obj.getString("name");
			return recipeName;
		} catch (JSONException e) {
			Log.w("MainActivity - Recipe.parseName", "Failure.");
			return "FailName";
		}
	}
	
	/*
	 * Parses an ingredient list from JSON
	 */
	private ArrayList<Ingredient> parseIngredients(String json) {
		ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray jsonIngredients = obj.getJSONArray("ingredients");
			for (int i = 0; i < jsonIngredients.length(); i++) {
				JSONObject ing = jsonIngredients.getJSONObject(i);
				String name = ing.getString("name");
				double amount;
				String units;
				try {
					amount = Double.parseDouble(ing.getString("amount"));
					units = ing.getString("unit");
				} catch (NumberFormatException e) {
					if (ing.getString("amount").equals("1/2")) {
						amount = 0.5;
						units = ing.getString("unit");
					} else if (ing.getString("amount").equals("3/4")) {
						amount = 0.75;
						units = ing.getString("unit");
					} else if (ing.getString("amount").equals("1/4")) {
						amount = 0.25;
						units = ing.getString("unit");
					} else {
						amount = 0;
						units = ing.getString("amount") + " " + ing.getString("unit");
					}
				}
				ingredients.add(Ingredient.makeNew(name, amount, units));
			}
		} catch (JSONException e) {
			Log.w("MainActivity - Recipe.parseIngredients", "Parse failed.");
		}
		return ingredients;
	}
}
