package spanishquizapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DataStore {
    private static final String DATA_DIR = "data";
    private static final Path USERS_FILE = Paths.get(DATA_DIR, "users.txt");
    private static final Path QUESTIONS_FILE = Paths.get(DATA_DIR, "questions.txt");
    private static final Path STATS_FILE = Paths.get(DATA_DIR, "stats.txt");

    private static final DataStore INSTANCE = new DataStore();

    private final List<User> users = new ArrayList<>();
    private final List<Question> questions = new ArrayList<>();
    private final Map<String, StatsEntry> stats = new HashMap<>();

    private DataStore() {
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    public void init() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            loadUsers();
            loadQuestions();
            loadStats();
            if (users.isEmpty()) {
                seedUsers();
            }
            if (questions.isEmpty()) {
                seedQuestions();
            }
            if (stats.isEmpty()) {
                seedStats();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize data store", e);
        }
    }

    private void loadUsers() throws IOException {
        users.clear();
        if (Files.notExists(USERS_FILE)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(USERS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length < 3) {
                    continue;
                }
                String username = parts[0].trim();
                String password = parts[1].trim();
                Role role = Role.valueOf(parts[2].trim());
                users.add(new User(username, password, role));
            }
        }
    }

    private void loadQuestions() throws IOException {
        questions.clear();
        if (Files.notExists(QUESTIONS_FILE)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(QUESTIONS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    questions.add(Question.fromLine(line));
                } catch (IllegalArgumentException ex) {
                    // skip malformed line
                }
            }
        }
    }

    private void loadStats() throws IOException {
        stats.clear();
        if (Files.notExists(STATS_FILE)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(STATS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    StatsEntry entry = StatsEntry.fromLine(line);
                    stats.put(key(entry.getUsername(), entry.getTopic(), entry.getSubtopic()), entry);
                } catch (IllegalArgumentException ex) {
                    // skip malformed
                }
            }
        }
    }

    private void seedUsers() throws IOException {
        users.add(new User("tidmarsh", "spanish123", Role.TEACHER));
        users.add(new User("vuk", "1234", Role.STUDENT));
        saveUsers();
    }

    private void seedQuestions() throws IOException {
        List<Question> seed = new ArrayList<>();
        seed.add(new Question("Comida", "Sustantivos", Difficulty.EASY, "¿Cómo se dice 'apple' en español?",
                List.of("manzana", "plátano", "pera", "uva"), 0));
        seed.add(new Question("Comida", "Sustantivos", Difficulty.MEDIUM, "Selecciona la palabra que significa 'bread'.",
                List.of("queso", "pan", "aceite", "carne"), 1));
        seed.add(new Question("Comida", "Sustantivos", Difficulty.HARD, "¿Cuál es la traducción correcta de 'seafood'?",
                List.of("mariscos", "embutidos", "legumbres", "golosinas"), 0));
        seed.add(new Question("Comida", "Verbos", Difficulty.EASY, "'Cocinar' significa...",
                List.of("to cook", "to eat", "to buy", "to clean"), 0));
        seed.add(new Question("Comida", "Verbos", Difficulty.MEDIUM, "¿Qué verbo significa 'to taste'?",
                List.of("probar", "cortar", "mezclar", "freír"), 0));
        seed.add(new Question("Comida", "Verbos", Difficulty.HARD, "Elige el verbo que significa 'to season'.",
                List.of("sazonar", "hervir", "asar", "amasar"), 0));
        seed.add(new Question("Escuela", "Frases", Difficulty.EASY, "'Tengo deberes' significa...",
                List.of("I have homework", "I have lunch", "I have class", "I have a pen"), 0));
        seed.add(new Question("Escuela", "Frases", Difficulty.MEDIUM, "Selecciona la frase correcta para 'Where is the classroom?'",
                List.of("¿Dónde está el aula?", "¿Dónde está el profesor?", "¿Dónde está el libro?", "¿Dónde está el recreo?"), 0));
        seed.add(new Question("Escuela", "Frases", Difficulty.HARD, "¿Cómo se dice 'I need to study for the exam' en español?",
                List.of("Necesito estudiar para el examen", "Necesito escribir en el examen", "Necesito hablar con el examen", "Necesito correr al examen"), 0));
        questions.addAll(seed);
        saveQuestions();
    }

    private void seedStats() throws IOException {
        stats.clear();
        saveStats();
    }

    public Optional<User> findUser(String username) {
        return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public void addStudent(String username, String password) throws IOException {
        users.add(new User(username, password, Role.STUDENT));
        saveUsers();
    }

    private void saveUsers() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(USERS_FILE)) {
            for (User user : users) {
                writer.write(user.getUsername() + "|" + user.getPassword() + "|" + user.getRole().name());
                writer.newLine();
            }
        }
    }

    private void saveQuestions() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(QUESTIONS_FILE)) {
            for (Question question : questions) {
                writer.write(question.toLine());
                writer.newLine();
            }
        }
    }

    public void saveStats() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(STATS_FILE)) {
            List<StatsEntry> sorted = new ArrayList<>(stats.values());
            sorted.sort(Comparator.comparing(StatsEntry::getUsername)
                    .thenComparing(StatsEntry::getTopic)
                    .thenComparing(StatsEntry::getSubtopic));
            for (StatsEntry entry : sorted) {
                writer.write(entry.toLine());
                writer.newLine();
            }
        }
    }

    private String key(String username, String topic, String subtopic) {
        return username.toLowerCase() + "|" + topic.toLowerCase() + "|" + subtopic.toLowerCase();
    }

    public StatsEntry getOrCreateStats(String username, String topic, String subtopic) {
        String key = key(username, topic, subtopic);
        return stats.computeIfAbsent(key, k -> new StatsEntry(username, topic, subtopic, 0, 0));
    }

    public List<StatsEntry> getStatsForStudent(String username) {
        return stats.values().stream()
                .filter(entry -> entry.getUsername().equalsIgnoreCase(username))
                .sorted(Comparator.comparing(StatsEntry::getTopic).thenComparing(StatsEntry::getSubtopic))
                .collect(Collectors.toList());
    }

    public List<StatsEntry> getAllStats() {
        return new ArrayList<>(stats.values());
    }

    public List<Question> getQuestionsFor(String topic, String subtopic) {
        return questions.stream()
                .filter(q -> q.getTopic().equalsIgnoreCase(topic) && q.getSubtopic().equalsIgnoreCase(subtopic))
                .collect(Collectors.toList());
    }

    public Set<String> getTopics() {
        Set<String> topics = new HashSet<>();
        for (Question question : questions) {
            topics.add(question.getTopic());
        }
        return topics.stream().sorted().collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    public Set<String> getSubtopics(String topic) {
        return questions.stream()
                .filter(q -> q.getTopic().equalsIgnoreCase(topic))
                .map(Question::getSubtopic)
                .collect(Collectors.toCollection(() -> new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER)));
    }

    public void ensureStatsPersisted() {
        try {
            saveStats();
        } catch (IOException e) {
            throw new RuntimeException("Unable to save stats", e);
        }
    }
}
