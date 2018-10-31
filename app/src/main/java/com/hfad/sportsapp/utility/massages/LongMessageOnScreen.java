package com.hfad.sportsapp.utility.massages;

import android.content.Context;
import android.widget.Toast;

import com.hfad.sportsapp.R;

public class LongMessageOnScreen  extends MessageOnScreen {

    private static final int EMPTY_STRING = R.string.emptyString;

    private Context context;
    private int length;

    public LongMessageOnScreen(Context context, int length) {
        this.context = context;
        this.length = length;
    }

    @Override
    public void show(int stringId) {
        Toast toast = Toast.makeText(context, stringId, length);
        toast.show();
    }

    @Override
    public void show(String msg) {
        Toast toast = Toast.makeText(context, msg, length);
        toast.show();
    }
}
