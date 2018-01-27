package edu.ewu.team1.foodrescue;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class EaterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eater);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.switcher, menu);
        menu.findItem(R.id.itemEater).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itemFeeder) {
            startActivity(new Intent(this, FeederActivity.class));
            return true;
        }else if (id == R.id.itemEater){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
