package com.ilibellus.extensions;

import com.pushbullet.android.extension.MessagingExtension;

import de.greenrobot.event.EventBus;
import com.ilibellus.async.bus.PushbulletReplyEvent;
import com.ilibellus.helpers.LogDelegate;


public class PushBulletExtension extends MessagingExtension {

    private static final String TAG = "PushBulletExtension";

    @Override
    protected void onMessageReceived(final String conversationIden, final String message) {
        LogDelegate.i("Pushbullet MessagingExtension: onMessageReceived(" + conversationIden + ", " + message
                + ")");
        EventBus.getDefault().post(new PushbulletReplyEvent(message));
//        MainActivity runningMainActivity = MainActivity.getInstance();
//        if (runningMainActivity != null && !runningMainActivity.isFinishing()) {
//            runningMainActivity.onPushBulletReply(message);
//        }
    }


    @Override
    protected void onConversationDismissed(final String conversationIden) {
        LogDelegate.i("Pushbullet MessagingExtension: onConversationDismissed(" + conversationIden + ")");
    }
}
