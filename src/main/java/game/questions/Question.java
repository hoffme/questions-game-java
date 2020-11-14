package game.questions;

public class Question {

    public final String title;
    public final String type;
    public final String correctAnswer;

    public Question(String title, String type, String correctAnswer) {
        this.title = title;
        this.type = type;
        this.correctAnswer = correctAnswer;
    }

    public String answer() {
        return "";
    }
}
