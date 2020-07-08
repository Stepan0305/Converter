package com.example.converter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.converter.Models.Rate;
import com.example.converter.R;

import java.util.List;

public class AdapterCurrentRate extends RecyclerView.Adapter<AdapterCurrentRate.ViewHolder> {
    private LayoutInflater inflater;
    private List<Rate> cards;

    public AdapterCurrentRate(Context context, List<Rate> cards) {
        this.inflater = LayoutInflater.from(context);
        this.cards = cards;
    }

    @NonNull
    @Override
    public AdapterCurrentRate.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.rate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCurrentRate.ViewHolder holder, int position) {
        Rate rate=cards.get(position);
        if (rate.getUSD()!=0.0){        //проверка, какая валюта на элементе
            holder.flagCurrencyCountry.setImageResource(R.drawable.usa_flag);
            holder.currencyNominal.setText(1+"");
            holder.exchangedSum.setText(cards.get(position).getUSD()+"");
            holder.currencyCode.setText("USD");
        } else if (rate.getEUR()!=0.0){
            holder.flagCurrencyCountry.setImageResource(R.drawable.es_flag);
            holder.currencyNominal.setText(1+"");
            holder.exchangedSum.setText(cards.get(position).getEUR()+"");
            holder.currencyCode.setText("EUR");
        } else if (rate.getJPY100()!=0.0){
            holder.flagCurrencyCountry.setImageResource(R.drawable.japan_flag);
            holder.currencyNominal.setText(100+"");
            holder.exchangedSum.setText(cards.get(position).getJPY100()+"");
            holder.currencyCode.setText("JPY");
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView currencyNominal, exchangedSum, currencyCode;
        ImageView flagCurrencyCountry;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            currencyNominal=itemView.findViewById(R.id.currencyNominal);
            exchangedSum=itemView.findViewById(R.id.rublesCount);
            flagCurrencyCountry=itemView.findViewById(R.id.currencyCountryFlag);
            currencyCode=itemView.findViewById(R.id.currencyCode);
        }
    }
}
