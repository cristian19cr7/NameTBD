package com.example.coinfolio.ui.Addcoins;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.coinfolio.AddCoinAdapter;
import com.example.coinfolio.Coin;
import com.example.coinfolio.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddCoinsFragment extends Fragment {
    private List<Coin> list = new ArrayList<>();
    private AddCoinAdapter addCoinAdapter = new AddCoinAdapter(list);
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            final View view = inflater.inflate(R.layout.fragment_add_coins, container, false);
            // Write a message to the database
//            FirebaseDatabase database = FirebaseDatabase.getInstance(getString(R.string.Firebase_Key_API));
//            DatabaseReference myRef = database.getReference("message");
//
//            myRef.setValue("Hello world!");
//            String[] CoinList = getCoinsList();
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item,R.id.autoComplete_dropdown, CoinList);
//            final AutoCompleteTextView textView = view.findViewById(R.id.autoCompleteTextView);
//
//            textView.setOnItemClickListener( new AdapterView.OnItemClickListener()
//            {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView , View view , int position ,long arg3)
//                {
//                    Toast.makeText(getContext(), "list clicked " + position, Toast.LENGTH_SHORT).show();
//                }
//            });
//            textView.setAdapter(adapter);
            getListOfCoins();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    RV(view);
                }
            }, 2000);

            return view;
    }

//    public String[] getCoinsList()
//    {
//        final String[] coins = new String[6174];
//        String URL = "https://api.coingecko.com/api/v3/coins/list";
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        try {
//                            JSONArray array = new JSONArray(response);
//                            for (int i = 0; i < coins.length; i++) {
//                                JSONObject temp = array.getJSONObject(i);
//                                coins[i] = temp.getString("name");
//                            }
//                            Toast.makeText(getContext(),"done getting the list", Toast.LENGTH_SHORT).show();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(),"error getting the coin lists",Toast.LENGTH_SHORT).show();
//
//            }
//        });
//        queue.add(stringRequest);
//        return coins;
//    }


    public void getListOfCoins()
    {
        String URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=250&page=1&sparkline=true";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject temp = array.getJSONObject(i);
                                Coin tempCoin = new Coin();
                                tempCoin.name = temp.getString("name");
                                tempCoin.coin_ID = temp.getString("id");
                                tempCoin.imageURL = temp.getString("image");
                                tempCoin.market_cap_rank = temp.getInt("market_cap_rank");
                                list.add(tempCoin);
                            }
                            Toast.makeText(getContext(),"lkidfshg;lkasidfhgd",Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"error getting the coin lists",Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(stringRequest);
        addCoinAdapter.notifyDataSetChanged();
    }
    
    public void RV(View v){
        RecyclerView recyclerView = v.findViewById(R.id.AddRV);
        recyclerView.setAdapter(addCoinAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addCoinAdapter.notifyDataSetChanged();
    }

}