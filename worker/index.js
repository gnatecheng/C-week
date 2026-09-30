/** Canonical host redirect + static assets (ASSETS binding). */
const CANONICAL_HOST = "etais.dev";

/** Hosts that must 301 to CANONICAL_HOST (dashboard-managed aliases). */
const REDIRECT_HOSTS = new Set([
  "app.etais.dev",
  "apps.etais.dev",
  "www.etais.dev",
  "etai.chengyitang.workers.dev",
]);

export default {
  /**
   * @param {Request} request
   * @param {{ ASSETS: { fetch: (req: Request) => Promise<Response> } }} env
   */
  async fetch(request, env) {
    const url = new URL(request.url);
    const host = url.hostname.toLowerCase();

    if (REDIRECT_HOSTS.has(host)) {
      url.protocol = "https:";
      url.hostname = CANONICAL_HOST;
      url.port = "";
      return new Response(null, {
        status: 301,
        headers: {
          Location: url.toString(),
        },
      });
    }

    return env.ASSETS.fetch(request);
  },
};
