package com.example.finalandroidproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private DatePicker datePicker;
    private ImageView imageView;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // button to return to ListView 1st page
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Either from launch screen or date picker depending which ListView chosen
        String initialDate = getIntent().getStringExtra("selected_date");


        if(initialDate != null) {
            // Auto-fetch if date was skipped
            new FetchImageData().execute(initialDate);


        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressBar = findViewById(R.id.progressBar);
        datePicker = findViewById(R.id.datePicker);
        imageView = findViewById(R.id.ImageView);
        titleTextView = findViewById(R.id.news_title);
        progressBar.setVisibility(View.GONE);




        datePicker.setMaxDate(System.currentTimeMillis()); // ensure we can't go past current date so no potential error

        // Button above progress bar to onClick
        findViewById(R.id.DatePickerbutton).setOnClickListener(v -> {

            // We then get the selected date from DatePicker
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1; // months are 0-based
            int day = datePicker.getDayOfMonth();
            String selectedDate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);

            //  Start the AsyncTask to fetch the NASA data
            new FetchImageData().execute(selectedDate);
        });
    }

    // Help Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_help) {
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Help Dialog itself
    private void showHelpDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("How to Use This Page")
                .setMessage("This screen displays a NASA picture of the day\n\n" +
                        "- An image will be selected based on selected date.\n" +
                        "- The image will appear along with its official NASA date\n" +
                        "- Tap the back button to return to the main screen.")
                .setPositiveButton("Got it!", null)
                .show();
    }

    // AsyncTask to fetch the NASA image data
    private class FetchImageData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE); // Show progress bar after GO
            // this was from very first version, at this point with all the changes it didn't get a chance to be updated/fixed.
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }


        @Override
        protected String doInBackground(String... params) {
            String date = params[0];
            String urlString = "https://api.nasa.gov/planetary/apod?api_key=hhIb4ToWIXllAMo1KWXja5f4AoFFhD3XbfIYvZqK&date=" + date;
            // my API key

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();
                    return stringBuilder.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();//catch
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);

            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String mediaType = jsonResponse.getString("media_type");

                    if (!mediaType.equals("image")) {
                        Toast.makeText(MainActivity.this,
                                "The selected date has a video instead of an image",
                                // hasn't occured yet but not sure if the API is even images only
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String imageUrl = jsonResponse.getString("url");
                    String title = jsonResponse.getString("title");
                    String date = jsonResponse.getString("date");

                    // Load image with Picasso
                    // Tried a few dependencies but could only get Picasso to actually work.
                    // API did not work on it's own it was unable to properly render the images
                    Picasso.get()
                            .load(imageUrl)
                            .error(android.R.drawable.ic_dialog_alert)
                            .into(imageView);

                    titleTextView.setText(String.format("%s (%s)", title, date));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,
                            "Error parsing NASA data",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this,
                        "Failed to fetch data. Check your connection.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}