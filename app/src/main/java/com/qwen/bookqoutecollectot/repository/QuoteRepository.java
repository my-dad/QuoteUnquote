package com.qwen.bookqoutecollectot.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.qwen.bookqoutecollectot.database.QuoteDao;
import com.qwen.bookqoutecollectot.database.QuoteDatabase;
import com.qwen.bookqoutecollectot.model.Quote;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList; // <-- Add this import
import java.util.Collections; // <-- Add this import
import java.util.Set; // <-- Add this import
import java.util.LinkedHashSet; // <-- Add this import (to maintain order and uniqueness)

public class QuoteRepository {

    private static final String TAG = "QuoteRepository";
    // --- Add this section ---
    // Define default categories to always be available
    private static final List<String> DEFAULT_CATEGORIES = Arrays.asList(
            "Inspiration", "Humor", "Wisdom", "Love", "Life",
            "Motivation", "Philosophy", "Fiction", "Non-Fiction",
            "Poetry", "Science", "History"
            // Add your desired default categories here
    );
    // --- End addition ---

    private QuoteDao quoteDao;
    private LiveData<List<Quote>> allQuotes;
    private LiveData<List<Quote>> favoriteQuotes;
    private LiveData<List<String>> databaseCategories; // <-- Rename for clarity
    private LiveData<List<String>> allCategoriesCombined; // <-- New LiveData for combined list
    private ExecutorService executor;

    public QuoteRepository(Application application) {
        QuoteDatabase database = QuoteDatabase.getInstance(application);
        quoteDao = database.quoteDao();
        allQuotes = quoteDao.getAllQuotes(); // These already filter out dummies if you updated QuoteDao
        favoriteQuotes = quoteDao.getFavoriteQuotes(); // These already filter out dummies if you updated QuoteDao

        // --- Modified Section ---
        // Get the LiveData for categories directly from the database
        databaseCategories = quoteDao.getAllCategories(); // This gets categories from real quotes

        // Create a new LiveData that combines DEFAULT_CATEGORIES with databaseCategories
        // Use Transformations.switchMap or similar logic. A simple way is to create a new LiveData
        // that observes databaseCategories and merges the lists.
        // Using a MediatorLiveData or custom LiveData might be cleaner, but this works with basic LiveData.

        // Create a MutableLiveData to hold the combined result
        MutableLiveData<List<String>> combinedCategoriesLiveData = new MutableLiveData<>();

        // Observe the database categories
        databaseCategories.observeForever(new androidx.lifecycle.Observer<List<String>>() {
            @Override
            public void onChanged(List<String> dbCategories) {
                // Combine defaults with database categories, ensuring uniqueness
                Set<String> uniqueCategories = new LinkedHashSet<>(DEFAULT_CATEGORIES);
                if (dbCategories != null) {
                    uniqueCategories.addAll(dbCategories);
                }
                // Convert back to list (maintains LinkedHashSet order: defaults first, then db)
                List<String> finalCategoryList = new ArrayList<>(uniqueCategories);

                // Post the combined list to the MutableLiveData
                combinedCategoriesLiveData.postValue(finalCategoryList);
            }
        });

        // Assign the combined LiveData to the public field
        this.allCategoriesCombined = combinedCategoriesLiveData;
        // --- End Modified Section ---

        executor = Executors.newFixedThreadPool(2);
        Log.d(TAG, "QuoteRepository initialized.");
    }

    // --- Public methods for ViewModel to interact with (insert, update, delete remain the same) ---

    public void insert(Quote quote) {
        executor.execute(() -> {
            try {
                quoteDao.insert(quote);
                Log.d(TAG, "Quote inserted: " + quote.getText());
            } catch (Exception e) {
                Log.e(TAG, "Error inserting quote: " + quote.getText(), e);
            }
        });
    }

    public void update(Quote quote) {
        executor.execute(() -> {
            try {
                quoteDao.update(quote);
                Log.d(TAG, "Quote updated: " + quote.getText());
            } catch (Exception e) {
                Log.e(TAG, "Error updating quote: " + quote.getText(), e);
            }
        });
    }

    public void delete(Quote quote) {
        executor.execute(() -> {
            try {
                quoteDao.delete(quote);
                Log.d(TAG, "Quote deleted: " + quote.getText());
            } catch (Exception e) {
                Log.e(TAG, "Error deleting quote: " + quote.getText(), e);
            }
        });
    }

    // --- LiveData Getters for Observing Data ---
    // Keep the original getters for quotes (they should filter out dummies if QuoteDao was updated)
    public LiveData<List<Quote>> getAllQuotes() {
        return allQuotes;
    }

    public LiveData<List<Quote>> getFavoriteQuotes() {
        return favoriteQuotes;
    }

    // --- Modified Getter ---
    // Return the combined list of categories
    public LiveData<List<String>> getAllCategories() {
        // Return the combined LiveData instead of the raw database one
        return allCategoriesCombined;
        // Original: return databaseCategories;
    }
    // --- End Modified Getter ---

    public LiveData<List<Quote>> searchQuotes(String query) {
        // Make sure this filters out dummies too if QuoteDao was updated
        return quoteDao.searchQuotes(query);
    }

    public LiveData<List<Quote>> getQuotesByCategory(String category) {
        // Make sure this filters out dummies too if QuoteDao was updated
        return quoteDao.getQuotesByCategory(category);
    }
}