package rahul.lucky.chatapp.activity;

import android.arch.paging.PagedList;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import rahul.lucky.chatapp.R;
import rahul.lucky.chatapp.adapter.ChatRoomAdapter;
import rahul.lucky.chatapp.models.ChatRoom;
import rahul.lucky.chatapp.models.User;

public class ChatRoomActivity extends AppCompatActivity implements ChatRoomAdapter.RoomListener {

    private User currentUser;

    private RecyclerView recyclerView;
    private ChatRoomAdapter chatRoomAdapter;

    private CollectionReference chatRoomCollectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        currentUser = getIntent().getParcelableExtra("user");
        String userId = getIntent().getStringExtra("userId");
        currentUser.setUserId(FirebaseFirestore.getInstance().document(userId));

        chatRoomCollectionRef = FirebaseFirestore.getInstance().collection("chatRooms");


        recyclerView = findViewById(R.id.recyclerView);

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewChatClick();
            }
        });

        setUpAdapter();
    }


    private void setUpAdapter() {
        Query baseQuery = chatRoomCollectionRef.orderBy("createdAt", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(1)
                .setPageSize(2)
                .build();

        FirestorePagingOptions<ChatRoom> options = new FirestorePagingOptions.Builder<ChatRoom>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, ChatRoom.class)
                .build();

        chatRoomAdapter = new ChatRoomAdapter(this, currentUser, options);

        chatRoomAdapter.setRoomListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatRoomAdapter);
    }

    private void onNewChatClick() {
        Intent intent = new Intent(this,AllRoomActivity.class);
        intent.putExtra("user",currentUser);
        intent.putExtra("userId",currentUser.getUserId().getPath());
        startActivity(intent);
    }

    @Override
    public void onRoomClick(ChatRoom chatRoom) {
        //Same as AllRoom Activity clicking
        DocumentReference userId = this.currentUser.getUserId();
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("user",this.currentUser);
        intent.putExtra("userId", userId.getPath());

        String anotherUserId;

        if(chatRoom.getSenderId().getPath().equals(userId.getPath())){
            anotherUserId = chatRoom.getReceiverId().getPath();
        }else {
            anotherUserId = chatRoom.getSenderId().getPath();
        }

        //intent.putExtra("anotherUser",user);
        intent.putExtra("anotherUserId",anotherUserId);
        startActivity(intent);

    }
}
