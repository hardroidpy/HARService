/*
 * HARService: Activity Recognition Service
 * Copyright (C) 2015 agimenez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hardroid.client;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.hardroid.api.ActivityRecognitionApi;
import org.hardroid.api.ConnectionApi;
import org.hardroid.client.R;
import org.hardroid.common.IActivityRecognitionManager;

import java.util.List;

/**
 * The main entry point for Activity Recognition Service integration
 *
 * This class is used for requesting activity updates. This method requires that Connection
 * be connected.
 */
public class ActivityRecognitionClient implements ConnectionApi {
    public static final String TAG = ActivityRecognitionClient.class.getSimpleName();
    private Context context;
    private boolean connected = false;
    private boolean connecting = false;
    private ActivityRecognitionManager apiManager = null;
    private ComponentName componentName = null;
    private OnClientConnectionListener listener = null;

    public ActivityRecognitionClient(Context context) {
        this.context = context.getApplicationContext();

        checkInstalledService();
    }

    private boolean checkInstalledService() {
        Intent implicit = new Intent(IActivityRecognitionManager.class.getName());
        List<ResolveInfo> matches = this.context.getPackageManager()
                .queryIntentServices(implicit, 0);
        if (matches.size() == 0) {
            Toast.makeText(this.context, R.string.installed_message, Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            ServiceInfo svcInfo = matches.get(0).serviceInfo;
            componentName = new ComponentName(svcInfo.applicationInfo.packageName, svcInfo.name);
            return true;
        }
    }

    private void installService() {
        String packageName = "org.hardroid.server";

        String url = "";
        Intent intent;

        try {
            url = "market://details?id=" + packageName;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            url = "https://play.google.com/store/apps/details?id=" + packageName;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }


    }

    @Override
    public void connect() {
        if (componentName == null && !checkInstalledService()) {
            installService();
            return;
        }
        Intent explicit = new Intent(IActivityRecognitionManager.class.getName());
        explicit.setComponent(componentName);
        if (!this.context.bindService(explicit, connection, Context.BIND_AUTO_CREATE)) {
            this.connected = false;
            this.connecting = false;
            Log.w(TAG, "Failed to bind service");
        }
        else {
            this.connecting = true;
        }
    }

    @Override
    public void disconnect() {
        if (this.isConnected()) {
            this.context.unbindService(connection);
            this.connected = false;
            this.connecting = false;
        }
    }

    @Override
    public void reconnect() {
        this.disconnect();
        this.connect();
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public boolean isConnecting() {
        return this.connecting;
    }

    @Override
    public void addOnConnectionListener(OnClientConnectionListener listener) {
        this.listener = listener;
    }

    /**
     * @return
     */
    public ActivityRecognitionApi getService() {
        return this.apiManager;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "Service IBINDER connected");
            ActivityRecognitionClient.this.connected = true;
            ActivityRecognitionClient.this.connecting = false;
            ActivityRecognitionClient self = ActivityRecognitionClient.this;
            self.apiManager = new ActivityRecognitionManager(
                    self, iBinder, self.context
                    );
            if (listener != null) {
                listener.onConnect(ActivityRecognitionClient.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service IBINDER disconnected");
            if (listener != null) {
                listener.onDisconnect(ActivityRecognitionClient.this);
            }
            ActivityRecognitionClient self = ActivityRecognitionClient.this;
            self.apiManager = null;
            ActivityRecognitionClient.this.connected = false;
        }
    };
}