package com.example.test1706;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Checkout_PaymentDetails_activity extends AppCompatActivity {

    TextView txtId, txtAmount, txtStatus, tvtsCreationTime;
    Button btn_continue_shopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        txtId = (TextView) findViewById(R.id.txtId);
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        txtStatus = (TextView) findViewById(R.id.txtsStatus);
        tvtsCreationTime = (TextView) findViewById(R.id.tvtsCreationTime);
        btn_continue_shopping = (Button) findViewById(R.id.btn_continue_shopping);
        Intent intent = getIntent();
        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("Checkout_PaymentDetails_activity"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        btn_continue_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Checkout_PaymentDetails_activity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            txtId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            tvtsCreationTime.setText(response.getString("create_time"));
            txtAmount.setText(String.valueOf("$" + paymentAmount));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
