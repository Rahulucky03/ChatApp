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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import rahul.lucky.chatapp.R;
import rahul.lucky.chatapp.models.User;

/**
 * Created by Rahulucky03 on 02-02-2019.
 */
public class RoomAdapter extends FirestorePagingAdapter<User, RecyclerView.ViewHolder> {


    private Context context;
    private RoomListener roomListener;
    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public RoomAdapter(Context context, @NonNull FirestorePagingOptions<User> options) {
        super(options);
        this.context = context;
    }

    public void setRoomListener(RoomListener roomListener) {
        this.roomListener = roomListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_items_users, viewGroup, false);
        return new RoomViewHolder(context,view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull User model) {
        RoomViewHolder roomViewHolder = (RoomViewHolder) holder;
        roomViewHolder.onBindedSuccess(model, position, roomListener);
    }

    private class RoomViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageProfile;
        private TextView tvUserName;
        private TextView email;
        private TextView userId;
        private TextView fcmToken;
        private TextView createdAt;
        private TextView updatedAT;

        private Context context;
        private View mView;

        public RoomViewHolder(Context context, View view) {
            super(view);
            this.mView = view;
            this.context = context;

            imageProfile = view.findViewById(R.id.imageProfile);
            tvUserName = view.findViewById(R.id.tv_name);
            email = view.findViewById(R.id.email);

            userId = view.findViewById(R.id.userId);
            fcmToken = view.findViewById(R.id.fcmToken);

            createdAt = view.findViewById(R.id.createdAt);
            updatedAT = view.findViewById(R.id.updatedAT);
        }

        public void onBindedSuccess(final User user, int position, final RoomListener roomListener) {
            Picasso.get().load(user.getProfileUrl()).centerCrop().fit().into(imageProfile);
            tvUserName.setText(user.getName());
            email.setText(user.getEmail());
            userId.setText(user.getUserId().getId());
            fcmToken.setText(user.getFcmToken());

            String createdAt = user.getCreatedAt();
            String updatedAt = user.getUpdatedAt();

            this.createdAt.setText(createdAt);
            updatedAT.setText(updatedAt);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roomListener.onRoomClick(user);
                }
            });
        }
    }

    public interface RoomListener{
        void onRoomClick(User user);
    }
}
