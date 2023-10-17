package cn.iocoder.springboot.lab27.springwebflux.controller;

import java.beans.PropertyEditorSupport;

public class StringTrimmerEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        System.out.println("exec trim, text=" + text);
        text = text == null ? null : text.trim();
        setValue(text);
    }
}
