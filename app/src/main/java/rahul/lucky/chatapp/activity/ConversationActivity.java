package rahul.lucky.chatapp.activity;

import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import rahul.lucky.chatapp.R;
import rahul.lucky.chatapp.adapter.ConversationAdapter;
import rahul.lucky.chatapp.models.ChatRoom;
import rahul.lucky.chatapp.models.Message;
import rahul.lucky.chatapp.models.User;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView rvMsgList;
    private EditText etMsg;
    private ImageButton ivSend;

    private User currentUser;
    @Nullable
    private User anotherUser;
    private CollectionReference usersCollectionReference;
    private CollectionReference chatRoomCollectionRef;
    private CollectionReference messageRoomCollectionRef;
    private boolean isRoomExist;
    private String roomId;
    private ConversationAdapter conversationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        currentUser = getIntent().getParcelableExtra("user");
        String userId = getIntent().getStringExtra("userId");
        currentUser.setUserId(FirebaseFirestore.getInstance().document(userId));

        anotherUser = getIntent().getParcelableExtra("anotherUser");
        String anotherUserId = getIntent().getStringExtra("anotherUserId");

        usersCollectionReference = FirebaseFirestore.getInstance().collection("users");
        chatRoomCollectionRef = FirebaseFirestore.getInstance().collection("chatRooms");
        messageRoomCollectionRef = FirebaseFirestore.getInstance().collection("messages");

        if(anotherUser!=null){
            anotherUser.setUserId(FirebaseFirestore.getInstance().document(anotherUserId));
            afterAnotherUserGet();
        }else {
            getAnotherUser(anotherUserId);
        }

        rvMsgList = findViewById(R.id.rvMsgList);
        etMsg = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);

        setUpConversationAdapter();

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMsg.getText().toString();
                if(!message.isEmpty()){
                    if(isRoomExist){
                        storeMessageAtRoom(message);
                    }else {
                        createRoomAndStoreFirstMessage(message);
                    }
                }
            }
        });
    }

    private void setUpConversationAdapter() {
        Query baseQuery = messageRoomCollectionRef.orderBy("createdAt", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(1)
                .setPageSize(2)
                .build();

        FirestorePagingOptions<Message> options = new FirestorePagingOptions.Builder<Message>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, Message.class)
                .build();

        conversationAdapter = new ConversationAdapter(this, options, currentUser);

        //conversationAdapter.setRoomListener(this);
        rvMsgList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));
        rvMsgList.setAdapter(conversationAdapter);
    }

    private void getAnotherUser(String anotherUserId) {
        FirebaseFirestore.getInstance().document(anotherUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    if(user!=null){
                        anotherUser = user;
                        afterAnotherUserGet();
                    }
                }
            }
        });
    }

    private void afterAnotherUserGet() {
        roomId = getRoomIdByUser(currentUser,anotherUser);
        checkRoomIsExistOrNot();
    }

    private void storeMessageAtRoom(String message) {
        updateRoomWithLatestMessage(message);
        DocumentReference document = messageRoomCollectionRef.document();

        long currentTime = System.currentTimeMillis();

        Message messageObj = new Message();
        messageObj.setMessage(message);
        messageObj.setCreatedAt(currentTime + "");
        messageObj.setUpdatedAt(currentTime + "");
        messageObj.setMessage(message);
        messageObj.setSenderId(currentUser.getUserId());
        messageObj.setReceiverId(anotherUser.getUserId());
        messageObj.setMessageId(document);

        OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ConversationActivity.this, "Send", Toast.LENGTH_SHORT).show();
            }
        };
        document.set(messageObj).addOnCompleteListener(onCompleteListener);
    }

    private void updateRoomWithLatestMessage(String message) {
        DocumentReference document = chatRoomCollectionRef.document(roomId);

        long currentTime = System.currentTimeMillis();

        Map<String, Object> map = new HashMap<>();
        map.put("updatedAt",currentTime + "");
        map.put("lastMessage",message);
        map.put("senderId",currentUser.getUserId());
        map.put("receiverId",anotherUser.getUserId());

        document.set(map, SetOptions.merge());
    }

    private void createRoomAndStoreFirstMessage(final String message) {

        DocumentReference document = chatRoomCollectionRef.document(roomId);

        long currentTime = System.currentTimeMillis();

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setCreatedAt(currentTime + "");
        chatRoom.setChatRoomId(document);
        chatRoom.setUpdatedAt(currentTime + "");
        chatRoom.setLastMessage(message);
        chatRoom.setSenderId(currentUser.getUserId());
        chatRoom.setReceiverId(anotherUser.getUserId());

        OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                storeMessageAtRoom(message);
            }
        };

        document.set(chatRoom).addOnCompleteListener(onCompleteListener);
    }

    private void checkRoomIsExistOrNot() {
        if(!roomId.isEmpty()){
            OnCompleteListener<DocumentSnapshot> onCompleteListener = new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.e("ChatRoom", roomId + "Exits");
                        //ChatRoom chatRoom = document.toObject(ChatRoom.class);
                        isRoomExist = true;
                    } else {
                        Log.e("ChatRoom", roomId + "Not Exits");
                        isRoomExist = false;
                    }
                }
            };
            chatRoomCollectionRef.document(roomId).get().addOnCompleteListener(onCompleteListener);
        }
    }

    private String getRoomIdByUser(User currentUser, User anotherUser) {
        if(currentUser!=null && anotherUser!=null){
            String currentUserId = currentUser.getUserId().getId();
            String anotherUserId = anotherUser.getUserId().getId();
            if(anotherUserId.compareTo(currentUserId) > 0){
                return currentUserId+"_"+anotherUserId;
            }else {
                return anotherUserId+"_"+currentUserId;
            }
        }
        return "";
    }
}
