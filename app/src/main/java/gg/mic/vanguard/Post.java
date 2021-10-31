package gg.mic.vanguard;

public class Post {

    public String title;
    public String description;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Post(String f1, String f2) {
        this.title = f1;
        this.description = f2;
    }

}
