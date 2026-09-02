# Deploying RateMe for free

The app ships as a single Spring Boot jar that serves both the REST API and the
frontend. For a public demo it runs the **`cloud` profile**, which replaces the
MySQL container with an in-memory **H2** database (MySQL-compatibility mode).

- The 59 seeded restaurants/cafés/pubs are reloaded from
  [`db/h2/schema.sql`](Application/src/main/resources/db/h2/schema.sql) and
  [`db/h2/data.sql`](Application/src/main/resources/db/h2/data.sql) on every start.
- Accounts registered and ratings added on the live site are **wiped on every
  restart / redeploy** (in-memory DB). That is fine for a "click around" demo.
- Local development is unchanged: `docker compose up --build` still uses MySQL
  (compose sets `SPRING_PROFILES_ACTIVE=default`).

The container listens on `$PORT` (falling back to 8080), which is what free hosts
provide.

---

## Option A — Render (recommended, no credit card)

1. Push this repo to GitHub (already at `github.com/SamarthKunwar/Rate-me`).
2. Go to <https://dashboard.render.com> → **New** → **Blueprint**.
3. Pick the `Rate-me` repo. Render reads [`render.yaml`](render.yaml) and proposes
   one free Docker web service named `rateme`. Click **Apply**.
4. First build takes ~5 min (Maven build inside Docker). When it's live you get
   `https://rateme-XXXX.onrender.com`.

No database, no environment variables to set by hand — `render.yaml` already sets
`SPRING_PROFILES_ACTIVE=cloud`.

### Keep it awake (optional but worth it)

Render's free tier sleeps a service after ~15 min with no traffic; the next
visitor then waits ~40 s for the JVM to boot. To avoid a recruiter hitting that:

1. Create a free monitor at <https://uptimerobot.com> (or <https://cron-job.org>).
2. HTTP(s) check on `https://rateme-XXXX.onrender.com/` every **10 minutes**.

One always-on free web service uses ~730 of the 750 free instance-hours/month, so
this stays within the free allowance.

---

## Option B — Koyeb (no credit card)

1. <https://app.koyeb.com> → **Create Web Service** → **GitHub** → `Rate-me`.
2. Builder: **Dockerfile**, Dockerfile location `Application/Dockerfile`,
   work directory `Application`.
3. Environment variable: `SPRING_PROFILES_ACTIVE=cloud`.
4. Instance: **Free**. Port: `8080` (Koyeb injects `$PORT`). Deploy.

---

## Option C — Fly.io (needs a card; less cold-start pain)

```bash
cd Application
fly launch --no-deploy          # generates fly.toml; decline Postgres
fly secrets set SPRING_PROFILES_ACTIVE=cloud
fly deploy
```

In `fly.toml` set `[[services]] internal_port = 8080` and, to skip cold starts,
`min_machines_running = 1` (a single shared-cpu-1x/256–512 MB machine; usually a
few dollars a month, not strictly $0).

---

## Verifying a deployment

```bash
BASE=https://your-app-url
curl -si  $BASE/            | head -1                       # 200, serves index.html
TOK=$(curl -s -XPOST $BASE/auth/register -H 'Content-Type: application/json' \
  -d '{"username":"demo","email":"d@e.com","firstname":"D","lastname":"E","street":"S","streetNr":"1","zip":"66482","city":"ZW","password":"demopass1"}' \
  | python -c 'import sys,json;print(json.load(sys.stdin)["sessionToken"])')
curl -s $BASE/pois -H "Authorization: $TOK" | python -c 'import sys,json;print(len(json.load(sys.stdin)),"pois")'   # 59 pois
```

Swagger UI: `$BASE/swagger-ui.html`.
