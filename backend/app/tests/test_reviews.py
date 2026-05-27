def test_review_flow(auth_headers, client):
    custom = client.post(
        "/api/v1/words/custom",
        headers=auth_headers,
        json={"word": "reviewtest"},
    )
    word_id = custom.json()["id"]

    due = client.get("/api/v1/reviews/due", headers=auth_headers)
    assert due.status_code == 200
    assert any(item["word"]["id"] == word_id for item in due.json())

    answer = client.post(
        f"/api/v1/reviews/{word_id}/answer",
        headers=auth_headers,
        json={"answer": "good"},
    )
    assert answer.status_code == 200
    body = answer.json()
    assert body["intervalDays"] >= 3
    assert body["repetitions"] == 1
