from datetime import UTC, date, datetime

from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.core.enums import CEFRLevel, UserWordStatus
from app.core.utils import serialize_json_list, word_to_response
from app.models.review import Review
from app.models.user import User
from app.models.user_word import UserWord
from app.models.word import Word
from app.repositories.review_repository import ReviewRepository
from app.repositories.word_repository import WordRepository
from app.schemas.word import (
    CustomWordRequest,
    SuggestionListItem,
    UserWordListItem,
    WordResponse,
)


class WordService:
    def __init__(self, db: Session) -> None:
        self.word_repo = WordRepository(db)
        self.review_repo = ReviewRepository(db)

    def get_word(self, word_id: int) -> WordResponse:
        word = self.word_repo.get_by_id(word_id)
        if word is None:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Word not found")
        return word_to_response(word)

    def add_custom_word(self, user: User, data: CustomWordRequest) -> WordResponse:
        normalized = data.word.strip().lower()
        if not normalized:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Word cannot be empty",
            )

        existing = self.word_repo.get_by_word_text(normalized)
        if existing:
            word = existing
        else:
            word = self._create_mock_enriched_word(normalized, user.cefr_level or CEFRLevel.A1.value)
            word = self.word_repo.create(word)

        user_word = self.word_repo.get_user_word(user.id, word.id)
        if user_word is None:
            user_word = UserWord(
                user_id=user.id,
                word_id=word.id,
                saved=True,
                added_by_user=True,
                status=UserWordStatus.LEARNING.value,
            )
            self.word_repo.add_user_word(user_word)
            self._ensure_review(user.id, word.id)
        else:
            user_word.saved = True
            user_word.added_by_user = True
            self.word_repo.update_user_word(user_word)

        return word_to_response(word)

    def save_word(self, user: User, word_id: int) -> WordResponse:
        word = self.word_repo.get_by_id(word_id)
        if word is None:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Word not found")

        user_word = self.word_repo.get_user_word(user.id, word_id)
        if user_word is None:
            user_word = UserWord(
                user_id=user.id,
                word_id=word_id,
                saved=True,
                added_by_user=False,
                status=UserWordStatus.LEARNING.value,
            )
            self.word_repo.add_user_word(user_word)
            self._ensure_review(user.id, word_id)
        else:
            user_word.saved = True
            self.word_repo.update_user_word(user_word)
            self._ensure_review(user.id, word_id)

        return word_to_response(word)

    def list_user_words(self, user_id: int) -> list[UserWordListItem]:
        items = self.word_repo.get_user_words(user_id)
        return [
            UserWordListItem(
                id=uw.id,
                word=word_to_response(uw.word),
                saved=uw.saved,
                added_by_user=uw.added_by_user,
                status=uw.status,
                created_at=uw.created_at,
            )
            for uw in items
        ]

    def list_suggestions(self, user_id: int) -> list[SuggestionListItem]:
        history = self.word_repo.get_suggestion_history(user_id)
        return [
            SuggestionListItem(
                id=s.id,
                word=word_to_response(s.word),
                suggested_at=s.suggested_at,
            )
            for s in history
        ]

    def _ensure_review(self, user_id: int, word_id: int) -> None:
        review = self.review_repo.get_by_user_and_word(user_id, word_id)
        if review is None:
            review = Review(
                user_id=user_id,
                word_id=word_id,
                repetitions=0,
                interval_days=1,
                ease_factor=2.5,
                due_date=date.today(),
                last_reviewed_at=None,
                status="active",
            )
            self.review_repo.create(review)

    def _create_mock_enriched_word(self, word_text: str, cefr_level: str) -> Word:
        title = word_text.capitalize()
        return Word(
            word=word_text,
            lemma=word_text,
            part_of_speech="noun",
            cefr_level=cefr_level if cefr_level in {e.value for e in CEFRLevel} else CEFRLevel.A1.value,
            definition=f"A mock definition for '{word_text}' (replace with dictionary API later).",
            example_sentence=f"This is an example sentence using the word '{word_text}'.",
            collocations=serialize_json_list([f"{word_text} base", f"learn {word_text}"]),
            synonyms=serialize_json_list(["synonym1", "synonym2"]),
            source="mock_enrichment",
        )
