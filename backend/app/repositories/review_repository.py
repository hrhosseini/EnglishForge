from datetime import date

from sqlalchemy import select
from sqlalchemy.orm import Session, joinedload

from app.models.review import Review


class ReviewRepository:
    def __init__(self, db: Session) -> None:
        self.db = db

    def get_by_user_and_word(self, user_id: int, word_id: int) -> Review | None:
        stmt = select(Review).where(
            Review.user_id == user_id,
            Review.word_id == word_id,
        )
        return self.db.scalars(stmt).first()

    def get_due_reviews(self, user_id: int, today: date) -> list[Review]:
        stmt = (
            select(Review)
            .where(
                Review.user_id == user_id,
                Review.due_date <= today,
                Review.status == "active",
            )
            .options(joinedload(Review.word))
            .order_by(Review.due_date.asc())
        )
        return list(self.db.scalars(stmt).unique().all())

    def create(self, review: Review) -> Review:
        self.db.add(review)
        self.db.commit()
        self.db.refresh(review)
        return review

    def update(self, review: Review) -> Review:
        self.db.commit()
        self.db.refresh(review)
        return review
