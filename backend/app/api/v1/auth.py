from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.dependencies import get_current_user
from app.models.user import User
from app.schemas.auth import AuthUserResponse, LoginRequest, RegisterRequest, TokenResponse
from app.services.auth_service import AuthService
from app.services.user_service import UserService

router = APIRouter(prefix="/auth", tags=["auth"])


@router.post("/register", response_model=TokenResponse)
def register(data: RegisterRequest, db: Session = Depends(get_db)) -> TokenResponse:
    user, token = AuthService(db).register(data)
    return TokenResponse(access_token=token)


@router.post("/login", response_model=TokenResponse)
def login(data: LoginRequest, db: Session = Depends(get_db)) -> TokenResponse:
    _, token = AuthService(db).login(data.email, data.password)
    return TokenResponse(access_token=token)


@router.get("/me", response_model=AuthUserResponse)
def auth_me(current_user: User = Depends(get_current_user)) -> AuthUserResponse:
    return UserService.to_auth_response(current_user)
