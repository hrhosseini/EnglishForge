import json

from app.models.user import User
from app.models.word import Word
from app.schemas.word import WordResponse


def parse_json_list(value: str) -> list[str]:
    try:
        parsed = json.loads(value)
        if isinstance(parsed, list):
            return [str(item) for item in parsed]
    except (json.JSONDecodeError, TypeError):
        pass
    return [item.strip() for item in value.split(",") if item.strip()]


def serialize_json_list(items: list[str]) -> str:
    return json.dumps(items)


def parse_user_interests(user: User) -> list[str]:
    if not user.interests:
        return []
    return parse_json_list(user.interests)


def set_user_interests(user: User, interests: list[str]) -> None:
    user.interests = serialize_json_list(interests)


def word_to_response(word: Word) -> WordResponse:
    return WordResponse(
        id=word.id,
        word=word.word,
        lemma=word.lemma,
        part_of_speech=word.part_of_speech,
        cefr_level=word.cefr_level,
        definition=word.definition,
        example_sentence=word.example_sentence,
        collocations=parse_json_list(word.collocations),
        synonyms=parse_json_list(word.synonyms),
        source=word.source,
    )
