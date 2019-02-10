package rahul.lucky.chatapp.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahulucky03 on 04-02-2019.
 */
public class Message {

    @Expose
    @SerializedName("messageId")
    private DocumentReference messageId;

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("updatedAt")
    private String updatedAt;

    @Expose
    @SerializedName("createdAt")
    private String createdAt;

    @Expose
    @SerializedName("senderId")
    private DocumentReference senderId;

    @Expose
    @SerializedName("receiverId")
    private DocumentReference receiverId;


    @PropertyName("messageId")
    public DocumentReference getMessageId() {
        return messageId;
    }

    @PropertyName("messageId")
    public void setMessageId(DocumentReference messageId) {
        this.messageId = messageId;
    }

    @PropertyName("message")
    public String getMessage() {
        return message;
    }

    @PropertyName("message")
    public void setMessage(String message) {
        this.message = message;
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
}
