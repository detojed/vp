package spanishquizapp;

import java.util.Arrays;
import java.util.List;

public class Question {
    private final String topic;
    private final String subtopic;
    private final Difficulty difficulty;
    private final String prompt;
    private final List<String> options;
    private final int correctIndex;

    public Question(String topic, String subtopic, Difficulty difficulty, String prompt, List<String> options, int correctIndex) {
        this.topic = topic;
        this.subtopic = subtopic;
        this.difficulty = difficulty;
        this.prompt = prompt;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getTopic() {
        return topic;
    }

    public String getSubtopic() {
        return subtopic;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getPrompt() {
        return prompt;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public boolean isCorrect(int index) {
        return index == correctIndex;
    }

    public static Question fromLine(String line) {
        // topic|subtopic|difficulty|prompt|optA;optB;optC;optD|correctIndex
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid question format: " + line);
        }
        String topic = parts[0].trim();
        String subtopic = parts[1].trim();
        Difficulty difficulty = Difficulty.valueOf(parts[2].trim());
        String prompt = parts[3].trim();
        List<String> options = Arrays.asList(parts[4].split(";"));
        int correctIndex = Integer.parseInt(parts[5].trim());
        return new Question(topic, subtopic, difficulty, prompt, options, correctIndex);
    }

    public String toLine() {
        String optionsJoined = String.join(";", options);
        return String.join("|", Arrays.asList(topic, subtopic, difficulty.name(), prompt, optionsJoined, String.valueOf(correctIndex)));
    }
}
