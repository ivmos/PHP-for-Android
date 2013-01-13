/*
 * Copyright (C) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.googlecode.android_scripting.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.googlecode.android_scripting.AsyncTaskListener;
import com.googlecode.android_scripting.InterpreterInstaller;
import com.googlecode.android_scripting.InterpreterUninstaller;
import com.googlecode.android_scripting.Log;
import com.googlecode.android_scripting.exception.Sl4aException;
import com.googlecode.android_scripting.interpreter.InterpreterConstants;
import com.googlecode.android_scripting.interpreter.InterpreterDescriptor;
import com.irontec.phpforandroid.PhpDescriptor;
import com.irontec.phpforandroid.R;

/**
 * Base activity for distributing interpreters as APK's.
 * 
 * @author Damon Kohler (damonkohler@gmail.com)
 * @author Alexey Reznichenko (alexey.reznichenko@gmail.com)
 */
public abstract class Main extends Activity {
  private class SourceListDialogTask extends AsyncTask<Void, Void, String> {
    
    @Override
    protected void onPreExecute() {
      setProgressBarIndeterminateVisibility(true);
      
    }

    @Override
    protected String doInBackground(Void... params) {
      // TODO Auto-generated method stub
      StringBuilder sb = null;
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(binSourcesServer + "/arm/builds.php");
      try {
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        InputStream is = entity.getContent();

        //convert response to string
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
        sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line + "\n");
        }
        is.close();
      } catch (IllegalStateException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      return sb.toString();
    }
    
    protected void onPostExecute(String result) {
      setProgressBarIndeterminateVisibility(false);
      ArrayList<String> sourceItemList = new ArrayList<String>();

      JSONArray array = null;
       
        try {
          JSONObject jsonData = new JSONObject(result);
          JSONArray nameArray = jsonData.names();
          JSONArray valArray = jsonData.toJSONArray(nameArray);

          array = jsonData.getJSONArray((String)nameArray.get(0));

          for(int i = 0; i< array.length(); i++) {
            JSONObject row = array.getJSONObject(i);
            JSONArray array2 = row.getJSONArray("extensions");
            sourceItemList.add(array2.toString());
          }
        } catch (JSONException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }


      final Activity activity = Main.this;
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);

      String[] sourceItemArray = new String[sourceItemList.size()];
      for(int i=0; i<sourceItemList.size(); i++) {
        sourceItemArray[i] = sourceItemList.get(i)
                                           .replace("[", "")
                                           .replace("]", "")
                                           .replace("\"", "")
                                           .replace(",", ", ");
      }
      
      final JSONArray data = array;

      builder
      .setTitle("Pick the set of extensions you need.")
      .setSingleChoiceItems(sourceItemArray, 0, 
          new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
          Log.d(String.valueOf(which));
          // TODO Auto-generated method stub

        }
      }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
          // User clicked OK, so save the mSelectedItems results somewhere
          // or return them to the component that opened the dialog
          dialog.dismiss();
          int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
          try {
            JSONObject row = data.getJSONObject(selectedPosition);
            Log.d(row.get("download").toString());
            if (mDescriptor instanceof PhpDescriptor) {
              ((PhpDescriptor) mDescriptor).overrideInterpreterArchiveUrl(binSourcesServer + row.get("download").toString());
            }
            //install();
          } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          Log.d(String.valueOf(selectedPosition));
        }
      })
      .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
          dialog.dismiss();
        }
      });


      AlertDialog dialog = builder.create();
      dialog.show();

      
    }
    
  }
  protected final static float MARGIN_DIP = 3.0f;
  protected final static float SPINNER_DIP = 10.0f;

  protected final String mId = getClass().getPackage().getName();

  protected SharedPreferences mPreferences;
  protected InterpreterDescriptor mDescriptor;
  protected Button mButton;
  protected Button mAboutButton;
  protected final static String version = "0.4 (sl4a_r6)";
  public final static String binSourcesServer = "http://pthreads.org";
  private LinearLayout mProgressLayout;

  protected abstract InterpreterDescriptor getDescriptor();

  protected abstract InterpreterInstaller getInterpreterInstaller(InterpreterDescriptor descriptor,
      Context context, AsyncTaskListener<Boolean> listener) throws Sl4aException;

  protected abstract InterpreterUninstaller getInterpreterUninstaller(
      InterpreterDescriptor descriptor, Context context, AsyncTaskListener<Boolean> listener)
          throws Sl4aException;

  protected enum RunningTask {
    INSTALL, UNINSTALL
  }

  protected volatile RunningTask mCurrentTask = null;

  protected final AsyncTaskListener<Boolean> mTaskListener = new AsyncTaskListener<Boolean>() {
    @Override
    public void onTaskFinished(Boolean result, String message) {

      mProgressLayout.setVisibility(View.INVISIBLE);

      if (result) {
        switch (mCurrentTask) {
        case INSTALL:
          setInstalled(true);
          prepareUninstallButton();
          break;
        case UNINSTALL:
          setInstalled(false);
          prepareInstallButton();
          break;
        }

      }
      Log.v(Main.this, message);
      mCurrentTask = null;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    mDescriptor = getDescriptor();
    initializeViews();
    if (checkInstalled()) {
      prepareUninstallButton();
    } else {
      prepareInstallButton();
    }
    prepareAboutButton();
  }

  @Override
  protected void onStop() {
    super.onStop();
    finish();
  }

  // TODO(alexey): Pull out to a layout XML?
  protected void initializeViews() {
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    layout.setGravity(Gravity.CENTER_HORIZONTAL);
    TextView textview = new TextView(this);
    textview.setText(" PhpForAndroid " + version);
    ImageView imageView = new ImageView(this);
    imageView.setImageDrawable(getResources().getDrawable(R.drawable.pfa));
    layout.addView(imageView);
    mButton = new Button(this);
    mAboutButton = new Button(this);
    MarginLayoutParams marginParams =
        new MarginLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    final float scale = getResources().getDisplayMetrics().density;
    int marginPixels = (int) (MARGIN_DIP * scale + 0.5f);
    marginParams.setMargins(marginPixels, marginPixels, marginPixels, marginPixels);
    mButton.setLayoutParams(marginParams);
    mAboutButton.setLayoutParams(marginParams);
    layout.addView(textview);
    layout.addView(mButton);
    layout.addView(mAboutButton);

    mProgressLayout = new LinearLayout(this);
    mProgressLayout.setOrientation(LinearLayout.HORIZONTAL);
    mProgressLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
        LayoutParams.FILL_PARENT));
    mProgressLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

    LinearLayout bottom = new LinearLayout(this);
    bottom.setOrientation(LinearLayout.HORIZONTAL);
    bottom.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    bottom.setGravity(Gravity.CENTER_VERTICAL);
    mProgressLayout.addView(bottom);

    TextView message = new TextView(this);
    message.setText("   In Progress...");
    message.setTextSize(20);
    message.setTypeface(Typeface.DEFAULT_BOLD);
    message.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    ProgressBar bar = new ProgressBar(this);
    bar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    bottom.addView(bar);
    bottom.addView(message);
    mProgressLayout.setVisibility(View.INVISIBLE);

    layout.addView(mProgressLayout);
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    setProgressBarIndeterminateVisibility(false);

    setContentView(layout);
  }

  protected void prepareInstallButton() {
    mButton.setText("Install");

    mButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new SourceListDialogTask().execute();
        
      }
    });
  }

  protected void prepareAboutButton() {
    mAboutButton.setText("About");
    mAboutButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        about();

      }
    });
  }

  protected void prepareUninstallButton() {
    mButton.setText("Uninstall");
    mButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        uninstall();
      }
    });
  }

  protected void broadcastInstallationStateChange(boolean isInterpreterInstalled) {
    Intent intent = new Intent();
    intent.setData(Uri.parse("package:" + mId));
    if (isInterpreterInstalled) {
      intent.setAction(InterpreterConstants.ACTION_INTERPRETER_ADDED);
    } else {
      intent.setAction(InterpreterConstants.ACTION_INTERPRETER_REMOVED);
    }
    sendBroadcast(intent);
  }

  protected synchronized void about() {
    Intent browserIntent =
        new Intent("android.intent.action.VIEW", Uri.parse("http://www.phpforandroid.net/about"));
    startActivity(browserIntent);
  }

  // protected synchronized void about() {
  //
  // Context mContext = getApplicationContext();
  // dialog = new Dialog(Main.this);
  // dialog.setCancelable(true);
  // LinearLayout layout = new LinearLayout(this);
  // layout.setOrientation(LinearLayout.VERTICAL);
  // layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
  //
  // TextView textview = new TextView(this);
  // textview.setText(" PHP for Android " + Main.version +
  // "\n\n www.phpforandroid.net\n (c) Copyright Irontec 2010\n\nDevelopment:\n\n Ivan Mosquera Paulo (ivan@irontec.com)\n\n"
  // +
  // " Acknowledgements:\n\n Javier Infante (jabi@irontec.com)\n Gorka Rodrigo (gorka@irontec.com)\n Moshe Doron (momo@php.net)\n Damon Kohler (damonkohler@gmail.com)\n Alexey Reznichenko (alexey.reznichenko@googlemail.com)\n");
  // Button button = new Button(this);
  // button.setText("OK");
  // button.setOnClickListener(new OnClickListener() {
  // @Override
  // public void onClick(View v) {
  // dialog.dismiss();
  // }
  // });
  // layout.addView(textview);
  // layout.addView(button);
  // dialog.setContentView(layout);
  // dialog.setTitle("About PHP for Android");
  // dialog.show();
  //
  // }

  protected synchronized void install() {
    if (mCurrentTask != null) {
      return;
    }

    mProgressLayout.setVisibility(View.VISIBLE);

    mCurrentTask = RunningTask.INSTALL;
    InterpreterInstaller installTask;
    try {
      installTask = getInterpreterInstaller(mDescriptor, Main.this, mTaskListener);
    } catch (Sl4aException e) {
      Log.e(this, e.getMessage(), e);
      return;
    }
    installTask.execute();
  }

  protected synchronized void uninstall() {
    if (mCurrentTask != null) {
      return;
    }

    mProgressLayout.setVisibility(View.VISIBLE);

    mCurrentTask = RunningTask.UNINSTALL;
    InterpreterUninstaller uninstallTask;
    try {
      uninstallTask = getInterpreterUninstaller(mDescriptor, Main.this, mTaskListener);
    } catch (Sl4aException e) {
      Log.e(this, e.getMessage(), e);
      return;
    }
    uninstallTask.execute();
  }

  protected void setInstalled(boolean isInstalled) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putBoolean(InterpreterConstants.INSTALLED_PREFERENCE_KEY, isInstalled);
    // editor.putBoolean(InterpreterConstants.INSTALL_PREF, isInstalled);

    editor.commit();
    broadcastInstallationStateChange(isInstalled);
  }

  protected boolean checkInstalled() {
    boolean isInstalled =
        mPreferences.getBoolean(InterpreterConstants.INSTALLED_PREFERENCE_KEY, false);
    // mPreferences.getBoolean(InterpreterConstants.INSTALL_PREF, false);

    broadcastInstallationStateChange(isInstalled);
    return isInstalled;
  }
}
