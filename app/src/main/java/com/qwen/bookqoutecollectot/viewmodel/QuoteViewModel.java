package com.qwen.bookqoutecollectot.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.qwen.bookqoutecollectot.model.Quote;
import com.qwen.bookqoutecollectot.repository.QuoteRepository;

import java.util.List;

public class QuoteViewModel extends AndroidViewModel {

    private QuoteRepository repository;
    private LiveData<List<Quote>> allQuotes;
    private LiveData<List<Quote>> favoriteQuotes;
    private LiveData<List<String>> allCategories;

    public QuoteViewModel(@NonNull Application application) {
        super(application);
        repository = new QuoteRepository(application);
        allQuotes = repository.getAllQuotes();
        favoriteQuotes = repository.getFavoriteQuotes();
        allCategories = repository.getAllCategories();
    }

    public void insert(Quote quote) {
        repository.insert(quote);
    }

    public void update(Quote quote) {
        repository.update(quote);
    }

    public void delete(Quote quote) {
        repository.delete(quote);
    }

    public LiveData<List<Quote>> getAllQuotes() {
        return allQuotes;
    }

    public LiveData<List<Quote>> getFavoriteQuotes() {
        return favoriteQuotes;
    }

    public LiveData<List<String>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<Quote>> searchQuotes(String query) {
       return repository.searchQuotes(query);
    }

     public LiveData<List<Quote>> getQuotesByCategory(String category) {
        return repository.getQuotesByCategory(category);
    }
}