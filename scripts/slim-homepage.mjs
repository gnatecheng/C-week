/**
 * Slim homepage app sections: teasers + prominent learn-more; drop long feature blocks.
 * Run: node scripts/slim-homepage.mjs
 */
import { readFile, writeFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { APP_PAGES } from "./site-app-config.mjs";
import { loadTranslations } from "./static-i18n.mjs";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const indexPath = path.join(root, "site/index.html");

function learnMoreBlock(slug, keys, zh) {
  const items = keys.map((k) => `                <li data-i18n="${k}">${zh[k]}</li>`).join("\n");
  return `              <ul class="app-home-teasers">
${items}
              </ul>
              <p class="app-learn-more">
                <a class="btn btn-secondary btn-learn-more" href="/${slug}/#features" data-i18n="appPage.learnMore">了解更多</a>
              </p>`;
}

async function main() {
  const T = await loadTranslations(readFile, root);
  let html = await readFile(indexPath, "utf8");

  const qjz = APP_PAGES.find((a) => a.slug === "easy-ledger");
  const gm = APP_PAGES.find((a) => a.slug === "group-matters");
  const cw = APP_PAGES.find((a) => a.slug === "c-week");

  html = html.replace(
    /<p class="app-learn-more"><a href="\/easy-ledger\/" data-i18n="appPage\.learnMore">[\s\S]*?<\/a><\/p>/,
    learnMoreBlock("easy-ledger", qjz.homeTeaserKeys, T.zh)
  );
  html = html.replace(
    /<p class="app-learn-more"><a href="\/group-matters\/" data-i18n="appPage\.learnMore">[\s\S]*?<\/a><\/p>/,
    learnMoreBlock("group-matters", gm.homeTeaserKeys, T.zh)
  );
  html = html.replace(
    /<p class="app-learn-more"><a href="\/c-week\/" data-i18n="appPage\.learnMore">[\s\S]*?<\/a><\/p>/,
    learnMoreBlock("c-week", cw.homeTeaserKeys, T.zh)
  );

  html = html.replace(
    /\n\s*<h3 class="project-subhead" data-i18n="qjz\.featuresHeading">[\s\S]*?<p class="project-foot" data-i18n="qjz\.foot">[\s\S]*?<\/p>\n/,
    "\n"
  );
  html = html.replace(
    /\n\s*<h3 class="project-subhead" data-i18n="class\.featuresHeading">[\s\S]*?<p class="project-foot" data-i18n="class\.foot">[\s\S]*?<\/p>\n/,
    "\n"
  );
  html = html.replace(
    /\n\s*<section id="features" data-group="cweek">[\s\S]*?<\/section>\n\n\s*<section id="roadmap"[\s\S]*?<\/section>\n/,
    "\n"
  );

  html = html.replace(/\/css\/style\.css\?v=\d+/g, "/css/style.css?v=27");
  html = html.replace(/\/js\/translations\.js\?v=\d+/g, "/js/translations.js?v=35");

  await writeFile(indexPath, html, "utf8");
  console.log("Slimmed site/index.html");
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
