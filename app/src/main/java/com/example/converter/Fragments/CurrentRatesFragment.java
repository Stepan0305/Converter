package com.example.converter.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.converter.Adapters.AdapterCurrentRate;
import com.example.converter.Models.DbHelper;
import com.example.converter.Models.Rate;
import com.example.converter.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CurrentRatesFragment extends Fragment {
    public static final String LINK = "https://www.cbr-xml-daily.ru/daily_json.js";
    RecyclerView recyclerView;
    AdapterCurrentRate adapter;
    ProgressDialog pd;
    public static Fragment newInstance() {
        return new CurrentRatesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_rates_fragment, container, false);
        DbHelper dbHelper = new DbHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerCurrentRates);
        boolean connected;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE); //проверка, есть ли интернет
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else connected = false;
        if (connected) {
            try {
                MyJsonTask task = new MyJsonTask();
                task.execute(LINK);
                String res = task.get();
                JSONObject object = new JSONObject(res);
                JSONObject valute = object.getJSONObject("Valute");
                Rate rate = new Rate();
                rate.setUSD(valute.getJSONObject("USD").getDouble("Value"));
                rate.setEUR(valute.getJSONObject("EUR").getDouble("Value"));
                rate.setJPY100(valute.getJSONObject("JPY").getDouble("Value"));
                dbHelper.setCurrentRate(rate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Rate rate = dbHelper.getCurrentRate();
        ArrayList<Rate> rates = new ArrayList<>();
        Rate rate1 = new Rate(), rate2 = new Rate(), rate3 = new Rate();
        rate1.setUSD(rate.getUSD());
        rate2.setEUR(rate.getEUR());
        rate3.setJPY100(rate.getJPY100());
        rates.add(rate1);
        rates.add(rate2);
        rates.add(rate3);  //костыль для адаптера
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AdapterCurrentRate(getContext(), rates);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private class MyJsonTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getContext());
            pd.setMessage("Пожалуйста, подождите");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                String buffer = "";
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer += (line + "\n");
                }
                return buffer;

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if ((pd!= null) && pd.isShowing()) {
                    pd.dismiss();
                }
            }  catch (final Exception e) {
            } finally {
                pd = null;
            }
        }
    }
}
