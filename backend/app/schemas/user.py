from pydantic import BaseModel, Field, field_validator

from app.core.enums import CEFRLevel, Interest


class UserProfileResponse(BaseModel):
    id: int
    email: str
    display_name: str | None
    cefr_level: str | None
    interests: list[str]

    model_config = {"from_attributes": True}


class UserProfileUpdate(BaseModel):
    display_name: str | None = Field(default=None, max_length=100)
    cefr_level: CEFRLevel | None = None
    interests: list[Interest] | None = None

    @field_validator("interests")
    @classmethod
    def validate_interests(cls, v: list[Interest] | None) -> list[Interest] | None:
        if v is not None and len(v) == 0:
            return []
        return v
