package com.example.yashpatel.cards;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;

public class creditCard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_credit_card);
        CardForm cardForm = (CardForm) findViewById(R.id.cardform);
        TextView txtDes = findViewById(R.id.payment_amount);
        Button btnPay = findViewById(R.id.btn_pay);
        txtDes.setText("$1");
        btnPay.setText(String.format("payter is %s", txtDes.getText()));
        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
                Toast.makeText(creditCard.this,"Name : "+card.getName()+ " | Last 4 Digits ",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
