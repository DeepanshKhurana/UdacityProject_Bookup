package in.thepolymath.bookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    EditText queryText;
    String q;
    ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queryText = (EditText) findViewById(R.id.query);
        searchIcon = (ImageView) findViewById(R.id.search_button);

        searchIcon.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        q = queryText.getText().toString();
                        Intent intent = new Intent(MainActivity.this, BookActivity.class);
                        intent.putExtra("query", q);
                        startActivity(intent);
                    }
                });
    }

}
