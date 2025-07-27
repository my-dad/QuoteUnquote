package com.qwen.bookqoutecollectot.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.qwen.bookqoutecollectot.model.Quote;

@Database(entities = {Quote.class}, version = 1, exportSchema = false)
public abstract class QuoteDatabase extends RoomDatabase {

    private static volatile QuoteDatabase INSTANCE;
    // public static final List<String> DEFAULT_CATEGORIES = Arrays.asList(...); // Can remove

    public abstract QuoteDao quoteDao();

    // private static final ExecutorService databaseWriteExecutor = ...; // Can remove
    // private static final RoomDatabase.Callback roomCallback = ...; // Can remove

    public static QuoteDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (QuoteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    QuoteDatabase.class, "quote_database")
                            .fallbackToDestructiveMigration()
                            // .addCallback(roomCallback) // Remove or comment out
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // private static void populateDefaultCategories(QuoteDao dao) { ... } // Can remove
}