package spanishquizapp;

import java.util.List;

public class StatsManager {
    private final DataStore dataStore = DataStore.getInstance();

    public void recordAttempt(User user, Question question, boolean correct) {
        StatsEntry entry = dataStore.getOrCreateStats(user.getUsername(), question.getTopic(), question.getSubtopic());
        entry.recordAttempt(correct);
        dataStore.ensureStatsPersisted();
    }

    public List<StatsEntry> getStudentStats(User user) {
        return dataStore.getStatsForStudent(user.getUsername());
    }

    public List<StatsEntry> getAllStats() {
        return dataStore.getAllStats();
    }
}
