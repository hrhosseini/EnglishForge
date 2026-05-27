from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.core.enums import CEFRLevel
from app.core.security import create_access_token, get_password_hash, verify_password
from app.models.user import User
from app.repositories.user_repository import UserRepository
from app.schemas.auth import RegisterRequest


class AuthService:
    def __init__(self, db: Session) -> None:
        self.user_repo = UserRepository(db)

    def register(self, data: RegisterRequest) -> tuple[User, str]:
        if self.user_repo.get_by_email(data.email):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Email already registered",
            )

        user = User(
            email=data.email.lower(),
            hashed_password=get_password_hash(data.password),
            display_name=data.display_name,
            cefr_level=CEFRLevel.A1.value,
            interests=None,
        )
        user = self.user_repo.create(user)
        token = create_access_token(user.id)
        return user, token

    def login(self, email: str, password: str) -> tuple[User, str]:
        user = self.user_repo.get_by_email(email)
        if user is None or not verify_password(password, user.hashed_password):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Incorrect email or password",
            )
        token = create_access_token(user.id)
        return user, token
