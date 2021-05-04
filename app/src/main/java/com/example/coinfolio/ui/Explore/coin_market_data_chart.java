package com.example.coinfolio.ui.Explore;

import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.coinfolio.SparkAdapter;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coinfolio.R;
import com.example.coinfolio.VolleySingleton;
import com.robinhood.spark.SparkView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class coin_market_data_chart extends AppCompatActivity {

    private TextView marketCapTV,tradingVolumeTV, priceScrub, ATL_TV, ATLpercentageTV, ATH_TV, ATHpercentageTV, AssetNameSymbol;
    private SparkView sparkView;
    private float[] coinSparkData;
    private RadioGroup chartTimeframes;
    private RadioButton timeframeSelected;
    private ProgressBar loadingSparkLine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_market_data_chart);
        final String asset = getIntent().getStringExtra("coin_id");
        marketCapTV = findViewById(R.id.textView4);
        tradingVolumeTV = findViewById(R.id.volume_TV);
        ATL_TV = findViewById(R.id.atl_tv);
        ATLpercentageTV = findViewById(R.id.atl_percentage_tv);
        ATH_TV = findViewById(R.id.ath_tv);
        ATHpercentageTV = findViewById(R.id.ath_percentage_tv);
        AssetNameSymbol = findViewById(R.id.coin_name_tv);
        priceScrub = findViewById(R.id.explore_scrub);
        chartTimeframes = findViewById(R.id.explore_radio_group);
        loadingSparkLine = findViewById(R.id.explore_progress);
        loadingSparkLine.setVisibility(View.VISIBLE);

        getSparklineData(asset,1);


        chartTimeframes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = group.getCheckedRadioButtonId();
                timeframeSelected = findViewById(selectedId);
                String temp = timeframeSelected.getText().toString();

                switch (temp)
                {
                    case "Day":
                        loadingSparkLine.setVisibility(View.VISIBLE);
                        getSparklineData(asset, 1);
                        break;
                    case "Week":
                        loadingSparkLine.setVisibility(View.VISIBLE);
                        getSparklineData(asset, 7);
                        break;
                    case "Month":
                        loadingSparkLine.setVisibility(View.VISIBLE);
                        getSparklineData(asset, 30);
                        break;

                }
            }
        });

    }

    public void setSparkView()
    {
        sparkView = findViewById(R.id.charts_spark);
        sparkView.setAdapter(new SparkAdapter(coinSparkData));
        final int current_price = coinSparkData.length-1;
        sparkView.setScrubEnabled(true);
        sparkView.setScrubListener(new SparkView.OnScrubListener() {
            @Override
            public void onScrubbed(Object value) {
                if(value != null)
                {
                    double price_scrub = Double.parseDouble(value.toString());
                    if(price_scrub > 2.01)
                    {
                        priceScrub.setText(String.format("$%.2f", price_scrub));
                    }
                    else
                    {
                        priceScrub.setText(String.format("$%.5f", price_scrub));
                    }
                }
                else
                {
                    priceScrub.setText(String.format("$%.2f", coinSparkData[current_price]));
                }


            }
        });
        priceScrub.setText(String.format("$%.2f", coinSparkData[current_price]));
    }

    public void getSparklineData(final String coinID, int timeframe)
    {
        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        String url ="https://api.coingecko.com/api/v3/coins/" + coinID + "/market_chart?vs_currency=usd&days="+ timeframe;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("prices");
                            float []data;

                            data = new float[array.length()];
                            for (int j = 0; j < array.length(); j++) {
                                JSONArray arraytemp = array.getJSONArray(j);
                                data[j] = (float) arraytemp.getDouble(1);
                            }
                            coinSparkData = data;
                            loadingSparkLine.setVisibility(View.INVISIBLE);
                            setSparkView();
                            getMarketData(coinID);

                        }catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"volley error", Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getMarketData(String coinID)
    {
        RequestQueue queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        String url ="https://api.coingecko.com/api/v3/coins/" + coinID + "?tickers=false&community_data=false&developer_data=false&sparkline=false";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject market_data = jsonObject.getJSONObject("market_data");
                            Double market_cap = market_data.getJSONObject("market_cap").getDouble("usd");
                            Double trading_volume = market_data.getJSONObject("total_volume").getDouble("usd");
                            Double atl = market_data.getJSONObject("atl").getDouble("usd");
                            Double atl_percent = market_data.getJSONObject("atl_change_percentage").getDouble("usd");
                            Double ath = market_data.getJSONObject("ath").getDouble("usd");
                            Double ath_percent = market_data.getJSONObject("ath_change_percentage").getDouble("usd");
                            String coinName = jsonObject.getString("name");
                            String coinSymbol = jsonObject.getString("symbol");
                            marketCapTV.setText(String.format("$%.2f", market_cap));
                            tradingVolumeTV.setText(String.format("%.2f", trading_volume));
                            ATL_TV.setText(String.format("$%.5f", atl));
                            ATLpercentageTV.setTextColor(Color.GREEN);
                            ATLpercentageTV.setText(String.format("%.2f", atl_percent) + "%");

                            ATH_TV.setText(String.format("$%.5f", ath));
                            ATHpercentageTV.setTextColor(Color.RED);
                            ATHpercentageTV.setText(String.format("%.2f", ath_percent) + "%");

                            AssetNameSymbol.setText(String.format("%s (%s)", coinName, coinSymbol.toUpperCase()));

                        }catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"volley error", Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
