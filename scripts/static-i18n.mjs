/** Apply translation strings into static HTML (homepage /en/ build). */

export async function loadTranslations(readFile, root) {
  const path = await import("node:path");
  let code = await readFile(path.join(root, "site/js/translations.js"), "utf8");
  code = code.replace("window.ETAI_TRANSLATIONS", "var ETAI_TRANSLATIONS");
  const fn = new Function(code + "\nreturn ETAI_TRANSLATIONS;");
  return fn();
}

export function t(dict, key) {
  if (dict && Object.prototype.hasOwnProperty.call(dict, key)) return dict[key];
  return "";
}

export function escapeHtmlText(s) {
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}

export function applyStaticI18n(html, dict, { htmlKeys = null } = {}) {
  const htmlKeySet =
    htmlKeys ||
    new Set([
      "faq.install.body",
      "faq.installTips.body",
      "faq.vscode.body",
      "qjz.hero.lead",
      "class.hero.lead",
      "cweek.hero.lead",
    ]);

  html = html.replace(
    /<(\w+)([^>]*\sdata-i18n-html="([^"]+)"[^>]*)>([\s\S]*?)<\/\1>/g,
    (match, tag, attrs, key, _inner) => {
      const val = t(dict, key);
      if (!val) return match;
      return `<${tag}${attrs}>${val}</${tag}>`;
    }
  );

  html = html.replace(
    /<(\w+)([^>]*\sdata-i18n="([^"]+)"[^>]*)>([^<]*)<\/\1>/g,
    (match, tag, attrs, key, _inner) => {
      const val = t(dict, key);
      if (!val) return match;
      return `<${tag}${attrs}>${escapeHtmlText(val)}</${tag}>`;
    }
  );

  html = html.replace(
    /<(\w+)([^>]*\sdata-i18n="([^"]+)"[^>]*)\/>/g,
    (match, tag, attrs, key) => {
      const val = t(dict, key);
      if (!val) return match;
      return `<${tag}${attrs}>${escapeHtmlText(val)}</${tag}>`;
    }
  );

  html = html.replace(
    /(<[^>]*\sdata-i18n-content="([^"]+)"[^>]*\scontent=")([^"]*)(")/g,
    (match, pre, key, _old, post) => {
      const val = t(dict, key);
      if (!val) return match;
      return pre + val.replace(/"/g, "&quot;") + post;
    }
  );

  html = html.replace(
    /(<[^>]*\sdata-i18n-alt="([^"]+)"[^>]*\salt=")([^"]*)(")/g,
    (match, pre, key, _old, post) => {
      const val = t(dict, key);
      if (!val) return match;
      return pre + val.replace(/"/g, "&quot;") + post;
    }
  );

  html = html.replace(
    /(<[^>]*\sdata-i18n-aria="([^"]+)"[^>]*\saria-label=")([^"]*)(")/g,
    (match, pre, key, _old, post) => {
      const val = t(dict, key);
      if (!val) return match;
      return pre + val.replace(/"/g, "&quot;") + post;
    }
  );

  return html;
}
