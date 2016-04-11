package com.baozou.imageeditor.event;

/**
 * Created by jiangyu on 2016/4/11.
 */
public class SaveImgeEvent {
    public String fileName;

    public SaveImgeEvent(String name) {
        this.fileName = name;
    }
}
