"""Seed vocabulary database with CEFR-level words."""

import sys
from pathlib import Path

# Allow running as: python -m app.seed.seed_words
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))

from sqlalchemy.orm import Session

from app.core.database import SessionLocal
from app.core.utils import serialize_json_list
from app.models.word import Word
from app.repositories.word_repository import WordRepository

SEED_WORDS: list[dict] = [
    # A1
    {
        "word": "hello",
        "lemma": "hello",
        "part_of_speech": "interjection",
        "cefr_level": "A1",
        "definition": "Used as a greeting or to begin a phone conversation.",
        "example_sentence": "Hello, how are you today?",
        "collocations": ["say hello", "hello there"],
        "synonyms": ["hi", "hey"],
        "source": "seed",
    },
    {
        "word": "book",
        "lemma": "book",
        "part_of_speech": "noun",
        "cefr_level": "A1",
        "definition": "A set of printed pages bound together for reading.",
        "example_sentence": "I read a book every night.",
        "collocations": ["read a book", "open a book"],
        "synonyms": ["volume", "text"],
        "source": "seed",
    },
    {
        "word": "water",
        "lemma": "water",
        "part_of_speech": "noun",
        "cefr_level": "A1",
        "definition": "A clear liquid that people and animals drink.",
        "example_sentence": "Please drink more water.",
        "collocations": ["drink water", "cold water"],
        "synonyms": ["H2O"],
        "source": "seed",
    },
    {
        "word": "happy",
        "lemma": "happy",
        "part_of_speech": "adjective",
        "cefr_level": "A1",
        "definition": "Feeling or showing pleasure or contentment.",
        "example_sentence": "She looks very happy today.",
        "collocations": ["feel happy", "happy family"],
        "synonyms": ["glad", "joyful"],
        "source": "seed",
    },
    {
        "word": "go",
        "lemma": "go",
        "part_of_speech": "verb",
        "cefr_level": "A1",
        "definition": "To move or travel from one place to another.",
        "example_sentence": "We go to school by bus.",
        "collocations": ["go home", "go shopping"],
        "synonyms": ["travel", "move"],
        "source": "seed",
    },
    # A2
    {
        "word": "improve",
        "lemma": "improve",
        "part_of_speech": "verb",
        "cefr_level": "A2",
        "definition": "To make something better or to become better.",
        "example_sentence": "I want to improve my English.",
        "collocations": ["improve skills", "improve performance"],
        "synonyms": ["enhance", "develop"],
        "source": "seed",
    },
    {
        "word": "borrow",
        "lemma": "borrow",
        "part_of_speech": "verb",
        "cefr_level": "A2",
        "definition": "To take and use something belonging to someone else temporarily.",
        "example_sentence": "Can I borrow your pen?",
        "collocations": ["borrow money", "borrow a book"],
        "synonyms": ["take on loan"],
        "source": "seed",
    },
    {
        "word": "weather",
        "lemma": "weather",
        "part_of_speech": "noun",
        "cefr_level": "A2",
        "definition": "The condition of the atmosphere at a particular place and time.",
        "example_sentence": "The weather is sunny today.",
        "collocations": ["bad weather", "weather forecast"],
        "synonyms": ["climate conditions"],
        "source": "seed",
    },
    {
        "word": "choose",
        "lemma": "choose",
        "part_of_speech": "verb",
        "cefr_level": "A2",
        "definition": "To select from a number of possibilities.",
        "example_sentence": "You can choose any topic you like.",
        "collocations": ["choose wisely", "choose between"],
        "synonyms": ["select", "pick"],
        "source": "seed",
    },
    {
        "word": "expensive",
        "lemma": "expensive",
        "part_of_speech": "adjective",
        "cefr_level": "A2",
        "definition": "Costing a lot of money.",
        "example_sentence": "This restaurant is too expensive.",
        "collocations": ["very expensive", "expensive gift"],
        "synonyms": ["costly", "pricey"],
        "source": "seed",
    },
    # B1
    {
        "word": "achieve",
        "lemma": "achieve",
        "part_of_speech": "verb",
        "cefr_level": "B1",
        "definition": "To successfully reach a desired result or goal.",
        "example_sentence": "She worked hard to achieve her goals.",
        "collocations": ["achieve success", "achieve a goal"],
        "synonyms": ["accomplish", "attain"],
        "source": "seed",
    },
    {
        "word": "environment",
        "lemma": "environment",
        "part_of_speech": "noun",
        "cefr_level": "B1",
        "definition": "The natural world or the conditions in which people live and work.",
        "example_sentence": "We must protect the environment.",
        "collocations": ["protect the environment", "work environment"],
        "synonyms": ["surroundings", "habitat"],
        "source": "seed",
    },
    {
        "word": "recommend",
        "lemma": "recommend",
        "part_of_speech": "verb",
        "cefr_level": "B1",
        "definition": "To advise someone that something is good or suitable.",
        "example_sentence": "I recommend this app for vocabulary practice.",
        "collocations": ["highly recommend", "recommend a book"],
        "synonyms": ["suggest", "endorse"],
        "source": "seed",
    },
    {
        "word": "confident",
        "lemma": "confident",
        "part_of_speech": "adjective",
        "cefr_level": "B1",
        "definition": "Feeling sure about your own abilities or qualities.",
        "example_sentence": "He feels confident speaking English now.",
        "collocations": ["feel confident", "confident speaker"],
        "synonyms": ["self-assured", "sure"],
        "source": "seed",
    },
    {
        "word": "deadline",
        "lemma": "deadline",
        "part_of_speech": "noun",
        "cefr_level": "B1",
        "definition": "The latest time or date by which something must be completed.",
        "example_sentence": "The project deadline is next Friday.",
        "collocations": ["meet a deadline", "miss a deadline"],
        "synonyms": ["due date", "time limit"],
        "source": "seed",
    },
    # B2
    {
        "word": "comprehensive",
        "lemma": "comprehensive",
        "part_of_speech": "adjective",
        "cefr_level": "B2",
        "definition": "Including or dealing with all or nearly all elements of something.",
        "example_sentence": "The report provides a comprehensive overview.",
        "collocations": ["comprehensive study", "comprehensive guide"],
        "synonyms": ["thorough", "complete"],
        "source": "seed",
    },
    {
        "word": "negotiate",
        "lemma": "negotiate",
        "part_of_speech": "verb",
        "cefr_level": "B2",
        "definition": "To discuss something formally to reach an agreement.",
        "example_sentence": "They negotiated a better contract.",
        "collocations": ["negotiate a deal", "negotiate terms"],
        "synonyms": ["bargain", "discuss"],
        "source": "seed",
    },
    {
        "word": "hypothesis",
        "lemma": "hypothesis",
        "part_of_speech": "noun",
        "cefr_level": "B2",
        "definition": "An idea or explanation that you test through study and experiments.",
        "example_sentence": "The scientist tested her hypothesis.",
        "collocations": ["test a hypothesis", "form a hypothesis"],
        "synonyms": ["theory", "assumption"],
        "source": "seed",
    },
    {
        "word": "inevitable",
        "lemma": "inevitable",
        "part_of_speech": "adjective",
        "cefr_level": "B2",
        "definition": "Certain to happen and impossible to avoid.",
        "example_sentence": "Change is inevitable in any industry.",
        "collocations": ["seem inevitable", "inevitable outcome"],
        "synonyms": ["unavoidable", "certain"],
        "source": "seed",
    },
    {
        "word": "sustain",
        "lemma": "sustain",
        "part_of_speech": "verb",
        "cefr_level": "B2",
        "definition": "To keep something going or to support over time.",
        "example_sentence": "It is hard to sustain motivation without goals.",
        "collocations": ["sustain growth", "sustain effort"],
        "synonyms": ["maintain", "support"],
        "source": "seed",
    },
    # C1
    {
        "word": "ambivalent",
        "lemma": "ambivalent",
        "part_of_speech": "adjective",
        "cefr_level": "C1",
        "definition": "Having mixed feelings or contradictory ideas about something.",
        "example_sentence": "She felt ambivalent about accepting the offer.",
        "collocations": ["ambivalent attitude", "remain ambivalent"],
        "synonyms": ["uncertain", "conflicted"],
        "source": "seed",
    },
    {
        "word": "paradigm",
        "lemma": "paradigm",
        "part_of_speech": "noun",
        "cefr_level": "C1",
        "definition": "A typical example or pattern of something; a worldview underlying theories.",
        "example_sentence": "The discovery shifted the scientific paradigm.",
        "collocations": ["shift a paradigm", "dominant paradigm"],
        "synonyms": ["model", "framework"],
        "source": "seed",
    },
    {
        "word": "scrutinize",
        "lemma": "scrutinize",
        "part_of_speech": "verb",
        "cefr_level": "C1",
        "definition": "To examine something very carefully and in detail.",
        "example_sentence": "Auditors scrutinize financial records.",
        "collocations": ["scrutinize evidence", "closely scrutinize"],
        "synonyms": ["inspect", "examine"],
        "source": "seed",
    },
    {
        "word": "ubiquitous",
        "lemma": "ubiquitous",
        "part_of_speech": "adjective",
        "cefr_level": "C1",
        "definition": "Present, appearing, or found everywhere.",
        "example_sentence": "Smartphones have become ubiquitous in modern life.",
        "collocations": ["ubiquitous presence", "seem ubiquitous"],
        "synonyms": ["omnipresent", "everywhere"],
        "source": "seed",
    },
    {
        "word": "mitigate",
        "lemma": "mitigate",
        "part_of_speech": "verb",
        "cefr_level": "C1",
        "definition": "To make something less severe, serious, or painful.",
        "example_sentence": "Policies were introduced to mitigate climate risks.",
        "collocations": ["mitigate risk", "mitigate damage"],
        "synonyms": ["alleviate", "reduce"],
        "source": "seed",
    },
    # C2
    {
        "word": "ephemeral",
        "lemma": "ephemeral",
        "part_of_speech": "adjective",
        "cefr_level": "C2",
        "definition": "Lasting for a very short time.",
        "example_sentence": "Fame can be ephemeral in the digital age.",
        "collocations": ["ephemeral nature", "ephemeral moment"],
        "synonyms": ["fleeting", "transient"],
        "source": "seed",
    },
    {
        "word": "obfuscate",
        "lemma": "obfuscate",
        "part_of_speech": "verb",
        "cefr_level": "C2",
        "definition": "To make something unclear or difficult to understand.",
        "example_sentence": "The report seemed designed to obfuscate the truth.",
        "collocations": ["obfuscate the issue", "deliberately obfuscate"],
        "synonyms": ["confuse", "cloud"],
        "source": "seed",
    },
    {
        "word": "quintessential",
        "lemma": "quintessential",
        "part_of_speech": "adjective",
        "cefr_level": "C2",
        "definition": "Representing the most perfect or typical example of a quality or class.",
        "example_sentence": "She is the quintessential professional.",
        "collocations": ["quintessential example", "quintessential feature"],
        "synonyms": ["archetypal", "definitive"],
        "source": "seed",
    },
    {
        "word": "recalcitrant",
        "lemma": "recalcitrant",
        "part_of_speech": "adjective",
        "cefr_level": "C2",
        "definition": "Having an obstinately uncooperative attitude toward authority.",
        "example_sentence": "The recalcitrant student refused to follow instructions.",
        "collocations": ["recalcitrant behavior", "remain recalcitrant"],
        "synonyms": ["defiant", "uncooperative"],
        "source": "seed",
    },
    {
        "word": "vicarious",
        "lemma": "vicarious",
        "part_of_speech": "adjective",
        "cefr_level": "C2",
        "definition": "Experienced through the actions or feelings of another person.",
        "example_sentence": "She felt vicarious pride watching her team win.",
        "collocations": ["vicarious pleasure", "vicarious experience"],
        "synonyms": ["indirect", "second-hand"],
        "source": "seed",
    },
]


def seed(db: Session | None = None) -> None:
    own_session = db is None
    if db is None:
        db = SessionLocal()

    repo = WordRepository(db)
    created = 0
    skipped = 0

    try:
        for entry in SEED_WORDS:
            if repo.get_by_word_text(entry["word"]):
                skipped += 1
                continue

            word = Word(
                word=entry["word"],
                lemma=entry["lemma"],
                part_of_speech=entry["part_of_speech"],
                cefr_level=entry["cefr_level"],
                definition=entry["definition"],
                example_sentence=entry["example_sentence"],
                collocations=serialize_json_list(entry["collocations"]),
                synonyms=serialize_json_list(entry["synonyms"]),
                source=entry["source"],
            )
            repo.create(word)
            created += 1

        if own_session:
            print(f"Seed complete: {created} created, {skipped} skipped (already exist).")
    finally:
        if own_session:
            db.close()


if __name__ == "__main__":
    seed()
