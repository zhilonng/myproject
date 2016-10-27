package com.cn21.speedtest.model;

import java.io.Serializable;

/**
 * Created by shenpeng on 2016/8/3.
 */
public class Node implements Serializable{

    String value;
    int key;
    String content;

    public Node(String value, int key, String content){
        this.value=value;
        this.key=key;
        this.content=content;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getContent(){return content;}

    public void setContent(){this.content=content;}

}
