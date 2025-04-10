package com.example.finalandroidproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDateView extends AppCompatActivity {

    private ImageView imageView;
    private TextView descriptionView;

    // any items pasted over from MainActivity have the better descriptions over there.
    private DescriptionFragment descriptionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_date_view);
        if (savedInstanceState == null) {
            DescriptionFragment descriptionFragment = DescriptionFragment.newInstance("Here’s today’s NASA image description!");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.description_fragment_container, descriptionFragment)
                    .commit();





            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            this.descriptionFragment = descriptionFragment;
        }

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Initialize views
        imageView = findViewById(R.id.random_image);



        String randomDate = generateRandomDate();
        new FetchImageData().execute(randomDate);
    }

    // Generating a random date for Random Date option
    private String generateRandomDate() {
        // FunFact: NASA API supports dates starting from 1995-06-16
        Calendar start = new GregorianCalendar(1995, Calendar.JUNE, 16);
        Calendar end = Calendar.getInstance(); // today

        long startMillis = start.getTimeInMillis();
        long endMillis = end.getTimeInMillis();

        long randomMillis = ThreadLocalRandom.current().nextLong(startMillis, endMillis);
        Date randomDate = new Date(randomMillis);

        return new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(randomDate);
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
                .setMessage("This screen displays a random NASA Astronomy Picture of the Day.\n\n" +
                        "- An image will be randomly selected from past dates.\n" +
                        "- The image will appear along with its official NASA description.\n" +
                        "- Tap the back button to return to the main screen.")
                .setPositiveButton("Got it!", null)
                .show();
    }








    // Rather then being smart and creating a modular object/class I have just copied and pasted the AsyncTask
    // Ideally this would not be the case.
    // AsyncTask to fetch the NASA image data
    private class FetchImageData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        @Override
        protected String doInBackground(String... params) {
            String date = params[0];
            String urlString = "https://api.nasa.gov/planetary/apod?api_key=hhIb4ToWIXllAMo1KWXja5f4AoFFhD3XbfIYvZqK&date=" + date;
                             // ^^ API Key
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
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String mediaType = jsonResponse.getString("media_type");

                    if (!mediaType.equals("image")) {
                        Toast.makeText(RandomDateView.this,
                                "The selected date has a video instead of an image", // idk if Nasa API includes videos but ya never know
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String imageUrl = jsonResponse.getString("url");
                    String title = jsonResponse.getString("title");
                    String date = jsonResponse.getString("date"); // Date and Title are in Mainactivity. Too Cluttery with Date added too.
                    String description = jsonResponse.getString("explanation");

                    if (descriptionFragment != null) {
                        descriptionFragment.updateDescription(description);
                    }

                    // Load image with Picasso // More details in MainActivity
                    Picasso.get()
                            .load(imageUrl)
                            .error(android.R.drawable.ic_dialog_alert)
                            .into(imageView);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RandomDateView.this,
                            "Error parsing NASA data",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RandomDateView.this,
                        "Failed to fetch data. Check your connection.",
                        Toast.LENGTH_LONG).show();
            }
        }



    }
}