from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.dependencies import get_current_user
from app.models.user import User
from app.schemas.user import UserProfileResponse, UserProfileUpdate
from app.schemas.word import SuggestionListItem, UserWordListItem
from app.services.user_service import UserService
from app.services.word_service import WordService

router = APIRouter(prefix="/users", tags=["users"])


@router.get("/me", response_model=UserProfileResponse)
def get_me(current_user: User = Depends(get_current_user)) -> UserProfileResponse:
    return UserService().get_profile(current_user)


@router.put("/me", response_model=UserProfileResponse)
def update_me(
    data: UserProfileUpdate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
) -> UserProfileResponse:
    return UserService(db).update_profile(current_user, data)


@router.get("/me/words", response_model=list[UserWordListItem])
def list_my_words(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
) -> list[UserWordListItem]:
    return WordService(db).list_user_words(current_user.id)


@router.get("/me/suggestions", response_model=list[SuggestionListItem])
def list_my_suggestions(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
) -> list[SuggestionListItem]:
    return WordService(db).list_suggestions(current_user.id)
