package com.example.android.bakingapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class RecipeSyncIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RecipeSyncIntentService() {
        super(RecipeSyncIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        RecipeSyncTask.syncRecipes(this);
    }
}
