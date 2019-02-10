package rahul.lucky.chatapp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rahul.lucky.chatapp.ModelListener;

/**
 * Created by Rahulucky03 on 01-02-2019.
 */
public class User implements Parcelable {

    @Expose
    @SerializedName("userId")
    private DocumentReference userId;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("profileUrl")
    private String profileUrl;

    @Expose
    @SerializedName("email")
    private String email;

    @Expose
    @SerializedName("fcmToken")
    private String fcmToken;

    @Expose
    @SerializedName("createdAt")
    private String createdAt;

    @Expose
    @SerializedName("updatedAt")
    private String updatedAt;


    ModelListener<User> modelListener;
    private int requestCode;

    public User() {
    }

    public void setModelListener(ModelListener<User> modelListener, int requestCode) {
        this.modelListener = modelListener;
        this.requestCode = requestCode;
    }

    public User(DocumentReference userId, String name, Uri profileUrl, String email, String fcmToken, String createdAt) {
        this.userId = userId;
        this.name = name;
        if(profileUrl!=null){
            this.profileUrl = profileUrl.toString();
        }
        this.email = email;
        this.fcmToken = fcmToken;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    protected User(Parcel in) {
        name = in.readString();
        profileUrl = in.readString();
        email = in.readString();
        fcmToken = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("userId")
    public DocumentReference getUserId() {
        if(modelListener!=null) {
            userId.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot result = task.getResult();
                        if (result != null && result.exists()) {
                            User user = result.toObject(User.class);
                            modelListener.onReturnSuccess(user, requestCode);
                        }
                    } else {
                        modelListener.onReturnFailed(requestCode);
                    }
                }
            });
        }
        return userId;
    }

    @PropertyName("userId")
    public void setUserId(DocumentReference userId) {
        this.userId = userId;
    }

    @PropertyName("profileUrl")
    public String getProfileUrl() {
        return profileUrl;
    }

    @PropertyName("profileUrl")
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("fcmToken")
    public String getFcmToken() {
        return fcmToken;
    }

    @PropertyName("fcmToken")
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @PropertyName("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    @PropertyName("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @PropertyName("updatedAt")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @PropertyName("updatedAt")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(profileUrl);
        parcel.writeString(email);
        parcel.writeString(fcmToken);
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
    }
}
