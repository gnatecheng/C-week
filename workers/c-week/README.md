# c-week redirect Worker

301-redirects all requests from the old Worker hostname (`c-week.chengyitang.workers.dev`) to the current site at `https://etai.chengyitang.workers.dev`, preserving path and query.

This is **not** deployed by the repo’s main Workers Builds integration (that deploys the `etai` static site from the repo root). Deploy this Worker manually once in Cloudflare.

## Deploy (owner)

1. Install dependencies are not required; only Wrangler is needed (`npx wrangler`).
2. From this directory:

   ```bash
   cd workers/c-week
   npx wrangler deploy
   ```

3. In the Cloudflare dashboard, open the **`c-week`** Worker → **Settings** → **Domains & Routes** and attach the legacy workers.dev route (or custom domain) that previously served the old site, e.g. `c-week.chengyitang.workers.dev`.
4. Verify: `curl -I 'https://c-week.chengyitang.workers.dev/foo?bar=1'` should return `301` with `Location: https://etai.chengyitang.workers.dev/foo?bar=1`.
