/** Shared JSON-LD @graph for zh (/) and en (/en/) homepages. */

export function loadTranslationsFromFile(readFileSync, filePath) {
  let code = readFileSync(filePath, "utf8");
  code = code.replace("window.ETAI_TRANSLATIONS", "var ETAI_TRANSLATIONS");
  const fn = new Function(code + "\nreturn ETAI_TRANSLATIONS;");
  return fn();
}

export function stripHtml(html) {
  return (html || "")
    .replace(/<[^>]+>/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

export function buildJsonLd(lang, t) {
  const base = lang === "en" ? "https://etais.dev/en/" : "https://etais.dev/";
  const siteName = t["meta.siteName"];
  const altSite = lang === "en" ? "Etai 应用集" : "Etai Apps";

  const apps =
    lang === "en"
      ? [
          {
            name: "C Week",
            alternateName: "C一周通",
            url: base + "#cweek",
            category: "EducationalApplication",
            description:
              "Learn C in seven days from Hello World to Dijkstra with bundled VS Code screen recordings and offline labs.",
            downloadUrl: "https://github.com/gnatecheng/c-week/releases/latest",
          },
          {
            name: "Easy Ledger",
            alternateName: "轻记账",
            url: base + "#qingjizhang",
            category: "FinanceApplication",
            description: "Private budgeting that stays on your phone—no sign-up or cloud sync.",
            downloadUrl: "https://github.com/gnatecheng/easy-ledger/releases/latest",
          },
          {
            name: "Group Matters",
            alternateName: "团团记",
            url: base + "#group-matters",
            category: "BusinessApplication",
            description:
              "Small-group attendance, fee collection, cost splits, and checklists—data stays on device.",
            downloadUrl: "https://github.com/gnatecheng/group-matters/releases/latest",
          },
        ]
      : [
          {
            name: "C一周通",
            alternateName: "C Week",
            url: base + "#cweek",
            category: "EducationalApplication",
            description:
              "7 天从 Hello World 学到 Dijkstra，课文含 VS Code 实操录像，离线实验与测验。",
            downloadUrl: "https://github.com/gnatecheng/c-week/releases/latest",
          },
          {
            name: "轻记账",
            alternateName: "Easy Ledger",
            url: base + "#qingjizhang",
            category: "FinanceApplication",
            description: "无需注册的本地记账，数据只存在你的手机里，不用注册账号，也不上传云端。",
            downloadUrl: "https://github.com/gnatecheng/easy-ledger/releases/latest",
          },
          {
            name: "团团记",
            alternateName: "Group Matters",
            url: base + "#group-matters",
            category: "BusinessApplication",
            description: "小团体点名、收费、费用分摊与清单，数据只存本机。",
            downloadUrl: "https://github.com/gnatecheng/group-matters/releases/latest",
          },
        ];

  const graph = [
    {
      "@type": "WebSite",
      "@id": base + "#website",
      url: base,
      name: siteName,
      alternateName: altSite,
      inLanguage: lang === "en" ? "en-US" : "zh-CN",
    },
    ...apps.map((app, i) => ({
      "@type": "SoftwareApplication",
      "@id": base + "#app-" + i,
      name: app.name,
      alternateName: app.alternateName,
      url: app.url,
      operatingSystem: "Android 8.0+",
      applicationCategory: app.category,
      description: app.description,
      downloadUrl: app.downloadUrl,
      offers: {
        "@type": "Offer",
        price: "0",
        priceCurrency: "USD",
      },
    })),
  ];

  return {
    "@context": "https://schema.org",
    "@graph": graph,
  };
}

/** Single-app landing page: SoftwareApplication + FAQPage. */
export function buildAppPageJsonLd(canonicalUrl, t, app, alternateAppName) {
  const isEn = canonicalUrl.includes("/en/");
  const hubUrl = isEn ? "https://etais.dev/en/" : "https://etais.dev/";
  const faqEntity = app.faqKeys.map(([titleKey, bodyKey]) => ({
    "@type": "Question",
    name: t[titleKey],
    acceptedAnswer: {
      "@type": "Answer",
      text: stripHtml(t[bodyKey]),
    },
  }));

  return {
    "@context": "https://schema.org",
    "@graph": [
      {
        "@type": "BreadcrumbList",
        "@id": canonicalUrl + "#breadcrumb",
        itemListElement: [
          {
            "@type": "ListItem",
            position: 1,
            name: t["meta.siteName"],
            item: hubUrl,
          },
          {
            "@type": "ListItem",
            position: 2,
            name: t[app.keys.name],
            item: canonicalUrl,
          },
        ],
      },
      {
        "@type": "SoftwareApplication",
        "@id": canonicalUrl + "#app",
        name: t[app.keys.name],
        alternateName: alternateAppName,
        url: canonicalUrl,
        operatingSystem: "Android 8.0+",
        applicationCategory: app.category,
        description: stripHtml(t[app.keys.tagline]),
        downloadUrl: `https://github.com/${app.github}/releases/latest`,
        offers: {
          "@type": "Offer",
          price: "0",
          priceCurrency: "USD",
        },
      },
      {
        "@type": "FAQPage",
        "@id": canonicalUrl + "#faq",
        mainEntity: faqEntity,
      },
    ],
  };
}
