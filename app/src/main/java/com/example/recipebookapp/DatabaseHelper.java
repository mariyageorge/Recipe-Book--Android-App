
package com.example.recipebookapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "RecipeDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE Recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "ingredients TEXT, " +
                        "instructions TEXT, " +
                        "prepTime INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Add database migration code if the schema changes.
    }

    // CREATE
    public boolean addRecipe(String name, String ingredients,
                             String instructions, int prepTime) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("ingredients", ingredients);
        values.put("instructions", instructions);
        values.put("prepTime", prepTime);

        return db.insert("Recipes", null, values) != -1;
    }

    // READ: Display all recipes
    public ArrayList<Recipe> getAllRecipes() {
        return getRecipes(null);
    }

    // SEARCH: Search recipe names
    public ArrayList<Recipe> searchRecipes(String keyword) {
        return getRecipes(keyword);
    }

    private ArrayList<Recipe> getRecipes(String keyword) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor;

        if (keyword == null || keyword.trim().isEmpty()) {
            cursor = db.rawQuery(
                    "SELECT * FROM Recipes ORDER BY name",
                    null
            );
        } else {
            cursor = db.rawQuery(
                    "SELECT * FROM Recipes WHERE name LIKE ? ORDER BY name",
                    new String[]{"%" + keyword.trim() + "%"}
            );
        }

        try {
            while (cursor.moveToNext()) {
                Recipe recipe = new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ingredients")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("prepTime"))
                );

                recipes.add(recipe);
            }
        } finally {
            cursor.close();
        }

        return recipes;
    }

    // UPDATE
    public boolean updateRecipe(int id, String name,
                                String ingredients, String instructions,
                                int prepTime) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("ingredients", ingredients);
        values.put("instructions", instructions);
        values.put("prepTime", prepTime);

        return db.update(
                "Recipes", values, "id = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    // DELETE
    public boolean deleteRecipe(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                "Recipes", "id = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    // Model for one recipe
    public static class Recipe {
        public int id;
        public String name;
        public String ingredients;
        public String instructions;
        public int prepTime;

        public Recipe(int id, String name, String ingredients,
                      String instructions, int prepTime) {
            this.id = id;
            this.name = name;
            this.ingredients = ingredients;
            this.instructions = instructions;
            this.prepTime = prepTime;
        }

        @Override
        public String toString() {
            return name
                    + "\nIngredients: " + ingredients
                    + "\nInstructions: " + instructions
                    + "\nPreparation time: " + prepTime + " minutes"
                    + "\nRecipe ID: " + id;
        }
    }
}
