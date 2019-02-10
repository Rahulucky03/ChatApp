package rahul.lucky.chatapp;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;

/**
 * Created by Rahulucky03 on 04-02-2019.
 */
public class Utills {

    public static String getID(@NonNull DocumentReference documentReference){
        return documentReference.getId();
    }

}
