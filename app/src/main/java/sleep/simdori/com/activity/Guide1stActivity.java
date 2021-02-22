package sleep.simdori.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import sleep.simdori.com.R;

public class Guide1stActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_1st);

        Button start_button = (Button) findViewById(R.id.startButton) ;
        start_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Guide2ndActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
