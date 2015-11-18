/*
 * Copyright 2012 Google Inc.
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

package com.example.xyzreader.utils;

import android.app.Activity;
import android.content.DialogInterface;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Utilities for creating dialogs.
 *
 * MaterialDialog implementation based on code from https://github.com/afollestad/material-dialogs
 * @author Kyle Parker
 */
public class DialogUtils {

    public static final int DEFAULT_MESSAGE_ID = -1;
    public static final int DEFAULT_TITLE_ID = -1;

    private static MaterialDialog createProgressDialog(final Activity activity, boolean spinner, int titleId, int messageId,
                                                       boolean cancelable, int max, DialogInterface.OnCancelListener onCancelListener,
                                                       Object... formatArgs) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .cancelable(cancelable);

        if (titleId > DEFAULT_TITLE_ID) {
            builder.title(messageId > DEFAULT_MESSAGE_ID ? activity.getString(titleId) : activity.getString(titleId, formatArgs));
        }
        if (messageId > DEFAULT_MESSAGE_ID) {
            builder.content(activity.getString(messageId, formatArgs));
        }
        if (onCancelListener != null) {
            builder.cancelListener(onCancelListener);
        }
        if (spinner) {
            builder.progress(true, 0);
        } else {
            builder.progress(false, max, true);
        }

        return builder.build();
    }

    /**
     * Creates a spinner progress dialog.
     *
     * @param activity
     * @param titleId
     * @param messageId
     * @param cancelable
     * @return
     */
    public static MaterialDialog createSpinnerProgressDialog(Activity activity, int titleId, int messageId, boolean cancelable) {
        return createProgressDialog(activity, true, titleId, messageId, cancelable, 0, null);
    }
}
