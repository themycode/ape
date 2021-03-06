/*
 * Copyright 2020 Advanced Software Technologies Lab at ETH Zurich, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.commands.monkey.ape.events;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.commands.monkey.MonkeyEvent;
import com.android.commands.monkey.MonkeyTouchEvent;
import com.android.commands.monkey.MonkeyWaitEvent;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;

public class ApeClickEvent extends AbstractApeEvent {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    float x;
    float y;
    boolean longClick;

    public ApeClickEvent(PointF point, boolean longClick) {
        this.x = point.x;
        this.y = point.y;
        this.longClick = longClick;
    }
    
    public ApeClickEvent(float x, float y, boolean longClick) {
        this.x = x;
        this.y = y;
        this.longClick = longClick;
    }

    @Override
    public List<MonkeyEvent> generateMonkeyEvents() {
        long downAt = SystemClock.uptimeMillis();
        MonkeyEvent down, wait = null, up;
        down = new MonkeyTouchEvent(MotionEvent.ACTION_DOWN).setDownTime(downAt).addPointer(0, x, y)
                .setIntermediateNote(false);

        if (longClick) {
            wait = new MonkeyWaitEvent(1000);
        }

        up = new MonkeyTouchEvent(MotionEvent.ACTION_UP).setDownTime(downAt).addPointer(0, x, y)
                .setIntermediateNote(false);
        if (wait == null) {
            return Arrays.asList(down, up);
        }
        return Arrays.asList(down, wait, up);
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jEvent = super.toJSONObject();
        jEvent.put("longClick", String.valueOf(longClick));
        jEvent.put("x", String.valueOf(x));
        jEvent.put("y", String.valueOf(y));
        return jEvent;
    }

    public static ApeEvent fromJSONObject(JSONObject jEvent) throws JSONException {
        boolean longClick = jEvent.getBoolean("longClick");
        float x = (float) jEvent.getDouble("x");
        float y = (float) jEvent.getDouble("y");
        return new ApeClickEvent(x, y, longClick);
    }
}
