# Vocabulary Learning API (FastAPI)

Python backend for an Android English vocabulary-learning app. Users learn words by CEFR level (A1–C2) with JWT auth, word suggestions, custom words, spaced repetition reviews, and profile settings.

## Tech stack

- Python 3.11+
- FastAPI + Uvicorn
- SQLAlchemy 2.x + Alembic
- SQLite (local dev) / PostgreSQL (production)
- JWT authentication
- Pydantic v2

## Project structure

```
backend/
  app/
    main.py
    core/
    models/
    schemas/
    api/v1/
    services/
    repositories/
    seed/
    tests/
  alembic/
  requirements.txt
  .env.example
```

## Quick start

### 1. Create virtual environment

```bash
cd backend
python3.11 -m venv .venv
source .venv/bin/activate   # Linux/macOS
# .venv\Scripts\activate    # Windows
```

### 2. Install dependencies

```bash
pip install --upgrade pip
pip install -r requirements.txt
```

### 3. Configure environment

```bash
cp .env.example .env
```

Edit `.env` if needed. Default uses SQLite file `vocabulary.db`.

### 4. Run database migrations

From the `backend/` directory:

```bash
alembic upgrade head
```

### 5. Seed vocabulary data (30 words, 5 per CEFR level)

```bash
python -m app.seed.seed_words
```

### 6. Run the server

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

API docs: http://127.0.0.1:8000/docs  
Health check: http://127.0.0.1:8000/health

## Android client connection

The server must listen on `0.0.0.0` so devices on your LAN can reach it.

| Client | Base URL |
|--------|----------|
| Android emulator | `http://10.0.2.2:8000` |
| Physical phone (same Wi‑Fi) | `http://YOUR_COMPUTER_LAN_IP:8000` |

Example for a phone when your computer’s LAN IP is `192.168.1.20`:

```
http://192.168.1.20:8000
```

Find your LAN IP:

```bash
# Linux
ip -4 addr show | grep inet

# macOS
ipconfig getifaddr en0
```

Ensure your firewall allows inbound TCP on port `8000`.

### CORS

Set `CORS_ORIGINS` in `.env` (comma-separated). Defaults include localhost and `http://10.0.2.2:8000`. Add your LAN URL if needed, e.g.:

```
CORS_ORIGINS=http://localhost,http://127.0.0.1,http://10.0.2.2:8000,http://192.168.1.20:8000
```

## PostgreSQL (production)

```env
DATABASE_URL=postgresql://user:password@localhost:5432/vocabulary_db
```

Install driver if needed:

```bash
pip install psycopg2-binary
```

Then run migrations and seed as above.

## API endpoints

| Method | Path | Auth |
|--------|------|------|
| GET | `/health` | No |
| POST | `/api/v1/auth/register` | No |
| POST | `/api/v1/auth/login` | No |
| GET | `/api/v1/auth/me` | Yes |
| GET | `/api/v1/users/me` | Yes |
| PUT | `/api/v1/users/me` | Yes |
| GET | `/api/v1/words/suggest` | Yes |
| GET | `/api/v1/words/{word_id}` | Yes |
| POST | `/api/v1/words/custom` | Yes |
| POST | `/api/v1/words/{word_id}/save` | Yes |
| GET | `/api/v1/users/me/words` | Yes |
| GET | `/api/v1/users/me/suggestions` | Yes |
| GET | `/api/v1/reviews/due` | Yes |
| POST | `/api/v1/reviews/{word_id}/answer` | Yes |

Review answers: `again`, `hard`, `good`, `easy`.

## curl examples

Replace `TOKEN` with the `access_token` from register/login.

```bash
# Health
curl http://127.0.0.1:8000/health

# Register
curl -X POST http://127.0.0.1:8000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"learner@example.com","password":"password123","display_name":"Alex"}'

# Login
curl -X POST http://127.0.0.1:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"learner@example.com","password":"password123"}'

# Update profile (CEFR + interests)
curl -X PUT http://127.0.0.1:8000/api/v1/users/me \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"cefr_level":"B1","interests":["work","technology"]}'

# Suggest word
curl http://127.0.0.1:8000/api/v1/words/suggest \
  -H "Authorization: Bearer TOKEN"

# Add custom word
curl -X POST http://127.0.0.1:8000/api/v1/words/custom \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"word":"knowledge"}'

# Save suggested word (replace 1 with word id)
curl -X POST http://127.0.0.1:8000/api/v1/words/1/save \
  -H "Authorization: Bearer TOKEN"

# List saved words
curl http://127.0.0.1:8000/api/v1/users/me/words \
  -H "Authorization: Bearer TOKEN"

# Due reviews
curl http://127.0.0.1:8000/api/v1/reviews/due \
  -H "Authorization: Bearer TOKEN"

# Submit review answer
curl -X POST http://127.0.0.1:8000/api/v1/reviews/1/answer \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"answer":"good"}'
```

## Tests

```bash
cd backend
pytest app/tests -v
```

## Spaced repetition rules

| Answer | Next interval |
|--------|----------------|
| again | 1 day |
| hard | 2 days |
| good | max(current × 2, 3) days |
| easy | max(current × 3, 5) days |

Stored per user/word: `repetitions`, `intervalDays`, `easeFactor`, `dueDate`, `lastReviewedAt`, `status`.

## License

MIT (adjust as needed for your project).
