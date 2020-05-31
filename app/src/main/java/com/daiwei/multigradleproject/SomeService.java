package com.daiwei.multigradleproject;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

/** This service is going to reply with some message when talked with */
public class SomeService extends Service {

  private static final String TAG = SomeService.class.getSimpleName();

  static final int GET_MESSAGE = 1;
  static final int GET_MESSAGE_REPLY = 2;
  static final String MESSENGE_KEY = "key";

  static class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      if (msg.what == GET_MESSAGE) {
        Log.d(TAG, "handleMessage");
        @Nullable Messenger messenger = msg.replyTo;

        if (messenger != null) {
          Log.d(TAG, "reply to not null");
          Message message = Message.obtain(null, GET_MESSAGE_REPLY);

          Bundle bundle = new Bundle();
          bundle.putString(MESSENGE_KEY, "Daiwei");
          message.setData(bundle);

          try {
            messenger.send(message);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
        }
      } else {
        super.handleMessage(msg);
      }
    }
  }

  @Nullable private Messenger mMessenger;

  @Override
  public IBinder onBind(Intent intent) {
    mMessenger = new Messenger(new IncomingHandler());
    return mMessenger.getBinder();
  }

  @Override
  public boolean onUnbind(Intent intent) {
    mMessenger = null;
    return super.onUnbind(intent);
  }
}
