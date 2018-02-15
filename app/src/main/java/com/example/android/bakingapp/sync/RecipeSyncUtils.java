package com.example.android.bakingapp.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public class RecipeSyncUtils {
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, RecipeSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
