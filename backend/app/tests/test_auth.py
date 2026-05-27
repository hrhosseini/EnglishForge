def test_health(client):
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_register_and_login(client):
    register = client.post(
        "/api/v1/auth/register",
        json={
            "email": "user@example.com",
            "password": "securepass1",
            "display_name": "User",
        },
    )
    assert register.status_code == 200
    assert "access_token" in register.json()

    login = client.post(
        "/api/v1/auth/login",
        json={"email": "user@example.com", "password": "securepass1"},
    )
    assert login.status_code == 200
    token = login.json()["access_token"]

    me = client.get("/api/v1/auth/me", headers={"Authorization": f"Bearer {token}"})
    assert me.status_code == 200
    assert me.json()["email"] == "user@example.com"


def test_register_duplicate_email(client):
    payload = {
        "email": "dup@example.com",
        "password": "password123",
    }
    assert client.post("/api/v1/auth/register", json=payload).status_code == 200
    assert client.post("/api/v1/auth/register", json=payload).status_code == 400
