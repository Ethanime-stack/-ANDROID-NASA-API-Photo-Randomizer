package com.example.finalandroidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class LaunchListActivity2 extends AppCompatActivity {

    // This will be the new launch activity.
    // To fulfill the requirement of a listview, I will add 2 options which go to the already working model or random picture

    private EditText editText; //Bottom edittext for bottomnav bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_list2);

        ListView optionsList = findViewById(R.id.optionsListView);


        editText = findViewById(R.id.editText);

// Load the EditText
        String savedText = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getString("user_input", "");
        editText.setText(savedText);


        // Show Toast when EditText clicked
        findViewById(R.id.editText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LaunchListActivity2.this, "Start Typing!", Toast.LENGTH_SHORT).show();
            }




        });



        // Show Snackbar when title is clicked
        findViewById(R.id.toolbar_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "THE REAL NASA!", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
            }
        });

        // Array of our list options. Could add more, but I think this suits for now.
        String[] options = {
                "📅 Select Specific Date",
                "🎲 Random Space Image"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                options
        );



        optionsList.setAdapter(adapter);

        optionsList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent;
            switch(position) {
                case 0: // Date picker
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    break;
                case 1: // Random image
                    intent = new Intent(this, RandomDateView.class);
                    startActivity(intent);
                    break;
            }
        });
    }

    // Saving the EditText onPause
    @Override
    protected void onPause() {
        super.onPause();
        String userInput = editText.getText().toString();
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .edit()
                .putString("user_input", userInput)
                .apply();
    }
}
