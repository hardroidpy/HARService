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

/**
 * From Cursor to HumanActivityFeed Model
 */
public class HumanActivityDataCreator {

    public HumanActivityData[] createFromCursor(Cursor cursor) {
        HumanActivityData[] result = this.newArray(cursor.getCount());
        int i = 0;
        ContentValues values = new ContentValues();
        while (cursor.moveToNext()) {
            DatabaseUtils.cursorRowToContentValues(cursor, values);
            result[i] = new HumanActivityData(values);
        }

        return result;
    }

    public HumanActivityData[] newArray(int size) {
        return new HumanActivityData[size];
    }
}
