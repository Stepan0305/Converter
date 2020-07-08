package com.example.converter.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.converter.Adapters.AdapterHistory;
import com.example.converter.Models.DbHelper;
import com.example.converter.R;

public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterHistory adapter;
    public static Fragment newInstance(){
        return new HistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.history_fragment, container, false);
        DbHelper dbHelper = new DbHelper(getContext());
        recyclerView=view.findViewById(R.id.recyclerHistory);
        adapter=new AdapterHistory(getContext(), dbHelper.getAllHistory());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }
}
