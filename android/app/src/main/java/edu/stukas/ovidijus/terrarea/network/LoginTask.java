package edu.stukas.ovidijus.terrarea.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Ovidijus Stukas
 */
public class LoginTask extends AsyncTask<String, Void, JsonObject> {
    private Context context;

    public LoginTask(Context context) {
        this.context = context;
    }

    @Override
    protected JsonObject doInBackground(String... strings) {
        if (strings.length < 1)
            return null;

        try {
            URL url = new URL(strings[0]);

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            final int responseCode = connection.getResponseCode();

            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (responseCode == HttpURLConnection.HTTP_OK)
                        Toast.makeText(context, "Prisijunkta", Toast.LENGTH_SHORT).show();
                    else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
                        Toast.makeText(context, "Bloga prisijungimo informacija", Toast.LENGTH_SHORT).show();
                }
            });

            connection.disconnect();
        } catch (IOException e) {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Įvyko vidinė klaida", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return null;
    }
}
