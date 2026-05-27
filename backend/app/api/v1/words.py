from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.dependencies import get_current_user
from app.models.user import User
from app.schemas.word import CustomWordRequest, WordResponse
from app.services.suggestion_service import SuggestionService
from app.services.word_service import WordService

router = APIRouter(prefix="/words", tags=["words"])


@router.get("/suggest", response_model=WordResponse)
def suggest_word(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
) -> WordResponse:
    return SuggestionService(db).suggest_word(current_user)


@router.get("/{word_id}", response_model=WordResponse)
def get_word(
    word_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
) -> WordResponse:
    return WordService(db).get_word(word_id)


@router.post("/custom", response_model=WordResponse)
def add_custom_word(
    data: CustomWordRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
) -> WordResponse:
    return WordService(db).add_custom_word(current_user, data)


@router.post("/{word_id}/save", response_model=WordResponse)
def save_word(
    word_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
) -> WordResponse:
    return WordService(db).save_word(current_user, word_id)
