package com.mrtrying.AutoSeparateEditText;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.edit_text);
        AutoSeparateTextWatcher textWatcher = new AutoSeparateTextWatcher(editText);
        textWatcher.setRULES(new int[]{3,4,4});
        textWatcher.setSeparator('-');
        editText.addTextChangedListener(textWatcher);
    }

}
