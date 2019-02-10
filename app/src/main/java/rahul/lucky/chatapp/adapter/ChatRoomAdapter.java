package rahul.lucky.chatapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import rahul.lucky.chatapp.R;
import rahul.lucky.chatapp.models.ChatRoom;
import rahul.lucky.chatapp.models.User;

/**
 * Created by Rahulucky03 on 06-02-2019.
 */
public class ChatRoomAdapter extends FirestorePagingAdapter<ChatRoom, RecyclerView.ViewHolder> {

    private final User currentUser;
    private final String currentUserId;
    private final CollectionReference usersReference;
    private Context context;
    private RoomListener roomListener;
    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public ChatRoomAdapter(Context context, User currentUser, @NonNull FirestorePagingOptions<ChatRoom> options) {
        super(options);
        usersReference = FirebaseFirestore.getInstance().collection("users");
        this.currentUser = currentUser;
        this.context = context;
        currentUserId = currentUser.getUserId().getId();
    }

    public void setRoomListener(RoomListener roomListener) {
        this.roomListener = roomListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_items_room, viewGroup, false);
        return new RoomViewHolder(context,view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChatRoom model) {
        RoomViewHolder roomViewHolder = (RoomViewHolder) holder;
        roomViewHolder.onBindedSuccess(model, position, roomListener);
    }

    private class RoomViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageProfile;
        private TextView tvUserName;
        private TextView tvLastMsg;
        private TextView tvTime;

        private Context context;
        private View mView;

        public RoomViewHolder(Context context, View view) {
            super(view);
            this.mView = view;
            this.context = context;

            imageProfile = view.findViewById(R.id.imageProfile);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvLastMsg = view.findViewById(R.id.tvLastMsg);
            tvTime = view.findViewById(R.id.tvTime);

        }

        public void onBindedSuccess(final ChatRoom chatRoom, int position, final RoomListener roomListener) {
            String anotherUserId = getAnotherUserIdFromChatRoom(chatRoom);

            usersReference.document(anotherUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        if(user!=null){
                            Picasso.get().load(user.getProfileUrl()).centerCrop().fit().into(imageProfile);
                            tvUserName.setText(user.getName());
                        }
                    }
                }
            });

            tvLastMsg.setText(chatRoom.getLastMessage());
            tvTime.setText(chatRoom.getUpdatedAt());

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roomListener.onRoomClick(chatRoom);
                }
            });
        }
    }

    private String getAnotherUserIdFromChatRoom(ChatRoom chatRoom) {
        String chatRoomId = chatRoom.getChatRoomId().getId();
        String[] s = chatRoomId.split("_");
        if(s.length > 0){
            if(s[0].equals(currentUserId)){
                return s[1];
            }else {
                return s[0];
            }
        }
        return "";
    }

    public interface RoomListener{
        void onRoomClick(ChatRoom chatRoom);
    }
}
