from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.dependencies import get_current_user
from app.models.user import User
from app.schemas.review import ReviewAnswerRequest, ReviewAnswerResponse, ReviewDueItem
from app.services.review_service import ReviewService

router = APIRouter(prefix="/reviews", tags=["reviews"])


@router.get("/due", response_model=list[ReviewDueItem])
def get_due_reviews(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
) -> list[ReviewDueItem]:
    return ReviewService(db).get_due_reviews(current_user)


@router.post("/{word_id}/answer", response_model=ReviewAnswerResponse)
def submit_review_answer(
    word_id: int,
    data: ReviewAnswerRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
) -> ReviewAnswerResponse:
    return ReviewService(db).submit_answer(current_user, word_id, data.answer)
