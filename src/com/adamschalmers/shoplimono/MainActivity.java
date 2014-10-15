package com.adamschalmers.shoplimono;

import java.util.ArrayList;
import java.util.HashSet;

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
	IngredientAdapter adapter;
	RecipeAdapter recipeAdapter;
	ListView ingredientsList;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		recipes = new ArrayList<Recipe>();
		recipes.add(new Recipe("www.taste.com/cake", "Cake", new ArrayList<Ingredient>()));
		recipeAdapter = new RecipeAdapter(this, recipes);
		
		// Initialize the ingredients list
		ingredients = new ArrayList<Ingredient>();
		ingredients.add(new Ingredient("Salt", 500, "g"));
		ingredients.add(new Ingredient("Garlic", 4, "cloves"));
		ingredients.add(new Ingredient("Chives", 2, "tsp"));
		ingredients.add(new Ingredient("Potatoes", 3, "kg"));
		ingredients.add(new Ingredient("Chicken stock", 2, "L"));
		
		// Find our views
		urlField = (EditText) findViewById(R.id.urlField);
		ingredientsList = (ListView) findViewById(R.id.ingredientsList);
		
		// Set up the adapter
		adapter = new IngredientAdapter(this, ingredients);
		ingredientsList.setAdapter(adapter);
		
		// Set up the user input listeners
		ingredientsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		setupListViewListener();
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
		} else if (id == R.id.action_recipes) {
			
			// If the user clicks "show recipes", bring up an alert with a list of all recipes.
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		    builder.setTitle("Open recipe")
		    	.setAdapter(recipeAdapter, new DialogInterface.OnClickListener() {
		    			
		    	// When a user clicks a recipe, open it in the web browser.
			   public void onClick(DialogInterface dialog, int which) {
				   String url = recipes.get(which).getUrl();
				   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				   startActivity(browserIntent);
			   }
		    });
		    builder.create().show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Called when the user presses the "add" button.
	public void addRecipe(View v) {
		String url = urlField.getText().toString();
		Recipe recipe = new Recipe(url);
		recipes.add(recipe);
		
		for (Ingredient newIngredient : recipe.getIngredients()) {
			boolean found = false;
			
			/* Check if the ingredient is already present in our recipe list.
			 * If it is, just increase the quantity.*/
			for (Ingredient ingredient : ingredients) {
				if (ingredient.getUnit() == newIngredient.getUnit() && ingredient.getName() == newIngredient.getName()) {
					found = true;
					ingredient.setAmount(ingredient.getAmount() + newIngredient.getAmount());
				}
			}
			// If it wasn't found, add the new ingredient.
			if (!found) adapter.add(newIngredient);
		}
		urlField.setText("");
	}
	
	/* 
	 * When the user clicks the checkbox
	 */
	public void toggleCheckbox(View view) {
		CheckBox checkbox = (CheckBox) view;
		View parent = (View) checkbox.getParent();
		TextView name = (TextView) parent.findViewById(R.id.ingredientName);
		TextView amount = (TextView) parent.findViewById(R.id.ingredientAmount);
		// Checked items are grey, unchecked items are black.
		if (checkbox.isChecked()) {
			name.setTextColor(Color.GRAY);
			amount.setTextColor(Color.GRAY);
		} else {
			name.setTextColor(Color.BLACK);
			amount.setTextColor(Color.BLACK);
		}
	}
	
	private void setupListViewListener() {
		/*ingredientsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position,long rowId) {
				ingredients.remove(position);
				adapter.notifyDataSetChanged();
				(Toast.makeText(getApplicationContext(), "Clearing", Toast.LENGTH_SHORT)).show();
			}
		});*/
		
		ingredientsList.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				mode.setTitle(ingredientsList.getCheckedItemCount() + " Selected");
				adapter.toggleSelection(position);
			}

			@Override
			public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
				SparseBooleanArray selected = adapter.getSelectedIds();
				switch (item.getItemId()) {
				
				case R.id.delete:
					for (int i = (selected.size() - 1); i >= 0; i--) {
						if (selected.valueAt(i)) {
							Ingredient selecteditem = adapter.getItem(selected.keyAt(i));
							adapter.remove(selecteditem);
						}
					}
					mode.finish();
					return true;
					
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
								SparseBooleanArray selected = adapter.getSelectedIds();
								for (int i = (selected.size() - 1); i >= 0; i--) {
									if (selected.valueAt(i)) {
										Ingredient selecteditem = adapter.getItem(selected.keyAt(i));
										adapter.remove(selecteditem);
									}
								}
								
								// Add one new merged item from the user's input
								AlertDialog _dialog = (AlertDialog) dialog;
								Spinner _units = ((Spinner) _dialog.findViewById(R.id.unitsSpinner));
								TextView tv = (TextView) _units.getSelectedView();
								String units = tv.getText().toString();
								String name = ((EditText) _dialog.findViewById(R.id.mergeName)).getText().toString();
								Double amount = Double.parseDouble(((EditText) _dialog.findViewById(R.id.mergeQuantity)).getText().toString());
								
								Ingredient merged = new Ingredient(name, amount, units);
								adapter.add(merged);
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
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.longclick_menu, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				adapter.removeSelection();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
		});
		
	}
	
	

}
