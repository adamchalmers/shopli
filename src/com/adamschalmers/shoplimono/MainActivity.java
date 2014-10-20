package com.adamschalmers.shoplimono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	ArrayList<Ingredient> ingredients;
	ArrayList<Recipe> recipes;
	EditText urlField;
	IngredientAdapter ingredientAdapter;
	RecipeAdapter recipeAdapter;
	ListView ingredientsList;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initialize recipes list
		readRecipesFromDb();
		recipeAdapter = new RecipeAdapter(this, recipes);
		
		// Initialize the ingredients list
		readIngredientsFromDb();
		
		// Find our views
		ingredientsList = (ListView) findViewById(R.id.ingredientsList);
		
		// Set up the adapter
		ingredientAdapter = new IngredientAdapter(this, ingredients);
		ingredientsList.setAdapter(ingredientAdapter);
		
		// Set up the UI
		ingredientsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		setupListViewListener();
		urlField = (EditText) findViewById(R.id.urlField);
		
		/* 
		 * Handle intents and shares
		 */
		Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();

	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if ("text/plain".equals(type)) {
	            handleSendText(intent);
	            setResult(1);
	            finish();
	        }
	    }
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
		switch (id) {
		case R.id.action_settings:
			return true;
			
		// If the user clicks "show recipes", bring up an alert with a list of all recipes.
		case R.id.action_recipes:
			AlertDialog.Builder recipeBuilder = new AlertDialog.Builder(MainActivity.this);
			recipeBuilder.setTitle("Open recipe")
		    	.setAdapter(recipeAdapter, new DialogInterface.OnClickListener() {
		    			
		    	// When a user clicks a recipe, open it in the web browser.
			   public void onClick(DialogInterface dialog, int which) {
				   String url = recipes.get(which).getUrl();
				   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				   startActivity(browserIntent);
			   }
		    });
			recipeBuilder.create().show();
			return true;
			
		// Remove all recipes 
		case R.id.action_clear_recipes:
			recipeAdapter.clear();
			dataChanged();
			return true;
			
		// Insert test ingredients and recipes
		case R.id.action_testdata:
			ingredientAdapter.add(Ingredient.makeNew("Salt", 500, "g"));
			ingredientAdapter.add(Ingredient.makeNew("Garlic", 4, "cloves"));
			ingredientAdapter.add(Ingredient.makeNew("Chives", 2, "tsp"));
			ingredientAdapter.add(Ingredient.makeNew("Potatoes", 3, "kg"));
			ingredientAdapter.add(Ingredient.makeNew("Chicken stock", 2, "L"));
			recipeAdapter.add(new Recipe("http://www.taste.com.au/recipes/cake", "Sultana cake", ingredients));
			recipeAdapter.add(new Recipe("http://www.taste.com.au/recipes/cake", "Thai beef salad", new ArrayList<Ingredient>()));
			dataChanged();
			return true;
			
		// Wipe all database entries, close the app.
		case R.id.action_wipe:
			ingredientAdapter.clear();
			recipeAdapter.clear();
			deleteDatabase("Shopli.db");
			finish();
			
		// Show a dialog to let the user add their own ingredient
		case R.id.action_add_ingredient:
			
			// Show the merge dialog box
			AlertDialog.Builder ingredientBuilder = new AlertDialog.Builder(MainActivity.this);
			LayoutInflater inflater = MainActivity.this.getLayoutInflater();
			View mergeView = inflater.inflate(R.layout.merge, null);
			
			// Set up the dialog box
			ingredientBuilder.setTitle("Add ingredient")
			
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						
						// Add one new merged item from the user's input
						AlertDialog _dialog = (AlertDialog) dialog;
						Spinner _units = ((Spinner) _dialog.findViewById(R.id.unitsSpinner));
						TextView tv = (TextView) _units.getSelectedView();
						String units = tv.getText().toString();
						String name = ((EditText) _dialog.findViewById(R.id.mergeName)).getText().toString();
						Double amount = Double.parseDouble(((EditText) _dialog.findViewById(R.id.mergeQuantity)).getText().toString());
						
						Ingredient newIng = Ingredient.makeNew(name, amount, units);
						ingredientAdapter.add(newIng);
						dataChanged();
					}
					
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//
					}
				}).setView(mergeView);
			AlertDialog dialog = ingredientBuilder.create();
			
			// Set up the spinner
			Spinner spinner = (Spinner) mergeView.findViewById(R.id.unitsSpinner);
			ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
					getApplicationContext(), R.array.units, R.layout.spinner_item);
			spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
			spinner.setAdapter(spinnerAdapter);
			
			// Create the dialog and finish
			dialog.show();
			return true;
			
			
		// Remove all checked ingredients
		case R.id.action_clear_checked_ingredients:
			int i = 0;
			while (i < ingredientAdapter.getCount()) {
				Ingredient ing = ingredientAdapter.getItem(i);
				if (ing.getChecked()) {
					ingredientAdapter.remove(ing);
				} else {
					i++;
				}
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Called when the user presses the "add" button.
	public void addRecipe(View v) {
		String url = urlField.getText().toString();
		if (Url.isTasteUrl(Url.addHttp(url))) {
			addRecipeFromUrl(url);
		} else {
			Toast.makeText(getApplicationContext(), R.string.bad_url, Toast.LENGTH_SHORT).show();
		}
	}
	
	// Add the recipe at the given URL, and all its ingredients to the ingredients list.
	// If ingredients are duplicates of already-present ingredients, just increase their quantity.
	public void addRecipeFromUrl(String url) {
		Recipe recipe = new Recipe(url);
		recipeAdapter.add(recipe);
		
		for (Ingredient newIngredient : recipe.getIngredients()) {
			boolean found = false;
			
			/* Check if the ingredient is already present in our recipe list.
			 * If it is, just increase the quantity.*/
			for (int i = 0, n = ingredientAdapter.getCount(); i < n; i++) {
				Ingredient ing = ingredientAdapter.getItem(i);
				if (ing.getUnit().equals(newIngredient.getUnit()) && ing.getName().equals(newIngredient.getName())) {
					found = true;
					ing.setAmount(ing.getAmount() + newIngredient.getAmount());
				}
			}
			// If it wasn't found, add the new ingredient.
			if (!found) ingredientAdapter.add(newIngredient);
		}
		dataChanged();
		urlField.setText("");
	}
	
	/* 
	 * When the user clicks the checkbox, update the ingredient and grey out/restore its text.
	 */
	public void toggleCheckbox(View view) {
		CheckBox checkbox = (CheckBox) view;
		View parent = (View) checkbox.getParent();
		TextView name = (TextView) parent.findViewById(R.id.ingredientName);
		TextView amount = (TextView) parent.findViewById(R.id.ingredientAmount);
		int position = ingredientsList.getPositionForView(view);
		
		// Checked items are grey, unchecked items are black.
		if (checkbox.isChecked()) {
			name.setTextColor(Color.GRAY);
			amount.setTextColor(Color.GRAY);
		} else {
			name.setTextColor(Color.BLACK);
			amount.setTextColor(Color.BLACK);
		}
		ingredientAdapter.getItem(position).setChecked(checkbox.isChecked());
	}
	
	/*
	 * Initializes the long-click merge/delete functionality of the main listview.
	 */
	private void setupListViewListener() {
		//TODO: Make this display a pop-up with different units
		//ingredientsList.setOnItemClickListener(new OnItemClickListener() {}
		
		ingredientsList.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				mode.setTitle(ingredientsList.getCheckedItemCount() + " Selected");
				ingredientAdapter.toggleSelection(position);
			}

			@Override
			/* 
			 * Handle the user clicking one of the action buttons in the merge menu
			 * @see android.view.ActionMode.Callback#onActionItemClicked(android.view.ActionMode, android.view.MenuItem)
			 */
			public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
				SparseBooleanArray selected = ingredientAdapter.getSelectedIds();
				switch (item.getItemId()) {
				
				// Delete the selected items
				case R.id.delete:
					for (int i = (selected.size() - 1); i >= 0; i--) {
						if (selected.valueAt(i)) {
							Ingredient selecteditem = ingredientAdapter.getItem(selected.keyAt(i));
							ingredientAdapter.remove(selecteditem);
						}
					}
					dataChanged();
					mode.finish();
					return true;
					
				// Merge the selected items
				case R.id.merge:
					// Show the merge dialog box
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					LayoutInflater inflater = MainActivity.this.getLayoutInflater();
					View mergeView = inflater.inflate(R.layout.merge, null);
					
					// Set up the dialog box
					builder.setTitle("Merge items").setMessage("Let's merge the items")
					
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								// Delete the old items
								SparseBooleanArray selected = ingredientAdapter.getSelectedIds();
								for (int i = (selected.size() - 1); i >= 0; i--) {
									if (selected.valueAt(i)) {
										Ingredient selecteditem = ingredientAdapter.getItem(selected.keyAt(i));
										ingredientAdapter.remove(selecteditem);
									}
								}
								
								// Add one new merged item from the user's input
								AlertDialog _dialog = (AlertDialog) dialog;
								Spinner _units = ((Spinner) _dialog.findViewById(R.id.unitsSpinner));
								TextView tv = (TextView) _units.getSelectedView();
								String units = tv.getText().toString();
								String name = ((EditText) _dialog.findViewById(R.id.mergeName)).getText().toString();
								Double amount = Double.parseDouble(((EditText) _dialog.findViewById(R.id.mergeQuantity)).getText().toString());
								
								Ingredient merged = Ingredient.makeNew(name, amount, units);
								ingredientAdapter.add(merged);
								dataChanged();
								mode.finish();
							}
							
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								//
							}
						}).setView(mergeView);
					AlertDialog dialog = builder.create();
					
					// Set up the spinner
					Spinner spinner = (Spinner) mergeView.findViewById(R.id.unitsSpinner);
					ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
							getApplicationContext(), R.array.units, R.layout.spinner_item);
					spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
					spinner.setAdapter(spinnerAdapter);
					
					// Create the dialog and finish
					dialog.show();
					return true;
					
				default:
					return false;
					
				}
			}

			@Override
			// Create the action mode menu
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.longclick_menu, menu);
				return true;
			}

			@Override
			// When the user exits multiselect mode, remove all selections.
			public void onDestroyActionMode(ActionMode mode) {
				ingredientAdapter.removeSelection();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
		});
		
	}
	
	/*
	 * Called if the user opens a URL with this app. 
	 * We should add the URL's recipe to the list.
	 */
	void handleSendText(Intent intent) {
	    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
	    if (sharedText != null) {
	    	if (Url.isTasteUrl(sharedText)) {
	    		addRecipeFromUrl(sharedText);
	    	} else {
	    		Toast.makeText(getApplicationContext(), R.string.bad_url, Toast.LENGTH_SHORT).show();
	    	}
	    }
	}
	
	/*
	 * Data persistance: load ingredients from database
	 */
	private void readIngredientsFromDb() {
		List<Ingredient> itemsFromDb = new Select().from(Ingredient.class).execute();
		ingredients = new ArrayList<Ingredient>();
		if (itemsFromDb != null && itemsFromDb.size() > 0) {
			for (Ingredient ing : itemsFromDb) {
				ingredients.add(ing);
			}
		}
	}
	
	/*
	 * Data persistance: load recipes from database
	 */
	private void readRecipesFromDb() {
		List<Recipe> itemsFromDb = new Select().from(Recipe.class).execute();
		recipes = new ArrayList<Recipe>();
		if (itemsFromDb != null && itemsFromDb.size() > 0) {
			for (Recipe r : itemsFromDb) {
				recipes.add(r);
			}
		}
	}
	
	private void dataChanged() {
		ingredientAdapter.notifyDataSetChanged();
		recipeAdapter.notifyDataSetChanged();
	}

}
