package com.adamschalmers.shoplimono;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
	
	ArrayList<Ingredient> ingredients;
	EditText urlField;
	IngredientAdapter adapter;
	ListView ingredientsList;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initialize the ingredients list
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(new Ingredient("Salt", 500, "g"));
		ingredients.add(new Ingredient("Garlic", 4, "cloves"));
		
		// Find our views
		urlField = (EditText) findViewById(R.id.urlField);
		ingredientsList = (ListView) findViewById(R.id.ingredientsList);
		
		// Set up the adapter
		adapter = new IngredientAdapter(this, ingredients);
		ingredientsList.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addRecipe(View v) {
		String url = urlField.getText().toString();
		adapter.add(new Ingredient(url));
		urlField.setText("");
	}

}
