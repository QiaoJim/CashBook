package com.cq9191.cashbook.MyManageUi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.cq9191.cashbook.MyManageActivity;
import com.cq9191.cashbook.R;

public class AboutUs extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_layout);

        ImageButton close = (ImageButton) findViewById(R.id.bt_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUs.this, MyManageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
