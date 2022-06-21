package ml.test7777.big6.appstore.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ml.test7777.big6.appstore.R;
import ml.test7777.big6.appstore.databinding.ActivityHelpBinding;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ml.test7777.big6.appstore.databinding.ActivityHelpBinding binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void onButtonClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.FAQButton) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://big6.test7777.ml/faq")));
        } else if (viewId == R.id.ContactButton) {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://big6.test7777.ml/contact")));
        }
    }

}