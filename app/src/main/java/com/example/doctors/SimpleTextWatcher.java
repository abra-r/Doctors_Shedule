package com.example.doctors;

import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
