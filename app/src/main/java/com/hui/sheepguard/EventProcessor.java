package com.hui.sheepguard;

import android.view.accessibility.AccessibilityEvent;

public interface EventProcessor {
    void onAccessibilityEvent(AccessibilityEvent event);
    void stop();
}
