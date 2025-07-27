package com.qwen.bookqoutecollectot;

import static com.qwen.bookqoutecollectot.util.Consts.ADD_QUOTE_REQUEST;
import static com.qwen.bookqoutecollectot.util.Consts.EDIT_QUOTE_REQUEST;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_ID;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.qwen.bookqoutecollectot.adapter.QuoteAdapter;
import com.qwen.bookqoutecollectot.model.Quote;
import com.qwen.bookqoutecollectot.viewmodel.QuoteViewModel;
import com.qwen.quoteunquote.R;
import com.qwen.quoteunquote.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private QuoteViewModel quoteViewModel;
    private QuoteAdapter adapter;
    private boolean isShowingFavorites = false; // Flag to track current view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        binding.fabAddQuote.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditQuoteActivity.class);
            startActivityForResult(intent, ADD_QUOTE_REQUEST);
        });

        binding.recyclerViewQuotes.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewQuotes.setHasFixedSize(true); // If item size is fixed

        adapter = new QuoteAdapter();
        binding.recyclerViewQuotes.setAdapter(adapter);

        quoteViewModel = new ViewModelProvider(this).get(QuoteViewModel.class);

        // Initially observe all quotes
        quoteViewModel.getAllQuotes().observe(this, quotes -> {
            adapter.submitList(quotes);
            updateEmptyView(quotes);
        });

        setupRecyclerView();
        setupSearchAndFilter();
    }

    private void setupRecyclerView() {
        adapter.setOnItemClickListener(new QuoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Quote quote) {
                Intent intent = new Intent(MainActivity.this, QuoteDetailActivity.class);
                intent.putExtra(EXTRA_QUOTE_ID, quote.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Quote quote, int position) {
                quote.setFavorite(!quote.isFavorite());
                quoteViewModel.update(quote);
                // Update UI immediately in adapter without waiting for DB callback
                adapter.notifyItemChanged(position);
            }
        });

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // We don't want drag & drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Quote quoteToDelete = adapter.getQuoteAt(position);
                quoteViewModel.delete(quoteToDelete);

                // Show Snackbar with Undo
                Snackbar snackbar = Snackbar.make(binding.recyclerViewQuotes, "Quote deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", view -> {
                            quoteViewModel.insert(quoteToDelete); // Re-insert
                        });
                snackbar.show();
            }
        }).attachToRecyclerView(binding.recyclerViewQuotes);
    }

    private void setupSearchAndFilter() {

        // Setup Filter Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.filterSpinner.setAdapter(spinnerAdapter);

        // Load categories from ViewModel
        quoteViewModel.getAllCategories().observe(this, categories -> {
            List<String> categoryList = new ArrayList<>(categories);
            categoryList.add(0, "All Categories"); // Add default option
            ArrayAdapter<String> updatedAdapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_spinner_item, categoryList);
            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.filterSpinner.setAdapter(updatedAdapter);
        });

        binding.filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getItemAtPosition(position);
                performSearchAndFilter(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                performSearchAndFilter("All Categories");
            }
        });

        // Setup Search
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearchAndFilter((String) binding.filterSpinner.getSelectedItem());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearchAndFilter(String category) {
        String query = binding.editTextSearch.getText().toString().trim();

        if (isShowingFavorites) {
            // If showing favorites, filter favorites
            if ("All Categories".equals(category) || category == null) {
                if (query.isEmpty()) {
                    quoteViewModel.getFavoriteQuotes().observe(this, adapter::submitList);
                } else {
                    // Implement search within favorites if needed (requires custom query)
                    // For now, just show all favorites if searching
                    quoteViewModel.getFavoriteQuotes().observe(this, adapter::submitList);
                }
            } else {
                 // Implement filtered search within favorites if needed
                 // For now, just show all favorites if filtering
                 quoteViewModel.getFavoriteQuotes().observe(this, adapter::submitList);
            }
        } else {
            // If showing all quotes, filter all
            if ("All Categories".equals(category) || category == null) {
                if (query.isEmpty()) {
                    quoteViewModel.getAllQuotes().observe(this, adapter::submitList);
                } else {
                    quoteViewModel.searchQuotes(query).observe(this, adapter::submitList);
                }
            } else {
                // Filter by category first, then apply search if needed
                // This requires a combined query in DAO or filtering logic here
                // For simplicity, let's filter by category only if no search query
                if (query.isEmpty()) {
                   quoteViewModel.getQuotesByCategory(category).observe(this, adapter::submitList);
                } else {
                    // Complex: Search within category. Requires custom DAO method or manual filtering.
                    // For basic implementation, just search everything.
                    quoteViewModel.searchQuotes(query).observe(this, adapter::submitList);
                }
            }
        }
    }

    private void updateEmptyView(List<Quote> quotes) {
        if (quotes == null || quotes.isEmpty()) {
            binding.textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.textViewEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean shouldClearFilters = false; // Flag to track if filters need clearing

        if (requestCode == ADD_QUOTE_REQUEST && resultCode == RESULT_OK) {
            // Handle successful addition if needed (e.g., show a toast)
            // Data is observed automatically via LiveData
            Toast.makeText(this, "Quote added!", Toast.LENGTH_SHORT).show();
            shouldClearFilters = true; // Clear filters after adding
        } else if (requestCode == EDIT_QUOTE_REQUEST && resultCode == RESULT_OK) {
            // Handle successful edit if needed
            Toast.makeText(this, "Quote updated!", Toast.LENGTH_SHORT).show();
            shouldClearFilters = true; // Clear filters after editing
        }

        // Reset search/filter after adding/editing to show the new/updated quote
        if (shouldClearFilters) {
            if (binding.editTextSearch != null) {
                binding.editTextSearch.setText("");
            }
            if (binding.filterSpinner != null) {
                binding.filterSpinner.setSelection(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorites) {
            toggleFavoritesView();
            return true;
        } else if (id == R.id.action_settings) { // For Dark Mode Toggle
             Intent settingsIntent = new Intent(this, SettingsActivity.class);
             startActivity(settingsIntent);
             return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleFavoritesView() {
        isShowingFavorites = !isShowingFavorites;
        if (isShowingFavorites) {
            // Switch to observing favorite quotes
            quoteViewModel.getFavoriteQuotes().observe(this, quotes -> {
                adapter.submitList(quotes);
                updateEmptyView(quotes);
            });
        } else {
            // Switch back to observing all quotes (filtered/searched)
            performSearchAndFilter((String) binding.filterSpinner.getSelectedItem());
        }
    }
}