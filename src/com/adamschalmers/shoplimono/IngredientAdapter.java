package com.adamschalmers.shoplimono;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IngredientAdapter extends ArrayAdapter<Ingredient> {
	
    public IngredientAdapter(Context context, ArrayList<Ingredient> Ingredients) {
       super(context, 0, Ingredients);
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
       
       // Fill in the view with data from the Ingredient object
       ingredientName.setText(Ingredient.name);
       // If the amount is actually an integer, display it as an integer (4 grams instead of 4.0 grams)
       Object amount = (Ingredient.getAmount() == (int) Ingredient.getAmount()) ? Integer.valueOf((int)Ingredient.getAmount()) : Ingredient.getAmount();
       String units = (Ingredient.getUnit() != null && Ingredient.getUnit() != "") ? " " + Ingredient.getUnit() : "";
       ingredientAmount.setText("" + amount + units);
       
       // Return the completed view to render on screen
       return convertView;
   }
}