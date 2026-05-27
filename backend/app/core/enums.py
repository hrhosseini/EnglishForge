import enum


class CEFRLevel(str, enum.Enum):
    A1 = "A1"
    A2 = "A2"
    B1 = "B1"
    B2 = "B2"
    C1 = "C1"
    C2 = "C2"


class Interest(str, enum.Enum):
    WORK = "work"
    TRAVEL = "travel"
    STUDY = "study"
    TECHNOLOGY = "technology"
    DAILY_LIFE = "daily life"
    BUSINESS = "business"
    ACADEMIC_ENGLISH = "academic English"


class ReviewAnswer(str, enum.Enum):
    AGAIN = "again"
    HARD = "hard"
    GOOD = "good"
    EASY = "easy"


class UserWordStatus(str, enum.Enum):
    LEARNING = "learning"
    REVIEWING = "reviewing"
    MASTERED = "mastered"


class ReviewStatus(str, enum.Enum):
    ACTIVE = "active"
    SUSPENDED = "suspended"
