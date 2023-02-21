package org.techtown.ebookbgm;

public class EmotionPointInfo {
    int chapter;
    String emotion_type;
    int start;
    int end;

    public EmotionPointInfo(int chapter, String emotion_type, int start, int end) {
        this.chapter = chapter;
        this.emotion_type = emotion_type;
        this.start = start;
        this.end = end;
    }
    public int getChapter(){return chapter;}
    public String getEmotion_type(){
        return emotion_type;
    }
    public int getStart(){
        return start;
    }
    public int getEnd(){
        return end;
    }
}
