from datetime import date, datetime

from pydantic import BaseModel

from app.core.enums import ReviewAnswer
from app.schemas.word import WordResponse


class ReviewDueItem(BaseModel):
    id: int
    word: WordResponse
    repetitions: int
    intervalDays: int
    easeFactor: float
    dueDate: date
    lastReviewedAt: datetime | None
    status: str


class ReviewAnswerRequest(BaseModel):
    answer: ReviewAnswer


class ReviewAnswerResponse(BaseModel):
    id: int
    word_id: int
    repetitions: int
    intervalDays: int
    easeFactor: float
    dueDate: date
    lastReviewedAt: datetime | None
    status: str
