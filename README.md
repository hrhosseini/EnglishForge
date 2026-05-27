# EnglishForge

EnglishForge is a vocabulary learning project with two main parts:

- `android/` — Android client built with Kotlin, Jetpack Compose, Retrofit, Room, and DataStore.
- `backend/` — FastAPI backend with JWT authentication, word suggestions, custom vocabulary, and spaced repetition review.

The app helps learners build practical English vocabulary with CEFR-based word suggestions, example sentences, collocations, saved words, and review sessions.

---

## Repository structure

```text
/android      # Android app project
/backend      # FastAPI backend and API
README.md     # This file
```

### Android

- Kotlin + Jetpack Compose UI
- Retrofit + Moshi for REST API
- Room for local persistence
- DataStore for preferences
- Secrets Gradle Plugin for configuration

### Backend

- FastAPI + Uvicorn
- SQLAlchemy + Alembic migrations
- SQLite for development, PostgreSQL ready for production
- JWT auth, user profiles, word suggestions, saved words, review answers

---

## Getting started

### Backend setup

1. Open a terminal and go to the backend directory:

```bash
cd backend
```

2. Create and activate a virtual environment:

```bash
python3 -m venv .venv
source .venv/bin/activate
```

3. Install dependencies:

```bash
pip install --upgrade pip
pip install -r requirements.txt
```

4. Copy and configure environment variables:

```bash
cp .env.example .env
```

Edit `backend/.env` if you need to change the port, database, JWT secret, or CORS origins.

5. Run database migrations:

```bash
alembic upgrade head
```

6. Seed the vocabulary data:

```bash
python -m app.seed.seed_words
```

7. Run the API server:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

Then open:

- http://127.0.0.1:8000/docs
- http://127.0.0.1:8000/health

#### Backend environment variables

- `APP_NAME`
- `DEBUG`
- `ENVIRONMENT`
- `HOST`
- `PORT`
- `DATABASE_URL`
- `SECRET_KEY`
- `ALGORITHM`
- `ACCESS_TOKEN_EXPIRE_MINUTES`
- `CORS_ORIGINS`

---

### Android setup

1. Open the `android/` folder in Android Studio.
2. Ensure your SDK, JDK, and Gradle are configured for Android API 36.
3. Build and run the app on an emulator or device.

#### Backend connection notes

- Android emulator: use `http://10.0.2.2:8000`
- Physical device: use `http://<YOUR_COMPUTER_IP>:8000`
- Ensure `backend/.env` includes the corresponding origin in `CORS_ORIGINS`

---

## API overview

The backend exposes standard auth, user, word, and review endpoints.

- `GET /health`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`
- `GET /api/v1/users/me`
- `PUT /api/v1/users/me`
- `GET /api/v1/words/suggest`
- `GET /api/v1/words/{word_id}`
- `POST /api/v1/words/custom`
- `POST /api/v1/words/{word_id}/save`
- `GET /api/v1/users/me/words`
- `GET /api/v1/users/me/suggestions`
- `GET /api/v1/reviews/due`
- `POST /api/v1/reviews/{word_id}/answer`

Review answer values include `again`, `hard`, `good`, and `easy`.

---

## Running tests

### Backend tests

From `backend/`:

```bash
pytest
```

### Android tests

Run unit and instrumentation tests from Android Studio or via Gradle commands.

---

## Notes

- The backend defaults to SQLite for local development.
- Use PostgreSQL in production by updating `DATABASE_URL`.
- Keep `backend/.env` secret values out of version control.

---

## Author

Hamidreza Hosseini

---

## License

This repository does not include a license file. Add one if you want to share or publish the project.
