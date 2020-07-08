package com.example.converter.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.converter.Models.DbHelper;
import com.example.converter.Models.Exchange;
import com.example.converter.Models.Rate;
import com.example.converter.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class ConverterFragment extends Fragment {
    //https://www.cbr-xml-daily.ru//archive//2020//01//18//daily_json.js  пример ссылки
    public static final String LINK_BEGINNING = "https://www.cbr-xml-daily.ru//archive//";
    public static final String LINK_END = "daily_json.js";
    EditText editText;
    TextView textView;
    public TextView date;
    String currencyInput = "USD", currencyOutput = "RUB";
    Button input, output, changeDate, saveExchange;
    DbHelper dbHelper;
    int type;  //1 input 2 output костыль, необходимый для того, чтоб сделать 2 меню на экране
    static long dateInMillis = System.currentTimeMillis();  //меняется при изменении даты
    static boolean hasDateChanged;
    // static final long currentMillis = System.currentTimeMillis();
    static Rate currentRate;
    ProgressDialog pd;

    //статические методы для календаря и для загрузки фрагмента
    public static Fragment newInstance() {
        hasDateChanged = false;
        return new ConverterFragment();
    }

    public static void setDateInMillis(long millis) {
        dateInMillis = millis;
        hasDateChanged = true;
    }

    //функция, вызываемая при отрисовке фрагмента
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.converter_fragment, container, false);
        dbHelper = new DbHelper(getContext());
        System.out.println(dateInMillis);
        currentRate = dbHelper.getCurrentRate();
        input = view.findViewById(R.id.buttonTop);
        output = view.findViewById(R.id.buttonBottom);
        editText = view.findViewById(R.id.inputSum);
        textView = view.findViewById(R.id.outputSum);
        changeDate = view.findViewById(R.id.changeDate);
        saveExchange = view.findViewById(R.id.save);
        date = view.findViewById(R.id.dateOnConverter);
        date.setText(convertMillisToString(dateInMillis));
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean connected;
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE); //проверка, есть ли интернет
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    connected = true;
                } else connected = false;
                if (connected) {
                    DialogFragment fragment = new com.example.converter.Fragments.DatePicker();
                    fragment.show(getActivity().getSupportFragmentManager(), "Выберите дату");
                } else
                    Toast.makeText(getContext(), "Отсутствует подключение к Интернету", Toast.LENGTH_LONG).show();
            }
        });
        saveExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double inputSum = Double.parseDouble(editText.getText().toString());
                    double outputSum = Double.parseDouble(textView.getText().toString());
                    int inputCurrency = Exchange.getIntCodeByString(currencyInput);
                    int outputCurrency = Exchange.getIntCodeByString(currencyOutput);
                    System.out.println(dateInMillis);
                    Exchange exchange = new Exchange(inputCurrency, outputCurrency, inputSum,
                            outputSum, dateInMillis / 1000, System.currentTimeMillis() / 1000);
                    dbHelper.addToHistory(exchange);
                    Toast.makeText(getContext(), "Обмен сохранен в историю", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Данные введены невнрно", Toast.LENGTH_LONG).show();
                }
            }
        });
        registerForContextMenu(input);
        registerForContextMenu(output);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1;
                getActivity().openContextMenu(input);
            }
        });
        output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 2;
                getActivity().openContextMenu(output);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!hasDateChanged) {
                    try {
                        String formattedDouble = String.format("%.2f", convertCurrency());
                        System.out.println(dateInMillis);
                        textView.setText(formattedDouble);
                    } catch (NumberFormatException e) {
                        textView.setText(0.0 + "");
                    }
                } else {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy//MM//dd//");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(dateInMillis);
                        String link = LINK_BEGINNING + sdf.format(calendar.getTime()) + LINK_END;
                        setCurrentRate(link);
                        String formattedDouble = String.format("%.2f", convertCurrency());
                        textView.setText(formattedDouble);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Курс на выбранную дату отсутствует",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, 0, "USD");
        menu.add(0, 2, 0, "RUB");
        menu.add(0, 3, 0, "EUR");
        menu.add(0, 4, 0, "JPY");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String text = item.getTitle().toString();
        if (type == 1) {
            currencyInput = text;
            input.setText(text);
        } else if (type == 2) {
            currencyOutput = text;
            output.setText(text);
        }
        return super.onContextItemSelected(item);
    }

    //мои функции по конвертации валют и установке курса, который будет считаться
    public double convertCurrency() {
        double curInp = currentRate.getCostByCode(currencyInput);
        double curOut = currentRate.getCostByCode(currencyOutput);
        return Double.parseDouble(editText.getText().toString()) * (curInp / curOut);
    }

    public void setCurrentRate(String link) throws Exception {
        MyJsonTask task = new MyJsonTask();
        task.execute(link);
        String res = task.get();
        JSONObject object = new JSONObject(res);
        JSONObject valute = object.getJSONObject("Valute");
        JSONObject objectUSD = valute.getJSONObject("USD");
        JSONObject objectEUR = valute.getJSONObject("EUR");
        JSONObject objectJPY = valute.getJSONObject("JPY");
        currentRate = new Rate(objectEUR.getDouble("Value"), objectUSD.getDouble("Value"),
                objectJPY.getDouble("Value"));
        hasDateChanged = false;
    }

    public String convertMillisToString(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("Дата: dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return sdf.format(calendar.getTime());
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
                if ((pd != null) && pd.isShowing()) {
                    pd.dismiss();
                }
            } catch (final Exception e) {
            } finally {
                pd = null;
            }
        }
    }
}
