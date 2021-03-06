/*
 * HARService: Activity Recognition Service
 * Copyright (C) 2015 agimenez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.harsurvey.android.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Creator
 */
public class DataCreator<T> {
    private static final String TAG = DataCreator.class.getSimpleName();
    Class<T> clazz;

    public DataCreator(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> createFromCursor(Cursor cursor) {
        ArrayList<T> result = new ArrayList<>();
        try {
            ContentValues values = new ContentValues();
            while (cursor.moveToNext()) {
                DatabaseUtils.cursorRowToContentValues(cursor, values);
                result.add(clazz.getConstructor(ContentValues.class).newInstance(values));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error obtaining data " + e.getMessage());
        }
        return result;
    }

    public T createSingleFromCursor(Cursor cursor) {
        try {
            ContentValues values = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, values);
            return clazz.getConstructor(ContentValues.class).newInstance(values);
        } catch (Exception e) {
            return null;
        }
    }
}
