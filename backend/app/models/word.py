from datetime import UTC, datetime

from sqlalchemy import DateTime, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base


class Word(Base):
    __tablename__ = "words"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    word: Mapped[str] = mapped_column(String(100), unique=True, index=True, nullable=False)
    lemma: Mapped[str] = mapped_column(String(100), nullable=False)
    part_of_speech: Mapped[str] = mapped_column(String(50), nullable=False)
    cefr_level: Mapped[str] = mapped_column(String(2), nullable=False, index=True)
    definition: Mapped[str] = mapped_column(Text, nullable=False)
    example_sentence: Mapped[str] = mapped_column(Text, nullable=False)
    collocations: Mapped[str] = mapped_column(Text, nullable=False)
    synonyms: Mapped[str] = mapped_column(Text, nullable=False)
    source: Mapped[str] = mapped_column(String(100), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(UTC),
        nullable=False,
    )

    user_words: Mapped[list["UserWord"]] = relationship(back_populates="word")
    suggestions: Mapped[list["SuggestionHistory"]] = relationship(back_populates="word")
    reviews: Mapped[list["Review"]] = relationship(back_populates="word")
