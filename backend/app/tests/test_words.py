def test_suggest_word(auth_headers, client):
    profile = client.put(
        "/api/v1/users/me",
        headers=auth_headers,
        json={"cefr_level": "A1"},
    )
    assert profile.status_code == 200

    suggest = client.get("/api/v1/words/suggest", headers=auth_headers)
    assert suggest.status_code == 200
    data = suggest.json()
    assert data["cefrLevel"] == "A1"
    assert "word" in data
    assert "definition" in data

    suggestions = client.get("/api/v1/users/me/suggestions", headers=auth_headers)
    assert suggestions.status_code == 200
    assert len(suggestions.json()) >= 1


def test_custom_word_and_save(auth_headers, client):
    custom = client.post(
        "/api/v1/words/custom",
        headers=auth_headers,
        json={"word": "knowledge"},
    )
    assert custom.status_code == 200
    assert custom.json()["word"] == "knowledge"

    words = client.get("/api/v1/users/me/words", headers=auth_headers)
    assert words.status_code == 200
    assert any(w["word"]["word"] == "knowledge" for w in words.json())

    suggest = client.get("/api/v1/words/suggest", headers=auth_headers)
    word_id = suggest.json()["id"]
    saved = client.post(f"/api/v1/words/{word_id}/save", headers=auth_headers)
    assert saved.status_code == 200
