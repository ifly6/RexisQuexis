package com.ifly6.rexisquexis.groups;

/**
 * @author ifly6
 */
public class RqResolutionData {

    private String resolutionName;
    private int num;
    private String category;
    private String strength;
    private boolean repealed;
    private String author;

    private int postNum;

    public RqResolutionData(String resolutionName, int resolutionNum, String category, String strength, int postNum,
                            boolean repealed, String author) {
        this.resolutionName = resolutionName;
        this.num = resolutionNum;
        this.category = category;
        this.strength = strength;
        this.postNum = postNum;
        this.repealed = repealed;
        this.author = author;
    }

    public String name() {
        return resolutionName;
    }

    public int num() {
        return num;
    }

    public String strength() {
        return strength;
    }

    public String category() {
        return category;
    }

    public int postNum() {
        return postNum;
    }

    public boolean isRepealed() {
        return repealed;
    }

    public void setRepealed(boolean repealed) {
        this.repealed = repealed;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
