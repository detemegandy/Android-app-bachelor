package com.example.itsme.firebasenotificationexample;

import android.support.annotation.NonNull;

import androidx.work.Data;
import androidx.work.Worker;

public class SendToServerWorker extends Worker {
    private final String KEY_RESULT = "isSentToServer";
    private Data input;

    @NonNull
    @Override
    public WorkerResult doWork() {

        input = getInputData();
        return SendToServer();
    }

    private WorkerResult SendToServer() {
        Data output = new Data.Builder()
                .putString(KEY_RESULT, "true")
                .build();
        setOutputData(output);
        return WorkerResult.SUCCESS;
    }

}
