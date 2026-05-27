from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.core.utils import parse_user_interests, set_user_interests
from app.models.user import User
from app.repositories.user_repository import UserRepository
from app.schemas.auth import AuthUserResponse
from app.schemas.user import UserProfileResponse, UserProfileUpdate


class UserService:
    def __init__(self, db: Session) -> None:
        self.user_repo = UserRepository(db)

    def get_profile(self, user: User) -> UserProfileResponse:
        return UserProfileResponse(
            id=user.id,
            email=user.email,
            display_name=user.display_name,
            cefr_level=user.cefr_level,
            interests=parse_user_interests(user),
        )

    def update_profile(self, user: User, data: UserProfileUpdate) -> UserProfileResponse:
        if data.display_name is not None:
            user.display_name = data.display_name
        if data.cefr_level is not None:
            user.cefr_level = data.cefr_level.value
        if data.interests is not None:
            set_user_interests(user, [i.value for i in data.interests])

        user = self.user_repo.update(user)
        return self.get_profile(user)

    @staticmethod
    def to_auth_response(user: User) -> AuthUserResponse:
        return AuthUserResponse(
            id=user.id,
            email=user.email,
            display_name=user.display_name,
            cefr_level=user.cefr_level,
            interests=parse_user_interests(user),
        )
