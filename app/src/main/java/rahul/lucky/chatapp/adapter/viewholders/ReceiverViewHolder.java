package rahul.lucky.chatapp.adapter.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import rahul.lucky.chatapp.R;

/**
 * Created by Rahulucky03 on 09-02-2019.
 */
public class ReceiverViewHolder extends RecyclerView.ViewHolder{

    /*@BindView(R.id.chat_company_reply_author)
    TextView chat_company_reply_author;*/
    public TextView message_text;
    public TextView time_text;

    public ReceiverViewHolder(@NonNull View itemView) {
        super(itemView);
        message_text = itemView.findViewById(R.id.message_text);
        time_text = itemView.findViewById(R.id.time_text);
    }
}
