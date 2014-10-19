package com.adamschalmers.shoplimono;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class IngredientAdapter extends ArrayAdapter<Ingredient> {
	
	private SparseBooleanArray mSelectedItemIds;
	
    public IngredientAdapter(Context context, ArrayList<Ingredient> Ingredients) {
       super(context, 0, Ingredients);
       mSelectedItemIds = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Ingredient Ingredient = getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient, parent, false);
       }
       
       // Lookup view for data population
       TextView ingredientName = (TextView) convertView.findViewById(R.id.ingredientName);
       TextView ingredientAmount = (TextView) convertView.findViewById(R.id.ingredientAmount);
       CheckBox ingredientCheckbox = (CheckBox) convertView.findViewById(R.id.ingredientCheck);
       
       ingredientCheckbox.setTag(position); // For remembering where in the list this is
       
       // Fill in the view with data from the Ingredient object
       ingredientName.setText(Ingredient.getName());
       
       // If the amount is actually an integer, display it as an integer (4 grams instead of 4.0 grams)
       Object amount = Ingredient.getAmount();
       String units = (Ingredient.getUnit() != null && Ingredient.getUnit() != "") ? " " + Ingredient.getUnit() : "";
       ingredientAmount.setText("" + amount + units);
       
       // Set text to grey and checkbox checked if the Ingredient's .checked property is true
       ingredientCheckbox.setChecked(Ingredient.getChecked());
       int color = (Ingredient.getChecked()) ? Color.GRAY : Color.BLACK;
       ingredientName.setTextColor(color);
       ingredientAmount.setTextColor(color);
       
       // Return the completed view to render on screen
       return convertView;
    }
       
    public void toggleSelection(int position) {
    	selectView(position, !mSelectedItemIds.get(position));
    }
    
    public void selectView(int position, boolean value) {
    	if (value) {
    		mSelectedItemIds.put(position, value);
    	} else {
    		mSelectedItemIds.delete(position);
    	}
    	notifyDataSetChanged();
    }
    
    public int getSelectedCount() {
    	return mSelectedItemIds.size();
    }
    
    public SparseBooleanArray getSelectedIds() {
    	return mSelectedItemIds;
    }
    
    public void removeSelection() {
    	mSelectedItemIds = new SparseBooleanArray();
    	notifyDataSetChanged();
    }
    
    public void add(Ingredient ing) {
    	super.add(ing);
    	ing.save();
    }
    
    public void remove(Ingredient ing) {
    	super.remove(ing);
    	ing.delete();
    }
}