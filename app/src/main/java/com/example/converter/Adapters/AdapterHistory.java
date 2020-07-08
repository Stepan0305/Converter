package com.example.converter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.converter.Models.Exchange;
import com.example.converter.Models.Rate;
import com.example.converter.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {
    private LayoutInflater inflater;
    private List<Exchange> cards;

    public AdapterHistory(Context context, List<Exchange> exchanges){
        this.inflater = LayoutInflater.from(context);
        this.cards = exchanges;
    }

    @NonNull
    @Override
    public AdapterHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistory.ViewHolder holder, int position) {
        Exchange exchange=cards.get(position);
        switch (exchange.getCurrencyInput()){
            case 1:
                holder.flagInput.setImageResource(R.drawable.russia_flag);
                holder.codeInput.setText("RUB");
                break;
            case 2:
                holder.flagInput.setImageResource(R.drawable.usa_flag);
                holder.codeInput.setText("USD");
                break;
            case 3:
                holder.flagInput.setImageResource(R.drawable.es_flag);
                holder.codeInput.setText("EUR");
                break;
            case 4:
                holder.flagInput.setImageResource(R.drawable.japan_flag);
                holder.codeInput.setText("JPY");
                break;
        }
        switch (exchange.getCurrencyOutput()){
            case 1:
                holder.flagOutput.setImageResource(R.drawable.russia_flag);
                holder.codeOutput.setText("RUB");
                break;
            case 2:
                holder.flagOutput.setImageResource(R.drawable.usa_flag);
                holder.codeOutput.setText("USD");
                break;
            case 3:
                holder.flagOutput.setImageResource(R.drawable.es_flag);
                holder.codeOutput.setText("EUR");
                break;
            case 4:
                holder.flagOutput.setImageResource(R.drawable.japan_flag);
                holder.codeOutput.setText("JPY");
                break;
        }
        holder.sumInput.setText(exchange.getInputSum()+"");
        holder.sumOutput.setText(exchange.getOutputSum()+"");
        holder.date.setText(convertMillisToString(exchange.getDateOfRate()*1000));
    }
    public String convertMillisToString(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("Дата: dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return sdf.format(calendar.getTime());
    }
    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sumInput, sumOutput, date, codeInput, codeOutput;
        ImageView flagInput, flagOutput;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            sumInput = itemView.findViewById(R.id.sumInput);
            sumOutput = itemView.findViewById(R.id.sumOutput);
            date = itemView.findViewById(R.id.dateHistory);
            codeInput = itemView.findViewById(R.id.currencyCodeInput);
            codeOutput = itemView.findViewById(R.id.currencyCodeOutput);
            flagInput = itemView.findViewById(R.id.flagInput);
            flagOutput = itemView.findViewById(R.id.flagOutput);
        }
    }
}
