package com.qwen.bookqoutecollectot.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
 // Adjust import

import com.qwen.bookqoutecollectot.model.Quote;

import java.util.List;

@Dao
public interface QuoteDao {

    @Insert
    void insert(Quote quote);

    @Update
    void update(Quote quote);

    @Delete
    void delete(Quote quote);

    // Get all *real* quotes (exclude dummy placeholders if they exist)
    // Assumes dummy quotes have text starting with "[Category Placeholder"
    @Query("SELECT * FROM quotes WHERE text NOT LIKE '[Category Placeholder %]' ORDER BY id ASC")
    LiveData<List<Quote>> getAllQuotes();

    // Get all *real* favorite quotes (exclude dummy placeholders)
    @Query("SELECT * FROM quotes WHERE isFavorite = 1 AND text NOT LIKE '[Category Placeholder %]'")
    LiveData<List<Quote>> getFavoriteQuotes();

    // Search *real* quotes (exclude dummy placeholders)
    @Query("SELECT * FROM quotes WHERE text NOT LIKE '[Category Placeholder %]' AND (text LIKE '%' || :searchQuery || '%' OR author LIKE '%' || :searchQuery || '%' OR bookTitle LIKE '%' || :searchQuery || '%')")
    LiveData<List<Quote>> searchQuotes(String searchQuery);

    // Get quotes by category, excluding dummies
    @Query("SELECT * FROM quotes WHERE text NOT LIKE '[Category Placeholder %]' AND category = :category")
    LiveData<List<Quote>> getQuotesByCategory(String category);

    // Get distinct categories from *real* quotes (this determines what's in databaseCategories LiveData)
    @Query("SELECT DISTINCT category FROM quotes WHERE category IS NOT NULL AND category != '' AND text NOT LIKE '[Category Placeholder %]'")
    LiveData<List<String>> getAllCategories();

    // Synchronous query for Room Callback or internal checks (if needed)
    @Query("SELECT DISTINCT category FROM quotes WHERE category IS NOT NULL AND category != '' AND text NOT LIKE '[Category Placeholder %]'")
    List<String> getAllCategoriesSync();
}