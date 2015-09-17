package com.mapplinks.rdcalculator;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Aditya Vikram on 9/11/2015.
 */
public class FontChangeCrawler {
    private Typeface typeface;

    public FontChangeCrawler(Typeface typeface) {
        this.typeface = typeface;
    }

    public FontChangeCrawler(AssetManager assets, String assetsFontFileName) {
        typeface = Typeface.createFromAsset(assets, assetsFontFileName);
    }

    public void replaceFonts(ViewGroup viewTree) {
        View child;
        for (int i = 0; i < viewTree.getChildCount(); ++i) {
            child = viewTree.getChildAt(i);
            if (child instanceof ViewGroup) {
                replaceFonts((ViewGroup) child);
            } else if (child instanceof TextView) {
                ((TextView) child).setTypeface(typeface);
            }
            else if (child instanceof Button) {
                ((Button) child).setTypeface(typeface);
            }
        }
    }
}