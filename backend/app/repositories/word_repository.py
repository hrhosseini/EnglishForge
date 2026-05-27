from sqlalchemy import func, select
from sqlalchemy.orm import Session, joinedload

from app.models.suggestion_history import SuggestionHistory
from app.models.user_word import UserWord
from app.models.word import Word


class WordRepository:
    def __init__(self, db: Session) -> None:
        self.db = db

    def get_by_id(self, word_id: int) -> Word | None:
        return self.db.get(Word, word_id)

    def get_by_word_text(self, word_text: str) -> Word | None:
        stmt = select(Word).where(func.lower(Word.word) == word_text.lower().strip())
        return self.db.scalars(stmt).first()

    def create(self, word: Word) -> Word:
        self.db.add(word)
        self.db.commit()
        self.db.refresh(word)
        return word

    def get_suggested_word_ids(self, user_id: int) -> set[int]:
        stmt = select(SuggestionHistory.word_id).where(SuggestionHistory.user_id == user_id)
        return set(self.db.scalars(stmt).all())

    def find_word_for_level(
        self,
        cefr_level: str,
        exclude_word_ids: set[int],
    ) -> Word | None:
        stmt = select(Word).where(Word.cefr_level == cefr_level)
        if exclude_word_ids:
            stmt = stmt.where(Word.id.not_in(exclude_word_ids))
        stmt = stmt.order_by(func.random()).limit(1)
        return self.db.scalars(stmt).first()

    def find_any_word(self, exclude_word_ids: set[int]) -> Word | None:
        stmt = select(Word)
        if exclude_word_ids:
            stmt = stmt.where(Word.id.not_in(exclude_word_ids))
        stmt = stmt.order_by(func.random()).limit(1)
        return self.db.scalars(stmt).first()

    def get_user_words(self, user_id: int) -> list[UserWord]:
        stmt = (
            select(UserWord)
            .where(UserWord.user_id == user_id)
            .options(joinedload(UserWord.word))
            .order_by(UserWord.created_at.desc())
        )
        return list(self.db.scalars(stmt).unique().all())

    def get_user_word(self, user_id: int, word_id: int) -> UserWord | None:
        stmt = select(UserWord).where(
            UserWord.user_id == user_id,
            UserWord.word_id == word_id,
        )
        return self.db.scalars(stmt).first()

    def get_suggestion_history(self, user_id: int) -> list[SuggestionHistory]:
        stmt = (
            select(SuggestionHistory)
            .where(SuggestionHistory.user_id == user_id)
            .options(joinedload(SuggestionHistory.word))
            .order_by(SuggestionHistory.suggested_at.desc())
        )
        return list(self.db.scalars(stmt).unique().all())

    def add_suggestion(self, suggestion: SuggestionHistory) -> SuggestionHistory:
        self.db.add(suggestion)
        self.db.commit()
        self.db.refresh(suggestion)
        return suggestion

    def add_user_word(self, user_word: UserWord) -> UserWord:
        self.db.add(user_word)
        self.db.commit()
        self.db.refresh(user_word)
        return user_word

    def update_user_word(self, user_word: UserWord) -> UserWord:
        self.db.commit()
        self.db.refresh(user_word)
        return user_word
