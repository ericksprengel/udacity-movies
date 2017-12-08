package br.com.ericksprengel.android.movies;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MovieDetailsActivity extends BaseActivity implements View.OnClickListener {

    final private static String LOG_TAG = "MovieListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        initBaseActivity();
        findViewById(R.id.movie_list_ac_test_button).setOnClickListener(this);
        super.setOnErrorClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_error_button:
                Toast.makeText(this, "Error button.", Toast.LENGTH_LONG).show();
                showContent();
                break;
            case R.id.movie_list_ac_test_button:
                showLoading("carregando");
                Handler h = new Handler();
                h.postDelayed((Runnable) () -> showError("Deu ruim."), 3000);
            default:
                Log.wtf(LOG_TAG, "Click event without treatment. (view id: " +view.getId()+ ")");
        }
    }
}
