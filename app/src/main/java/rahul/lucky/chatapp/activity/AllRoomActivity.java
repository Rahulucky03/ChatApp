package rahul.lucky.chatapp.activity;

import android.arch.paging.PagedList;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import rahul.lucky.chatapp.R;
import rahul.lucky.chatapp.adapter.RoomAdapter;
import rahul.lucky.chatapp.models.User;

public class AllRoomActivity extends AppCompatActivity implements RoomAdapter.RoomListener {

    private CollectionReference collectionReference;

    private RecyclerView roomList;
    private RoomAdapter roomAdapter;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_room);
        currentUser = getIntent().getParcelableExtra("user");
        String userId = getIntent().getStringExtra("userId");
        currentUser.setUserId(FirebaseFirestore.getInstance().document(userId));

        collectionReference = FirebaseFirestore.getInstance().collection("users");
        roomList = findViewById(R.id.room_list);

        setUpAdapter();
    }


    private void setUpAdapter() {
        Query baseQuery = collectionReference.orderBy("createdAt", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(1)
                .setPageSize(2)
                .build();

        FirestorePagingOptions<User> options = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, User.class)
                .build();

        roomAdapter = new RoomAdapter(this, options);

        roomAdapter.setRoomListener(this);
        roomList.setLayoutManager(new LinearLayoutManager(this));
        roomList.setAdapter(roomAdapter);
    }

    @Override
    public void onRoomClick(User user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getUserId().getId().equals(currentUser.getUid())){
            Toast.makeText(this, "You can not message to self", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra("user",this.currentUser);
            intent.putExtra("userId",this.currentUser.getUserId().getPath());

            intent.putExtra("anotherUser",user);
            intent.putExtra("anotherUserId",user.getUserId().getPath());
            startActivity(intent);
        }
    }
}
