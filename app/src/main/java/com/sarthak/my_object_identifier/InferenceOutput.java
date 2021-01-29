package com.sarthak.my_object_identifier;

import android.widget.TextView;

public class InferenceOutput {
    private TextView label1;
    private TextView label2;
    private TextView label3;
    private TextView Confidence1;
    private TextView Confidence2;
    private TextView Confidence3;

    public TextView getLabel1() {
        return label1;
    }

    public void setLabel1(TextView label1) {
        this.label1 = label1;
    }

    public TextView getLabel2() {
        return label2;
    }

    public void setLabel2(TextView label2) {
        this.label2 = label2;
    }

    public TextView getLabel3() {
        return label3;
    }

    public void setLabel3(TextView label3) {
        this.label3 = label3;
    }

    public TextView getConfidence1() {
        return Confidence1;
    }

    public void setConfidence1(TextView confidence1) {
        Confidence1 = confidence1;
    }

    public TextView getConfidence2() {
        return Confidence2;
    }

    public void setConfidence2(TextView confidence2) {
        Confidence2 = confidence2;
    }

    public TextView getConfidence3() {
        return Confidence3;
    }

    public void setConfidence3(TextView confidence3) {
        Confidence3 = confidence3;
    }

}
