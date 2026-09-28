/**
 * Legacy hostname redirect: c-week.chengyitang.workers.dev → etai.chengyitang.workers.dev
 * Preserves path and query string. Deploy separately in Cloudflare (see README).
 */
const TARGET_ORIGIN = "https://etai.chengyitang.workers.dev";

export default {
  fetch(request) {
    const incoming = new URL(request.url);
    const target = new URL(incoming.pathname + incoming.search, TARGET_ORIGIN);
    return Response.redirect(target.href, 301);
  },
};
