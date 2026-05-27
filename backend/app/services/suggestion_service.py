from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.core.enums import CEFRLevel
from app.core.utils import word_to_response
from app.models.suggestion_history import SuggestionHistory
from app.models.user import User
from app.repositories.word_repository import WordRepository
from app.schemas.word import WordResponse


class SuggestionService:
    def __init__(self, db: Session) -> None:
        self.word_repo = WordRepository(db)

    def suggest_word(self, user: User) -> WordResponse:
        cefr_level = user.cefr_level or CEFRLevel.A1.value
        if cefr_level not in {level.value for level in CEFRLevel}:
            cefr_level = CEFRLevel.A1.value

        suggested_ids = self.word_repo.get_suggested_word_ids(user.id)

        word = self.word_repo.find_word_for_level(cefr_level, suggested_ids)
        if word is None:
            word = self.word_repo.find_any_word(suggested_ids)
        if word is None and suggested_ids:
            word = self.word_repo.find_word_for_level(cefr_level, set())
        if word is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="No words available for suggestion. Run seed script first.",
            )

        suggestion = SuggestionHistory(user_id=user.id, word_id=word.id)
        self.word_repo.add_suggestion(suggestion)

        return word_to_response(word)
