package com.qwen.bookqoutecollectot;

import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_AUTHOR;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_BOOK_TITLE;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_CATEGORY;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_ID;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_TEXT;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.qwen.bookqoutecollectot.model.Quote;
import com.qwen.bookqoutecollectot.viewmodel.QuoteViewModel;
import com.qwen.quoteunquote.R;
import com.qwen.quoteunquote.databinding.ActivityAddEditQuoteBinding;

import java.util.ArrayList;

public class AddEditQuoteActivity extends AppCompatActivity {

    private ActivityAddEditQuoteBinding binding;

    private QuoteViewModel quoteViewModel;
    private int quoteId = -1; // -1 indicates new quote

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditQuoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        quoteViewModel = new ViewModelProvider(this).get(QuoteViewModel.class);

        // Populate category suggestions
        populateCategoryAdapter();

        setupToolbar();

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_QUOTE_ID)) {
            // Editing existing quote
            setTitle("Edit Quote");
            quoteId = intent.getIntExtra(EXTRA_QUOTE_ID, -1);
            binding.editTextQuote.setText(intent.getStringExtra(EXTRA_QUOTE_TEXT));
            binding.editTextAuthor.setText(intent.getStringExtra(EXTRA_QUOTE_AUTHOR));
            binding.editTextBookTitle.setText(intent.getStringExtra(EXTRA_QUOTE_BOOK_TITLE));
            binding.autoCompleteCategory.setText(intent.getStringExtra(EXTRA_QUOTE_CATEGORY));
        } else {
            setTitle("Add Quote");
        }


        binding.buttonSave.setOnClickListener(v -> {
            saveQuote();
        });

    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarAddEdit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void populateCategoryAdapter() {
        ArrayAdapter<CharSequence> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        binding.autoCompleteCategory.setAdapter(categoryAdapter);
        quoteViewModel.getAllCategories().observe(this, categories -> {
            categoryAdapter.clear();
            categoryAdapter.addAll(categories);
        });
    }

    private void saveQuote() {
        String quoteText = binding.editTextQuote.getText().toString().trim();
        String author = binding.editTextAuthor.getText().toString().trim();
        String bookTitle = binding.editTextBookTitle.getText().toString().trim();
        String category = binding.autoCompleteCategory.getText().toString().trim(); // Or getSelectedItem if strictly using dropdown

        // Validate
        if (TextUtils.isEmpty(quoteText) || TextUtils.isEmpty(author) || TextUtils.isEmpty(bookTitle)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Quote object
        Quote quote;
        if (quoteId == -1) {
            quote = new Quote(quoteText, author, bookTitle, category.isEmpty() ? null : category);
        } else {
            quote = new Quote(quoteText, author, bookTitle, category.isEmpty() ? null : category);
            quote.setId(quoteId);
        }

        if (quoteId == -1) {
            quoteViewModel.insert(quote);
            Toast.makeText(this, "Quote added", Toast.LENGTH_SHORT).show();
        } else {
            quoteViewModel.update(quote);
            Toast.makeText(this, "Quote updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_quote_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveQuote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}