package com.example.itsme.richnoteclient;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class updateBackEnd extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public updateBackEnd(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
