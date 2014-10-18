package com.adamschalmers.shoplimono;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RecipeAdapter extends ArrayAdapter<Recipe> { 
	
	public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
		super(context, 0, recipes);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       Recipe recipe = getItem(position);    
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.recipe, parent, false);
       }
       
       // Lookup view for data population
       TextView recipeName = (TextView) convertView.findViewById(R.id.recipeName);
       
       // Fill in the view with data from the Ingredient object
       recipeName.setText(recipe.getName());
       
       // Return the completed view to render on screen
       return convertView;
    }

}
