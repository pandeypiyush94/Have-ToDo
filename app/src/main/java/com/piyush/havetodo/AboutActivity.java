package com.piyush.havetodo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shashank.sony.fancyaboutpagelib.FancyAboutPage;

import static com.piyush.havetodo.AppConstants.DARK_THEME;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

                    /*--------------Set Activity's View--------------*/
        setContentView(R.layout.activity_about);

        setTitle(getString(R.string.about));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        FancyAboutPage fancyAboutPage=findViewById(R.id.fancyaboutpage);
        //fancyAboutPage.setCoverTintColor(Color.BLUE);  //Optional
        fancyAboutPage.setCover(R.drawable.cover); //Pass your cover image
        fancyAboutPage.setName("Piyush K Pandey");
        fancyAboutPage.setDescription("Professional Android App and Java Developer. | Developing Android Apps since 2 Years");
        fancyAboutPage.setAppIcon(R.mipmap.app_logo); //Pass your app icon image
        fancyAboutPage.setAppName(getString(R.string.app_name));
        fancyAboutPage.setVersionNameAsAppSubTitle("7.9");
        fancyAboutPage.setAppDescription("You have to set your To Do but want to remind you also. Don't worry.\n\n" +
                "Have To Do App will do it for you. You can set your ToDo and put a reminder on it so when time will come it will notify you.\n\n"+
                "Build on the beautiful Google Material Design Guidelines which makes this App more appealing. So, Let's give it a try I know you will surely say 'I Have To Do'.");
        fancyAboutPage.addEmailLink("pandeypiyush94@gmail.com");     //Add your email id
        fancyAboutPage.addFacebookLink("https://www.facebook.com/piyush.pandey.3726613");  //Add your facebook address url
        fancyAboutPage.addTwitterLink("https://twitter.com/PandeyPiyushK");
        fancyAboutPage.addLinkedinLink("https://www.linkedin.com/in/piyush-pandey-7755b9121/");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            startActivity(new Intent(this,MainActivity.class));
            finish();
        return true;
    }

}
