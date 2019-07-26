package com.example.bookapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final String TAB = MainActivity.class.getSimpleName();
    private static final String BASE_URL = "https://www.googleapis.com/books/";

    private RecyclerView mRecycleView;
    private EditText mSearchEditText;

    private BookAdapter mRecycleAdapter;

    private ProgressBar mProgressBar;
    private Button mSearchButton;
    private ArrayList<Book> books = new ArrayList<>();
    private TextView mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecycleView = findViewById(R.id.my_recycler_view);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setHasFixedSize(true);
        mSearchButton = findViewById(R.id.btnsearch);
        mSearchEditText = findViewById(R.id.etSearch);
        mConnection = findViewById(R.id.textView);
        mConnection.setVisibility(View.INVISIBLE);

        if (CheckInterConnection(getApplication())) {

            mSearchButton.setOnClickListener((View Button) -> {


                String getsearchname = mSearchEditText.getText().toString().trim();

                getsearchname.replace(" ", "+");

                if (!getsearchname.matches("")) {

                    if (books != null) {
                        books.clear();
                    }

                    new BooklistAsyncTask(getsearchname, getApplication()).execute();

                    hidekeyboard();
                } else {
                    Toast.makeText(getApplication(), "Please enter Book type", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            mConnection.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.INVISIBLE);
//            mSearchEditText.setVisibility(View.INVISIBLE);
//            mSearchButton.setVisibility(View.INVISIBLE);
            mRecycleAdapter = null;
            mSearchEditText = null;
            mProgressBar = null;
            mRecycleView = null;
            mSearchButton = null;
        }
    }

    void hidekeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean CheckInterConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    class BooklistAsyncTask extends AsyncTask<ArrayList<Book>, Void, ArrayList<Book>> {


        private String text;


        private Context context;

        BooklistAsyncTask(String name, Context context) {
            this.text = name;
            this.context = context;

        }


        @Override
        protected void onPreExecute() {

            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setMax(100);
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final ArrayList<Book> doInBackground(ArrayList<Book>... voids) {
            // Retrofit object.
            Retrofit sent = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Api apiInterface = sent.create(Api.class);

            Call<ResponseBody> getresponse = apiInterface.getbooks(text);
            getresponse.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {


                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getInt("totalItems") == 0) {
                            return;
                        }

                        JSONArray itemsArray = jsonObject.getJSONArray("items");

                        for (int i = 0; i < itemsArray.length(); i++) {

                            JSONObject currentObject = itemsArray.getJSONObject(i);

                            JSONObject volumeInfoObject = currentObject.getJSONObject("volumeInfo");


                            String title = volumeInfoObject.getString("title");

                            String[] author;

                            JSONArray authorArray = volumeInfoObject.optJSONArray("authors");

                            if (authorArray != null) {
                                ArrayList<String> list = new ArrayList<>();
                                for (int a = 0; a < authorArray.length(); a++) {
                                    list.add(authorArray.get(a).toString());
                                }
                                author = list.toArray(new String[list.size()]);
                            } else {

                                continue;
                            }

                            String description = "";

                            if (volumeInfoObject.optString("description") != null) {
                                description = volumeInfoObject.optString("description");
                            }
                            String infoLink = "";

                            if (volumeInfoObject.optString("infoLink") != null) {
                                infoLink = volumeInfoObject.optString("infoLink");
                            }

                            books.add(new Book(title, author, description, infoLink));

                            Log.i(TAB, title + Arrays.toString(author) + description + infoLink + i);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i(TAB, t.toString());
                }
            });

            return books;
        }


        @Override
        protected void onPostExecute(ArrayList<Book> list) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRecycleAdapter = new BookAdapter(context, list);
            mRecycleView.setAdapter(mRecycleAdapter);
            mRecycleAdapter.notifyDataSetChanged();
            Log.d("tag", list.size() + "");

//            mRecycleAdapter.SetOnItemClickListener(books -> {
//
////                String link = books.getInfoLink();
////                // start intent.
////                Intent i = new Intent(Intent.ACTION_VIEW);
////                i.setData(Uri.parse(link));
////                startActivity(i);
//
//            });
//            super.onPostExecute(list);
        }
    }
}