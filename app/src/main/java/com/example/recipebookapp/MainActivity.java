
package com.example.recipebookapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etRecipeName, etIngredients, etInstructions;
    EditText etPrepTime, etSearch;

    Button btnAdd, btnUpdate, btnDelete;
    Button btnSearch, btnDisplay;

    ListView lvRecipes;

    DatabaseHelper dbHelper;

    ArrayList<DatabaseHelper.Recipe> recipes;
    ArrayAdapter<DatabaseHelper.Recipe> adapter;

    DatabaseHelper.Recipe selectedRecipe = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etRecipeName = findViewById(R.id.etRecipeName);
        etIngredients = findViewById(R.id.etIngredients);
        etInstructions = findViewById(R.id.etInstructions);
        etPrepTime = findViewById(R.id.etPrepTime);
        etSearch = findViewById(R.id.etSearch);

        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnSearch = findViewById(R.id.btnSearch);
        btnDisplay = findViewById(R.id.btnDisplay);

        lvRecipes = findViewById(R.id.lvRecipes);

        dbHelper = new DatabaseHelper(this);

        recipes = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipes
        );

        lvRecipes.setAdapter(adapter);

        // CREATE
        btnAdd.setOnClickListener(v -> {
            if (!validateInput()) {
                return;
            }

            int time = Integer.parseInt(
                    etPrepTime.getText().toString().trim()
            );

            boolean success = dbHelper.addRecipe(
                    etRecipeName.getText().toString().trim(),
                    etIngredients.getText().toString().trim(),
                    etInstructions.getText().toString().trim(),
                    time
            );

            if (success) {
                showMessage("Recipe added successfully");
                clearFields();
                loadAllRecipes();
            } else {
                showMessage("Failed to add recipe");
            }
        });

        // READ
        btnDisplay.setOnClickListener(v -> loadAllRecipes());

        // SEARCH
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();

            selectedRecipe = null;
            clearFields();

            ArrayList<DatabaseHelper.Recipe> results =
                    dbHelper.searchRecipes(keyword);

            showRecipes(results);

            if (results.isEmpty()) {
                showMessage("No matching recipes found");
            }
        });

        // SELECT A RECIPE FROM THE LIST
        lvRecipes.setOnItemClickListener((parent, view, position, id) -> {
            selectedRecipe = recipes.get(position);

            etRecipeName.setText(selectedRecipe.name);
            etIngredients.setText(selectedRecipe.ingredients);
            etInstructions.setText(selectedRecipe.instructions);
            etPrepTime.setText(
                    String.valueOf(selectedRecipe.prepTime)
            );

            showMessage("Recipe selected for editing");
        });

        // UPDATE
        btnUpdate.setOnClickListener(v -> {
            if (selectedRecipe == null) {
                showMessage("Select a recipe from the list first");
                return;
            }

            if (!validateInput()) {
                return;
            }

            int time = Integer.parseInt(
                    etPrepTime.getText().toString().trim()
            );

            boolean success = dbHelper.updateRecipe(
                    selectedRecipe.id,
                    etRecipeName.getText().toString().trim(),
                    etIngredients.getText().toString().trim(),
                    etInstructions.getText().toString().trim(),
                    time
            );

            if (success) {
                showMessage("Recipe updated successfully");
                selectedRecipe = null;
                clearFields();
                loadAllRecipes();
            } else {
                showMessage("Failed to update recipe");
            }
        });

        // DELETE
        btnDelete.setOnClickListener(v -> {
            if (selectedRecipe == null) {
                showMessage("Select a recipe from the list first");
                return;
            }

            boolean success = dbHelper.deleteRecipe(selectedRecipe.id);

            if (success) {
                showMessage("Recipe deleted successfully");
                selectedRecipe = null;
                clearFields();
                loadAllRecipes();
            } else {
                showMessage("Failed to delete recipe");
            }
        });

        loadAllRecipes();
    }

    private boolean validateInput() {
        String name = etRecipeName.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String instructions = etInstructions.getText().toString().trim();
        String timeText = etPrepTime.getText().toString().trim();

        if (name.isEmpty()) {
            etRecipeName.setError("Enter recipe name");
            return false;
        }

        if (ingredients.isEmpty()) {
            etIngredients.setError("Enter ingredients");
            return false;
        }

        if (instructions.isEmpty()) {
            etInstructions.setError("Enter cooking instructions");
            return false;
        }

        if (timeText.isEmpty()) {
            etPrepTime.setError("Enter preparation time");
            return false;
        }

        try {
            int time = Integer.parseInt(timeText);

            if (time <= 0) {
                etPrepTime.setError("Time must be greater than zero");
                return false;
            }
        } catch (NumberFormatException e) {
            etPrepTime.setError("Enter a valid number");
            return false;
        }

        return true;
    }

    private void loadAllRecipes() {
        showRecipes(dbHelper.getAllRecipes());
    }

    private void showRecipes(
            ArrayList<DatabaseHelper.Recipe> results
    ) {
        recipes.clear();
        recipes.addAll(results);
        adapter.notifyDataSetChanged();
    }

    private void clearFields() {
        etRecipeName.setText("");
        etIngredients.setText("");
        etInstructions.setText("");
        etPrepTime.setText("");
    }

    private void showMessage(String message) {
        Toast.makeText(
                this, message, Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }

        super.onDestroy();
    }
}