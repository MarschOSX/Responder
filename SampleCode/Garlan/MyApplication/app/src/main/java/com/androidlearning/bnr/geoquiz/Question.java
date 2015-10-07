package com.androidlearning.bnr.geoquiz;

/**
 * Created by Garlan on 9/27/2015.
 */
public class Question {
    private int mTextResID;
    private boolean mAnswerTrue;

    public Question(int textResID, boolean answerTrue){
        this.mTextResID = textResID;
        this.mAnswerTrue = answerTrue;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public int getTextResId() {

        return mTextResID;
    }

    public void setTextResID(int textResID) {
        mTextResID = textResID;
    }
}
