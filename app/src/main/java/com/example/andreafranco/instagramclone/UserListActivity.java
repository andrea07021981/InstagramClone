package com.example.andreafranco.instagramclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        final ListView usersListView = findViewById(R.id.users_listview);
        final ArrayList<String> users = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);

        ParseQuery<ParseUser> queryUsers = ParseUser.getQuery();
        queryUsers.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        queryUsers.addAscendingOrder("username");

        queryUsers.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseUser parseUser : objects) {
                        users.add(parseUser.getUsername());
                    }
                    usersListView.setAdapter(adapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Error getting users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
