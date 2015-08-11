/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.util;

public class AutoCompleteData {
    private final String label;
    private final String value;
    private final String desc;
    

    public AutoCompleteData(String _label, String _value, String _desc) {
        super();
        this.label = _label;
        this.value = _value;
        this.desc = _desc;
    }

    public final String getLabel() {
        return this.label;
    }

    public final String getValue() {
        return this.value;
    }

    public final String getDesc() {
        return this.desc;
    }
}