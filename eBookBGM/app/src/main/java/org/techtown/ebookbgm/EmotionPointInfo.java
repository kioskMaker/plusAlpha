package org.techtown.ebookbgm;

public class EmotionPointInfo {
    String emotion_type;
    int start;
    int end;

    public EmotionPointInfo(String emotion_type, int start, int end) {
        this.emotion_type = emotion_type;
        this.start = start;
        this.end = end;
    }

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
