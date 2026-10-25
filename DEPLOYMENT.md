# Deployment (Contabo VPS)

Every push/merge to `main` runs `.github/workflows/deploy.yml`:

1. **test** – builds and runs the tests against a throwaway Postgres.
2. **build-image** – builds the Docker image and pushes it to `ghcr.io/douglaskhembo/bloombackend` (tags `latest` and the short commit SHA).
3. **deploy** – copies `deploy/docker-compose.yml` to the server over SSH, pulls the new image, restarts the stack and waits for `/actuator/health`.

PRs and pushes to `development` only run CI (`.github/workflows/ci.yml`).

## For frontend developers

| What | URL |
|------|-----|
| API base | `http://<VPS_HOST>:9090/api/v1` |
| Swagger UI | `http://<VPS_HOST>:9090/swagger-ui.html` |
| OpenAPI JSON | `http://<VPS_HOST>:9090/v3/api-docs` |

Your frontend origin must be listed in `CORS_ALLOWED_ORIGINS` on the server.

## One-time server setup

SSH into the VPS as root (or a sudo user) and run:

```bash
# 1. Install Docker + compose plugin
curl -fsSL https://get.docker.com | sh

# 2. Create a deploy user that can run docker
adduser --disabled-password --gecos "" deploy
usermod -aG docker deploy

# 3. Create the app directory and its .env
mkdir -p /opt/bloom-backend
chown deploy:deploy /opt/bloom-backend
sudo -u deploy nano /opt/bloom-backend/.env   # paste deploy/.env.example and fill in real values

# 4. Open the API port (if ufw is enabled)
ufw allow 9090/tcp
```

Create an SSH key pair for GitHub Actions (on your own machine):

```bash
ssh-keygen -t ed25519 -f bloom_deploy -C "github-actions" -N ""
ssh-copy-id -i bloom_deploy.pub deploy@<VPS_HOST>
```

## GitHub configuration

Repo → **Settings → Secrets and variables → Actions**:

| Secret | Value |
|--------|-------|
| `VPS_HOST` | Contabo server IP or hostname |
| `VPS_USER` | `deploy` |
| `VPS_SSH_KEY` | contents of the private key `bloom_deploy` |
| `VPS_PORT` | *(optional)* SSH port if not 22 |

Optional **variable** `DEPLOY_PATH` if you don't use `/opt/bloom-backend`.

The deploy job uses the `production` environment – it is created automatically on first run; you can add required reviewers to it under **Settings → Environments**.

## Day-to-day

- Deploy: merge `development` → `main` (or run the workflow manually from the Actions tab).
- Logs: `cd /opt/bloom-backend && docker compose logs -f backend`
- Rollback: set `BACKEND_IMAGE=ghcr.io/douglaskhembo/bloombackend:<old-sha>` in `.env`, then `docker compose up -d`.
- Database data lives in the `pgdata` Docker volume and survives redeploys.

## Recommended next step

Put a domain + HTTPS in front of the API (e.g. Caddy or Nginx + Let's Encrypt) so the frontend can call `https://api.yourdomain.com` instead of a raw IP and port.
