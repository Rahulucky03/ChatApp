package rahul.lucky.chatapp.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahulucky03 on 04-02-2019.
 */
public class ChatRoom {

    @Expose
    @SerializedName("chatRoomId")
    private DocumentReference chatRoomId;

    @Expose
    @SerializedName("lastMessage")
    private String lastMessage;

    @Expose
    @SerializedName("senderId")
    private DocumentReference senderId;

    @Expose
    @SerializedName("receiverId")
    private DocumentReference receiverId;

    @Expose
    @SerializedName("updatedAt")
    private String updatedAt;

    @Expose
    @SerializedName("createdAt")
    private String createdAt;


    @PropertyName("chatRoomId")
    public DocumentReference getChatRoomId() {
        return chatRoomId;
    }

    @PropertyName("chatRoomId")
    public void setChatRoomId(DocumentReference chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    @PropertyName("lastMessage")
    public String getLastMessage() {
        return lastMessage;
    }

    @PropertyName("lastMessage")
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @PropertyName("senderId")
    public DocumentReference getSenderId() {
        return senderId;
    }

    @PropertyName("senderId")
    public void setSenderId(DocumentReference senderId) {
        this.senderId = senderId;
    }

    @PropertyName("receiverId")
    public DocumentReference getReceiverId() {
        return receiverId;
    }

    @PropertyName("receiverId")
    public void setReceiverId(DocumentReference receiverId) {
        this.receiverId = receiverId;
    }

    @PropertyName("updatedAt")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @PropertyName("updatedAt")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PropertyName("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    @PropertyName("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
