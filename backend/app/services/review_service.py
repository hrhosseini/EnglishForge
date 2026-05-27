from datetime import UTC, date, datetime, timedelta

from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.core.enums import ReviewAnswer
from app.core.utils import word_to_response
from app.models.review import Review
from app.models.user import User
from app.repositories.review_repository import ReviewRepository
from app.repositories.word_repository import WordRepository
from app.schemas.review import ReviewAnswerResponse, ReviewDueItem


class ReviewService:
    DEFAULT_EASE = 2.5

    def __init__(self, db: Session) -> None:
        self.review_repo = ReviewRepository(db)
        self.word_repo = WordRepository(db)

    def get_due_reviews(self, user: User) -> list[ReviewDueItem]:
        today = date.today()
        reviews = self.review_repo.get_due_reviews(user.id, today)
        return [
            ReviewDueItem(
                id=r.id,
                word=word_to_response(r.word),
                repetitions=r.repetitions,
                intervalDays=r.interval_days,
                easeFactor=r.ease_factor,
                dueDate=r.due_date,
                lastReviewedAt=r.last_reviewed_at,
                status=r.status,
            )
            for r in reviews
        ]

    def submit_answer(
        self,
        user: User,
        word_id: int,
        answer: ReviewAnswer,
    ) -> ReviewAnswerResponse:
        word = self.word_repo.get_by_id(word_id)
        if word is None:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Word not found")

        review = self.review_repo.get_by_user_and_word(user.id, word_id)
        if review is None:
            review = Review(
                user_id=user.id,
                word_id=word_id,
                repetitions=0,
                interval_days=1,
                ease_factor=self.DEFAULT_EASE,
                due_date=date.today(),
                status="active",
            )
            review = self.review_repo.create(review)

        current_interval = max(review.interval_days, 1)
        new_interval, new_ease = self._calculate_interval(answer, current_interval, review.ease_factor)

        review.repetitions += 1
        review.interval_days = new_interval
        review.ease_factor = new_ease
        review.due_date = date.today() + timedelta(days=new_interval)
        review.last_reviewed_at = datetime.now(UTC)
        review.status = "active"
        review = self.review_repo.update(review)

        return ReviewAnswerResponse(
            id=review.id,
            word_id=review.word_id,
            repetitions=review.repetitions,
            intervalDays=review.interval_days,
            easeFactor=review.ease_factor,
            dueDate=review.due_date,
            lastReviewedAt=review.last_reviewed_at,
            status=review.status,
        )

    def _calculate_interval(
        self,
        answer: ReviewAnswer,
        current_interval: int,
        ease_factor: float,
    ) -> tuple[int, float]:
        if answer == ReviewAnswer.AGAIN:
            return 1, max(1.3, ease_factor - 0.2)
        if answer == ReviewAnswer.HARD:
            return 2, max(1.3, ease_factor - 0.15)
        if answer == ReviewAnswer.GOOD:
            return max(current_interval * 2, 3), ease_factor
        # easy
        return max(current_interval * 3, 5), min(3.0, ease_factor + 0.15)
