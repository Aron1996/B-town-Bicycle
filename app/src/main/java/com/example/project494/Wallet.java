package com.example.project494;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Wallet extends AppCompatActivity {

    TextView bal;
    EditText money;
    Button pay;
    int moneyleft, newnwemoney;
    static String userID, newmoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        bal = (TextView) findViewById(R.id.balance);
        money = (EditText) findViewById(R.id.moneyAdd);
        pay = (Button) findViewById(R.id.pay);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        userID = sharedPref.getString("id", "");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArray = new JSONArray(response);
                    if (jArray.length() == 0) {
                        Toast.makeText(Wallet.this, "Doesn't have any money yet", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < jArray.length(); i++) {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i);
                                moneyleft = oneObject.getInt("balance");
                                bal.setText(Integer.toString(moneyleft));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        paymentRequest paymentrequest = new paymentRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Wallet.this);
        queue.add(paymentrequest);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newmoney = money.getText().toString();
                newnwemoney = Integer.parseInt(newmoney);
                moneyleft += newnwemoney;
                processPayment();
                money.setText("");

            }
        });

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        startService(intent);
    }

    @Override
    protected void onDestroy(){
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }
    public static final int PAYPAL_REQUEST_CODE =7171;

    private static PayPalConfiguration configuration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(config.PAYPAL_CLIENT_ID);

    public void processPayment(){
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(newnwemoney) , "USD", "Add money to use", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null)
                {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        if(paymentDetails.contains("approved")){
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        if (success && !(newmoney.equals(""))) {
                                            bal.setText(String.format("%d", moneyleft));
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Wallet.this);
                                            if (newmoney.equals("")) {
                                                builder.setMessage("Please enter money amount")
                                                        .setNegativeButton("Retry", null)
                                                        .create()
                                                        .show();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            paymentADD paymentaDD = new paymentADD(userID, newmoney,"1", responseListener);
                            RequestQueue queue = Volley.newRequestQueue(Wallet.this);
                            queue.add(paymentaDD);
                            Toast.makeText(Wallet.this, "money submitted", Toast.LENGTH_LONG).show();

                        }

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }else{
                Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
            }
        }else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }
    }
}
class paymentRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://cgi.soic.indiana.edu/~yaokzhan/team32/payment.php";
    private Map<String, String> params;

    public paymentRequest(String id, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("userID", id);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

class paymentADD extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://cgi.soic.indiana.edu/~yaokzhan/team32/addPayment.php";
    private Map<String, String> params;

    public paymentADD(String id, String moneyAdd, String state, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("userID", id);
        params.put("money", moneyAdd);
        params.put("state", state);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}