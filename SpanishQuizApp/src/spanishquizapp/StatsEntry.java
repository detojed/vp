package spanishquizapp;

public class StatsEntry {
    private final String username;
    private final String topic;
    private final String subtopic;
    private int attempts;
    private int correct;

    public StatsEntry(String username, String topic, String subtopic, int attempts, int correct) {
        this.username = username;
        this.topic = topic;
        this.subtopic = subtopic;
        this.attempts = attempts;
        this.correct = correct;
    }

    public String getUsername() {
        return username;
    }

    public String getTopic() {
        return topic;
    }

    public String getSubtopic() {
        return subtopic;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getCorrect() {
        return correct;
    }

    public void recordAttempt(boolean wasCorrect) {
        attempts++;
        if (wasCorrect) {
            correct++;
        }
    }

    public double getAccuracy() {
        if (attempts == 0) {
            return 0.0;
        }
        return (double) correct / attempts;
    }

    public String toLine() {
        return username + "|" + topic + "|" + subtopic + "|" + attempts + "|" + correct;
    }

    public static StatsEntry fromLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid stats format: " + line);
        }
        String username = parts[0].trim();
        String topic = parts[1].trim();
        String subtopic = parts[2].trim();
        int attempts = Integer.parseInt(parts[3].trim());
        int correct = Integer.parseInt(parts[4].trim());
        return new StatsEntry(username, topic, subtopic, attempts, correct);
    }
}
