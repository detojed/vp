package spanishquizapp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class QuizSession {
    private static final int WINDOW_SIZE = 10;

    private final String topic;
    private final String subtopic;
    private final List<Question> questions;
    private final Deque<Boolean> recentAnswers = new ArrayDeque<>();
    private final Random random = new Random();

    private Difficulty currentDifficulty = Difficulty.MEDIUM;
    private Question currentQuestion;

    public QuizSession(String topic, String subtopic, List<Question> questions) {
        this.topic = topic;
        this.subtopic = subtopic;
        this.questions = new ArrayList<>(questions);
    }

    public String getTopic() {
        return topic;
    }

    public String getSubtopic() {
        return subtopic;
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public Question nextQuestion() {
        List<Question> filtered = questions.stream()
                .filter(q -> q.getDifficulty() == currentDifficulty)
                .collect(Collectors.toList());
        if (filtered.isEmpty()) {
            filtered = new ArrayList<>(questions);
        }
        if (filtered.isEmpty()) {
            return null;
        }
        currentQuestion = filtered.get(random.nextInt(filtered.size()));
        return currentQuestion;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void recordAnswer(boolean correct) {
        recentAnswers.addLast(correct);
        if (recentAnswers.size() > WINDOW_SIZE) {
            recentAnswers.removeFirst();
        }
        adjustDifficulty();
    }

    private void adjustDifficulty() {
        if (recentAnswers.isEmpty()) {
            return;
        }
        long correctCount = recentAnswers.stream().filter(b -> b).count();
        double accuracy = (double) correctCount / recentAnswers.size();
        if (accuracy >= 0.8) {
            currentDifficulty = currentDifficulty.increase();
        } else if (accuracy <= 0.5) {
            currentDifficulty = currentDifficulty.decrease();
        }
    }
}
