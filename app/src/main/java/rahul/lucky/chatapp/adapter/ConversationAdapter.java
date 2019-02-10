package rahul.lucky.chatapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import rahul.lucky.chatapp.R;
import rahul.lucky.chatapp.adapter.viewholders.LoadingVH;
import rahul.lucky.chatapp.adapter.viewholders.ReceiverViewHolder;
import rahul.lucky.chatapp.adapter.viewholders.SenderViewHolder;
import rahul.lucky.chatapp.models.Message;
import rahul.lucky.chatapp.models.User;

/**
 * Created by Rahulucky03 on 09-02-2019.
 */
public class ConversationAdapter extends FirestorePagingAdapter<Message, RecyclerView.ViewHolder> {

    private static final int LOADING = 0;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private final User currentUser;
    private Context context;

    private CollectionReference usersCollectionRef;

    public ConversationAdapter(Context context, FirestorePagingOptions<Message> options, User currentUser) {
        super(options);
        this.context = context;
        this.currentUser = currentUser;
        usersCollectionRef = FirebaseFirestore.getInstance().collection("users");
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull final Message message) {

        int itemViewType = getItemViewType(position);

        if(itemViewType == VIEW_TYPE_MESSAGE_RECEIVED || itemViewType == VIEW_TYPE_MESSAGE_SENT){

            String name = "",profileImage = "";
            String createdAt = message.getCreatedAt();

//            String date = CommonUtils.convertTimeToCurrentTimeZone(createdAt);
//            String time = CommonUtils.convertTime(createdAt.substring(10).trim());

            if(itemViewType == VIEW_TYPE_MESSAGE_RECEIVED){
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;

                /*message.getReceiverId().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            User receiverUser = documentSnapshot.toObject(User.class);
                            if(receiverUser!=null) {
                                String name = receiverUser.getName();
                                String profileImage = receiverUser.getProfileUrl();
                                //receiverViewHolder.chat_company_reply_author.setText(name)

                            }
                        }
                    }
                });*/

                receiverViewHolder.message_text.setText(message.getMessage());
                receiverViewHolder.time_text.setText(message.getCreatedAt());

            }
            if(itemViewType == VIEW_TYPE_MESSAGE_SENT){
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                /*name = currentUser.getName();
                profileImage = currentUser.getProfileUrl();*/

                senderViewHolder.message_text.setText(message.getMessage());
                senderViewHolder.time_text.setText(message.getCreatedAt());
                //senderViewHolder.user_reply_status.setText(name);

            }

            /*Glide.with(context).load((profileImage))
                    .apply(RequestOptions.placeholderOf(R.drawable.male_icon).fitCenter())
                    .into(sentViewHolder.profileImage);*/

//            sentViewHolder.tvMsg.setText(message.getMessage());
//            sentViewHolder.tvTime.setText(createdAt);

        }else {

        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_MESSAGE_SENT:
                View v1 = inflater.inflate(R.layout.chat_user2_item, parent, false);
                return new SenderViewHolder(v1);
            case VIEW_TYPE_MESSAGE_RECEIVED:
                View v2 = inflater.inflate(R.layout.chat_user1_item, parent, false);
                return new ReceiverViewHolder(v2);
            case LOADING:
                View v3 = inflater.inflate(R.layout.progress_item, parent, false);
                viewHolder = new LoadingVH(v3);
                return viewHolder;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        DocumentSnapshot item = getItem(position);
        if(item!=null && item.exists()){
            Message message = item.toObject(Message.class);
            if(message!=null){
                DocumentReference senderId = message.getSenderId();
                if(currentUser.getUserId().equals(senderId)){
                    return VIEW_TYPE_MESSAGE_SENT;
                }else {
                    return VIEW_TYPE_MESSAGE_RECEIVED;
                }
            }
        }
        return LOADING;
    }

}
