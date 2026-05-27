from datetime import datetime

from pydantic import BaseModel, Field


class WordResponse(BaseModel):
    id: int
    word: str
    lemma: str
    partOfSpeech: str = Field(validation_alias="part_of_speech")
    cefrLevel: str = Field(validation_alias="cefr_level")
    definition: str
    exampleSentence: str = Field(validation_alias="example_sentence")
    collocations: list[str]
    synonyms: list[str]
    source: str

    model_config = {"from_attributes": True, "populate_by_name": True}


class CustomWordRequest(BaseModel):
    word: str = Field(min_length=1, max_length=100)


class UserWordListItem(BaseModel):
    id: int
    word: WordResponse
    saved: bool
    added_by_user: bool
    status: str
    created_at: datetime


class SuggestionListItem(BaseModel):
    id: int
    word: WordResponse
    suggested_at: datetime
