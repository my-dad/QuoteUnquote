package com.qwen.bookqoutecollectot;

import static com.qwen.bookqoutecollectot.util.Consts.EDIT_QUOTE_REQUEST;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_AUTHOR;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_BOOK_TITLE;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_CATEGORY;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_ID;
import static com.qwen.bookqoutecollectot.util.Consts.EXTRA_QUOTE_TEXT;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.qwen.bookqoutecollectot.model.Quote;
import com.qwen.bookqoutecollectot.viewmodel.QuoteViewModel;
import com.qwen.quoteunquote.R;
import com.qwen.quoteunquote.databinding.ActivityQuoteDetailBinding;

public class QuoteDetailActivity extends AppCompatActivity {
    private ActivityQuoteDetailBinding binding;

    private QuoteViewModel quoteViewModel;
    private Quote currentQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuoteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        quoteViewModel = new ViewModelProvider(this).get(QuoteViewModel.class);

        Intent intent = getIntent();
        int quoteId = intent.getIntExtra(EXTRA_QUOTE_ID, -1);

        if (quoteId != -1) {
            // Load quote details using LiveData (observe once)
            quoteViewModel.getAllQuotes().observe(this, quotes -> {
               for(Quote quote : quotes) {
                   if(quote.getId() == quoteId) {
                       currentQuote = quote;
                       populateUI();
                       break;
                   }
               }
            });
        } else {
            // Handle error or finish activity
            Toast.makeText(this, "Error loading quote", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.buttonEdit.setOnClickListener(v -> {
            if (currentQuote != null) {
                Intent editIntent = new Intent(QuoteDetailActivity.this, AddEditQuoteActivity.class);
                editIntent.putExtra(EXTRA_QUOTE_ID, currentQuote.getId());
                editIntent.putExtra(EXTRA_QUOTE_TEXT, currentQuote.getText());
                editIntent.putExtra(EXTRA_QUOTE_AUTHOR, currentQuote.getAuthor());
                editIntent.putExtra(EXTRA_QUOTE_BOOK_TITLE, currentQuote.getBookTitle());
                editIntent.putExtra(EXTRA_QUOTE_CATEGORY, currentQuote.getCategory());
                startActivityForResult(editIntent, EDIT_QUOTE_REQUEST);
            }
        });

        binding.buttonDelete.setOnClickListener(v -> {
            if (currentQuote != null) {
                quoteViewModel.delete(currentQuote);
                Toast.makeText(QuoteDetailActivity.this, "Quote deleted", Toast.LENGTH_SHORT).show();
                finish(); // Close detail activity
            }
        });

        binding.imageViewDetailFavorite.setOnClickListener(v -> {
             if (currentQuote != null) {
                 currentQuote.setFavorite(!currentQuote.isFavorite());
                 quoteViewModel.update(currentQuote);
                 updateFavoriteIcon(); // Update UI immediately
                 // Optional: Toast or Snackbar feedback
             }
         });
    }

    private void populateUI() {
        if (currentQuote != null) {
            binding.textViewDetailQuote.setText(currentQuote.getText());
            binding.textViewDetailAuthor.setText(currentQuote.getAuthor());
            binding.textViewDetailAuthor.setText(currentQuote.getBookTitle());
            binding.textViewDetailCategory.setText(currentQuote.getCategory() != null ? currentQuote.getCategory() : "No Category");
            updateFavoriteIcon();
        }
    }

    private void updateFavoriteIcon() {
         if (currentQuote != null) {
             if (currentQuote.isFavorite()) {
                 binding.imageViewDetailFavorite.setImageResource(R.drawable.ic_star);
                 binding.imageViewDetailFavorite.setColorFilter(getResources().getColor(R.color.favorite_color));
             } else {
                 binding.imageViewDetailFavorite.setImageResource(R.drawable.ic_star_border);
                 binding.imageViewDetailFavorite.clearColorFilter();
             }
         }
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_QUOTE_REQUEST && resultCode == RESULT_OK) {
            // Refresh data if quote was edited
             if (data != null) {
                 currentQuote.setText(data.getStringExtra(EXTRA_QUOTE_TEXT));
                 currentQuote.setAuthor(data.getStringExtra(EXTRA_QUOTE_AUTHOR));
                 currentQuote.setBookTitle(data.getStringExtra(EXTRA_QUOTE_BOOK_TITLE));
                 String category = data.getStringExtra(EXTRA_QUOTE_CATEGORY);
                 currentQuote.setCategory(category); // Handles null correctly
                 populateUI(); // Update displayed data
                 Toast.makeText(this, "Quote updated", Toast.LENGTH_SHORT).show();
             }
        }
    }
}