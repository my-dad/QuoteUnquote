package com.qwen.bookqoutecollectot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.qwen.bookqoutecollectot.model.Quote;
import com.qwen.quoteunquote.R;


public class QuoteAdapter extends ListAdapter<Quote, QuoteAdapter.QuoteHolder> {

    private OnItemClickListener listener;

    public QuoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Quote> DIFF_CALLBACK = new DiffUtil.ItemCallback<Quote>() {
        @Override
        public boolean areItemsTheSame(@NonNull Quote oldItem, @NonNull Quote newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Quote oldItem, @NonNull Quote newItem) {
             return oldItem.getText().equals(newItem.getText()) &&
                    oldItem.getAuthor().equals(newItem.getAuthor()) &&
                    oldItem.getBookTitle().equals(newItem.getBookTitle()) &&
                    ((oldItem.getCategory() == null && newItem.getCategory() == null) ||
                     (oldItem.getCategory() != null && oldItem.getCategory().equals(newItem.getCategory()))) &&
                    oldItem.isFavorite() == newItem.isFavorite();
        }
    };

    @NonNull
    @Override
    public QuoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);
        return new QuoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteHolder holder, int position) {
        Quote currentQuote = getItem(position);
        holder.textViewQuote.setText(currentQuote.getText());
        holder.textViewAuthor.setText(currentQuote.getAuthor());
        holder.textViewBookTitle.setText(currentQuote.getBookTitle());

        // Handle favorite icon
        if (currentQuote.isFavorite()) {
            holder.imageViewFavorite.setImageResource(R.drawable.ic_star); // Use filled star
            holder.imageViewFavorite.setColorFilter(holder.imageViewFavorite.getContext().getResources().getColor(R.color.favorite_color));
        } else {
            holder.imageViewFavorite.setImageResource(R.drawable.ic_star_border); // Use border star
            holder.imageViewFavorite.clearColorFilter(); // Reset color
        }
    }

    public Quote getQuoteAt(int position) {
        return getItem(position);
    }

    class QuoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewQuote;
        private TextView textViewAuthor;
        private TextView textViewBookTitle;
        private ImageView imageViewFavorite;

        public QuoteHolder(View itemView) {
            super(itemView);
            textViewQuote = itemView.findViewById(R.id.text_view_quote);
            textViewAuthor = itemView.findViewById(R.id.text_view_author);
            textViewBookTitle = itemView.findViewById(R.id.text_view_book_title);
            imageViewFavorite = itemView.findViewById(R.id.image_view_favorite);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });

            imageViewFavorite.setOnClickListener(v -> {
                 int position = getAdapterPosition();
                 if (listener != null && position != RecyclerView.NO_POSITION) {
                     listener.onFavoriteClick(getItem(position), position);
                 }
             });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Quote quote);
        void onFavoriteClick(Quote quote, int position); // For toggling favorite directly from list
        // void onDeleteClick(Quote quote, int position); // Could be handled via swipe or menu
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}