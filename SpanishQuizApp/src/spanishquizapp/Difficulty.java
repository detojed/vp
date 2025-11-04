package spanishquizapp;

enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

    public Difficulty increase() {
        switch (this) {
            case EASY:
                return MEDIUM;
            case MEDIUM:
                return HARD;
            default:
                return HARD;
        }
    }

    public Difficulty decrease() {
        switch (this) {
            case HARD:
                return MEDIUM;
            case MEDIUM:
                return EASY;
            default:
                return EASY;
        }
    }
}
