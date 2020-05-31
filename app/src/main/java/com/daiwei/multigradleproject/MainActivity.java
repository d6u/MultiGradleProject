package com.daiwei.multigradleproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daiwei.multigradleproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  private static class ReplyHandler extends Handler {
    private ActivityMainBinding mActivityMainBinding;

    ReplyHandler(ActivityMainBinding activityMainBinding) {
      mActivityMainBinding = activityMainBinding;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
      Log.d(TAG, "handleMessage");

      if (msg.what == SomeService.GET_MESSAGE_REPLY) {
        Log.d(TAG, "string: " + msg.getData().getString(SomeService.MESSENGE_KEY));
        mActivityMainBinding.setMessage(msg.getData().getString(SomeService.MESSENGE_KEY));
      } else {
        super.handleMessage(msg);
      }
    }
  }

  @Nullable Messenger mServiceMessengerClient;
  @Nullable Messenger mActivityMessengerClient;
  @Nullable ActivityMainBinding mActivityMainBinding;

  private ServiceConnection mConnection =
      new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
          mServiceMessengerClient = new Messenger(binder);
          Message message = Message.obtain(null, SomeService.GET_MESSAGE);
          message.replyTo = mActivityMessengerClient;

          try {
            Log.d(TAG, "Send message");
            mServiceMessengerClient.send(message);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
          mServiceMessengerClient = null;
        }
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mActivityMainBinding =
        ActivityMainBinding.inflate(
            getLayoutInflater(), (ViewGroup) findViewById(android.R.id.content), true);
    mActivityMessengerClient = new Messenger(new ReplyHandler(mActivityMainBinding));
  }

  @Override
  protected void onStart() {
    super.onStart();
    Intent intent = new Intent(this, SomeService.class);
    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mServiceMessengerClient != null) {
      unbindService(mConnection);
    }
  }
}
