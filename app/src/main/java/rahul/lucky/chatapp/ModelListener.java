package rahul.lucky.chatapp;

/**
 * Created by Rahulucky03 on 04-02-2019.
 */
public interface ModelListener<T>  {
    T onReturnSuccess(T result, int requestCode);
    void onReturnFailed(int requestCode);
}
