package com.example.andreafranco.instagramclone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        final LinearLayout linLayout = findViewById(R.id.lin_layout);

        //Get the image of the user
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        if (username != null) {
            setTitle(username + "'s photos");
            ParseQuery<ParseObject> objectParseQueryImage = ParseQuery.getQuery("Image");//Equals to new ParseQuery<ParseObject>("Image");
            objectParseQueryImage.whereEqualTo("username", username);
            objectParseQueryImage.orderByDescending("createdAt");
            objectParseQueryImage.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects != null && objects.size() > 0) {
                        for (ParseObject parseObject : objects) {
                            final ParseFile imageParseFile = (ParseFile) parseObject.get("image");
                            imageParseFile.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    final ImageView imageView = new ImageView(getApplicationContext());
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    imageView.setImageBitmap(bitmap);
                                    linLayout.addView(imageView);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No images found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
