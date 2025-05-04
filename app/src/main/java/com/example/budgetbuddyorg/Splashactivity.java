package com.example.budgetbuddyorg;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splashactivity extends AppCompatActivity {

    private ImageView ImageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splashactivity);

        ImageView2=findViewById(R.id.imageView2);

        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        ImageView2.startAnimation(scaleAnim);


//        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
//        ImageView2.startAnimation(rotate);

//        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(ImageView2, "alpha", 0f, 1f);
//        fadeIn.setDuration(1000);
//
//        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ImageView2, "scaleX", 0.5f, 1f);
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ImageView2, "scaleY", 0.5f, 1f);
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(fadeIn, scaleX, scaleY);
//        animatorSet.start();


        Intent iHome;
        iHome=new Intent(Splashactivity.this, MainActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(iHome);
                finish();
            }
        },4000);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}