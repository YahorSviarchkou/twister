/*
 * Twister docs viewer.
 * Reads manifest.json, lists documents and renders them by type:
 *   md      - Markdown (fenced ```mermaid blocks are rendered as diagrams)
 *   html    - standalone HTML artifact in a sandboxed iframe (keeps its own interactivity)
 *   mermaid - a .mmd diagram source
 *   board   - a whiteboard snapshot (board.json), rendered by board.js
 * Routing uses the URL hash: #<doc-id>.
 */
(function () {
  "use strict";

  const state = { manifest: null, docs: [], byId: new Map(), byPath: new Map(), texts: new Map(), current: null };
  const $ = (sel) => document.querySelector(sel);

  // ---------- theme ----------
  function storedTheme() {
    try { return localStorage.getItem("tw-docs-theme"); } catch (e) { return null; }
  }
  function applyTheme(theme) {
    if (theme) document.documentElement.setAttribute("data-theme", theme);
    else document.documentElement.removeAttribute("data-theme");
  }
  function isDark() {
    const t = document.documentElement.getAttribute("data-theme");
    if (t) return t === "dark";
    return window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
  }
  applyTheme(storedTheme());
  $("#theme-toggle").addEventListener("click", () => {
    const next = isDark() ? "light" : "dark";
    applyTheme(next);
    try { localStorage.setItem("tw-docs-theme", next); } catch (e) { /* storage unavailable */ }
    if (state.current) show(state.current.id, { keepScroll: true });
  });

  // ---------- helpers ----------
  function esc(s) {
    return String(s).replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
  }
  function resolvePath(base, rel) {
    // base: path of the current document, rel: link inside it. Returns a normalized docs-relative path.
    const parts = base.split("/").slice(0, -1);
    for (const p of rel.split("/")) {
      if (p === "" || p === ".") continue;
      if (p === "..") parts.pop(); else parts.push(p);
    }
    return parts.join("/");
  }
  async function fetchText(path) {
    const res = await fetch(path, { cache: "no-cache" });
    if (!res.ok) throw new Error(`Cannot load ${path} (${res.status})`);
    return res.text();
  }
  function setBody(html) {
    const body = $("#doc-body");
    body.innerHTML = html;
    return body;
  }
  function showError(err) {
    setBody(`<div class="error"><b>Could not open this document.</b><br>${esc(err.message || err)}<br><br>
      Open the viewer through a local web server (see README), not by double-clicking the file.</div>`);
  }

  // ---------- mermaid ----------
  let mermaidSeq = 0;
  function initMermaid() {
    if (!window.mermaid) return;
    window.mermaid.initialize({
      startOnLoad: false,
      securityLevel: "strict",
      theme: isDark() ? "dark" : "default",
      flowchart: { htmlLabels: true, curve: "basis" },
      sequence: { mirrorActors: false, showSequenceNumbers: true },
    });
  }
  async function renderMermaid(code, target) {
    initMermaid();
    const id = "mmd-" + (++mermaidSeq);
    const { svg } = await window.mermaid.render(id, code);
    target.innerHTML = svg;
  }

  // ---------- sidebar ----------
  function renderList(filter) {
    const q = (filter || "").trim().toLowerCase();
    const nav = $("#doc-list");
    const parts = [];
    for (const section of state.manifest.sections) {
      const docs = state.docs.filter((d) => d.section === section.id && matches(d, q));
      if (!docs.length) continue;
      parts.push(`<div class="group-title">${esc(section.title)}</div>`);
      for (const d of docs) {
        const active = state.current && state.current.id === d.id ? " active" : "";
        const meta = [d.typeLabel, d.status, d.date, d.lang && d.lang !== "en" ? d.lang.toUpperCase() : ""].filter(Boolean).join(" · ");
        parts.push(`<a class="doc-link${active}" href="#${esc(d.id)}"><span class="t">${esc(d.title)}</span><span class="m">${esc(meta)}</span></a>`);
      }
    }
    nav.innerHTML = parts.join("") || `<div class="empty">Nothing matches “${esc(q)}”.</div>`;
  }
  function matches(d, q) {
    if (!q) return true;
    const hay = [d.title, d.description, (d.tags || []).join(" "), state.texts.get(d.id) || ""].join(" ").toLowerCase();
    return q.split(/\s+/).every((w) => hay.includes(w));
  }
  const TYPE_LABELS = { md: "Note", html: "Page", mermaid: "Diagram", board: "Whiteboard" };

  // ---------- document header ----------
  function renderHead(d) {
    $("#doc-head").hidden = false;
    $("#doc-title").textContent = d.title;
    const chips = [];
    chips.push(`<span class="chip">${esc(d.typeLabel)}</span>`);
    if (d.date) chips.push(`<span class="chip">${esc(d.date)}</span>`);
    if (d.status) chips.push(`<span class="chip">${esc(d.status)}</span>`);
    if (d.lang && d.lang !== "en") chips.push(`<span class="chip lang" title="Original language of this artifact">${esc(d.lang.toUpperCase())} original</span>`);
    for (const t of d.tags || []) chips.push(`<span class="chip">#${esc(t)}</span>`);
    $("#doc-meta").innerHTML = chips.join("");
    const actions = [];
    if (d.source) actions.push(`<a class="btn" href="${esc(d.source)}" target="_blank" rel="noopener">Live original</a>`);
    actions.push(`<a class="btn" href="${esc(d.path)}" target="_blank" rel="noopener">Open file</a>`);
    $("#doc-actions").innerHTML = actions.join("");
  }

  // ---------- renderers ----------
  async function renderMarkdown(d) {
    const text = await fetchText(d.path);
    state.texts.set(d.id, text);
    const html = window.marked.parse(text, { gfm: true });
    const body = setBody(`<article class="md">${html}</article>`);
    // mermaid code blocks
    const blocks = body.querySelectorAll("pre > code.language-mermaid");
    for (const code of blocks) {
      const holder = document.createElement("div");
      holder.className = "mermaid";
      code.parentElement.replaceWith(holder);
      try { await renderMermaid(code.textContent, holder); }
      catch (e) { holder.innerHTML = `<div class="error">Diagram error: ${esc(e.message || e)}</div>`; }
    }
    // links: internal docs open in the viewer, external links in a new tab
    for (const a of body.querySelectorAll("a[href]")) {
      const href = a.getAttribute("href");
      if (/^(https?:|mailto:)/.test(href)) { a.target = "_blank"; a.rel = "noopener"; continue; }
      if (href.startsWith("#")) continue;
      const [p, frag] = href.split("#");
      const target = state.byPath.get(resolvePath(d.path, p));
      if (target) a.setAttribute("href", "#" + target.id + (frag ? "" : ""));
      else a.setAttribute("href", resolvePath(d.path, p) + (frag ? "#" + frag : ""));
    }
  }

  async function renderHtml(d) {
    setBody(`<iframe class="frame" title="${esc(d.title)}" src="${esc(d.path)}"
      sandbox="allow-scripts allow-popups allow-popups-to-escape-sandbox"></iframe>`);
  }

  async function renderMermaidDoc(d) {
    const code = await fetchText(d.path);
    state.texts.set(d.id, code);
    const body = setBody(`<div class="diagram-wrap">
      <div class="diagram-tools">
        <button class="btn" data-z="out" type="button">−</button>
        <button class="btn" data-z="in" type="button">+</button>
        <button class="btn" data-z="fit" type="button">100%</button>
        <button class="btn" data-z="src" type="button">Source</button>
        <span class="hint">${esc(d.description || "")}</span>
      </div>
      <div class="diagram"></div>
      <div class="diagram-src"><pre><code>${esc(code)}</code></pre></div>
    </div>`);
    const holder = body.querySelector(".diagram");
    try { await renderMermaid(code, holder); }
    catch (e) { holder.innerHTML = `<div class="error">Diagram error: ${esc(e.message || e)}</div>`; return; }
    let zoom = 1;
    const svg = holder.querySelector("svg");
    const apply = () => { if (svg) { svg.style.transform = `scale(${zoom})`; svg.style.maxWidth = zoom === 1 ? "100%" : "none"; } };
    body.querySelector(".diagram-tools").addEventListener("click", (ev) => {
      const z = ev.target.getAttribute("data-z");
      if (z === "in") zoom = Math.min(4, zoom * 1.25);
      if (z === "out") zoom = Math.max(0.3, zoom / 1.25);
      if (z === "fit") zoom = 1;
      if (z === "src") body.querySelector(".diagram-src").classList.toggle("open");
      apply();
    });
  }

  async function renderBoard(d) {
    const data = JSON.parse(await fetchText(d.path));
    const body = setBody("");
    window.TwBoard.render(body, data);
  }

  const RENDERERS = { md: renderMarkdown, html: renderHtml, mermaid: renderMermaidDoc, board: renderBoard };

  // ---------- navigation ----------
  async function show(id, opts) {
    const d = state.byId.get(id) || state.byId.get(state.manifest.home);
    if (!d) return;
    state.current = d;
    document.title = `${d.title} · Twister docs`;
    renderHead(d);
    renderList($("#search").value);
    const body = $("#doc-body");
    if (!(opts && opts.keepScroll)) body.scrollTop = 0;
    try { await RENDERERS[d.type](d); }
    catch (e) { showError(e); }
  }

  function route() {
    const id = decodeURIComponent(location.hash.replace(/^#/, ""));
    show(id || state.manifest.home);
  }

  async function boot() {
    try {
      state.manifest = JSON.parse(await fetchText("manifest.json"));
    } catch (e) {
      showError(e);
      return;
    }
    state.docs = state.manifest.docs.map((d) => ({ ...d, typeLabel: d.section === "adr" ? "ADR" : TYPE_LABELS[d.type] || d.type }));
    for (const d of state.docs) { state.byId.set(d.id, d); state.byPath.set(d.path, d); }
    $("#search").addEventListener("input", (e) => renderList(e.target.value));
    window.addEventListener("hashchange", route);
    route();
    // Preload text documents in the background so search also matches their content.
    for (const d of state.docs) {
      if (d.type === "md" || d.type === "mermaid") fetchText(d.path).then((t) => state.texts.set(d.id, t)).catch(() => {});
    }
  }

  boot();
})();
