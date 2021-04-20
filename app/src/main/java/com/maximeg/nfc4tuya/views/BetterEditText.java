package com.maximeg.nfc4tuya.views;

import android.content.Context;
import android.util.AttributeSet;

import com.maximeg.nfc4tuya.interfaces.BetterEditTextListener;

import java.util.ArrayList;

public class BetterEditText extends androidx.appcompat.widget.AppCompatEditText
{
    private ArrayList<BetterEditTextListener> listeners;

    public BetterEditText(Context context)
    {
        super(context);
        listeners = new ArrayList<>();
    }

    public BetterEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        listeners = new ArrayList<>();
    }

    public BetterEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        listeners = new ArrayList<>();
    }

    public void addActionListener(BetterEditTextListener listener) {
        try {
            listeners.add(listener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id){
            case android.R.id.cut:
                onTextCut(getEditableText().toString());
                break;
            case android.R.id.paste:
                onTextPaste(getEditableText().toString());
                break;
            case android.R.id.copy:
                onTextCopy(getEditableText().toString());
        }
        return consumed;
    }

    public void onTextCut(String text){
        for (BetterEditTextListener listener : listeners) {
            listener.onTextCut(text);
        }
    }

    public void onTextCopy(String text){
        for (BetterEditTextListener listener : listeners) {
            listener.onTextCopy(text);
        }
    }

    public void onTextPaste(String text){
        for (BetterEditTextListener listener : listeners) {
            listener.onTextPaste(text);
        }
    }
}