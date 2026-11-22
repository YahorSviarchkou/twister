/*
 * Read-only renderer for whiteboard snapshots (board.json).
 * Supported element types: container, shape, text, arrow, stroke, link.
 * Drag to pan, wheel to zoom, "Fit" to see everything, "Jump to" to focus a section.
 */
(function () {
  "use strict";

  const NS = "http://www.w3.org/2000/svg";
  const XH = "http://www.w3.org/1999/xhtml";
  const FILL = {
    white: "var(--b-white)", yellow: "var(--b-yellow)", peach: "var(--b-peach)", pink: "var(--b-pink)",
    green: "var(--b-green)", blue: "var(--b-blue)", purple: "var(--b-purple)",
  };
  const INK = { ink: "var(--ink)", terracotta: "#c4623a", blue: "#3a6fc4", green: "#2f8a55", purple: "#7a55c4" };
  const SIZE = { small: 14, body: 15, large: 28, title: 48, huge: 96 };

  function el(name, attrs, parent) {
    const n = document.createElementNS(NS, name);
    for (const k in attrs) if (attrs[k] !== undefined && attrs[k] !== null) n.setAttribute(k, attrs[k]);
    if (parent) parent.appendChild(n);
    return n;
  }
  function esc(s) {
    return String(s).replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
  }
  function color(c, map, fallback) {
    if (!c) return fallback;
    if (c[0] === "#") return c;
    return map[c] || fallback;
  }

  // Minimal inline markdown used on boards: **bold**, *italic*, `code`, [label](url), "- " lists, line breaks.
  function inline(s) {
    return esc(s)
      .replace(/\*\*(.+?)\*\*/g, "<b>$1</b>")
      .replace(/(^|[^*])\*(?!\s)(.+?)\*(?!\*)/g, "$1<i>$2</i>")
      .replace(/`([^`]+)`/g, "<code>$1</code>")
      .replace(/\[([^\]]+)\]\((https?:[^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>');
  }
  function richText(text) {
    const out = [];
    let list = null;
    for (const line of String(text).split("\n")) {
      const m = /^(-|\d+\.) (.*)$/.exec(line);
      if (m) {
        if (!list) { list = []; }
        list.push(`<li>${inline(m[2])}</li>`);
        continue;
      }
      if (list) { out.push(`<ul>${list.join("")}</ul>`); list = null; }
      out.push(line.trim() === "" ? "<div>&nbsp;</div>" : `<div>${inline(line)}</div>`);
    }
    if (list) out.push(`<ul>${list.join("")}</ul>`);
    return out.join("");
  }

  function foreign(parent, x, y, w, h, html, cls, style) {
    const fo = el("foreignObject", { x, y, width: w, height: h }, parent);
    const div = document.createElementNS(XH, "div");
    div.className = cls;
    if (style) div.setAttribute("style", style);
    div.innerHTML = html;
    fo.appendChild(div);
    return fo;
  }

  function textBox(e) {
    const size = SIZE[e.size] || 16;
    const lines = String(e.text).split("\n");
    const longest = Math.max(...lines.map((l) => l.replace(/\*\*/g, "").length));
    const w = e.wrap || Math.max(40, longest * size * 0.58 + 8);
    const perLine = Math.max(1, Math.floor(w / (size * 0.52)));
    const rows = lines.reduce((n, l) => n + Math.max(1, Math.ceil(l.length / perLine)), 0);
    return { x: e.x, y: e.y, w, h: rows * size * 1.45 + 6 };
  }

  function bounds(e) {
    if (e.type === "text") return textBox(e);
    if (e.type === "link") return { x: e.x, y: e.y, w: 320, h: 56 };
    if (e.type === "container") return { x: e.x, y: e.y - 34, w: e.w, h: e.h + 34 };
    if (e.type === "stroke") {
      const xs = [], ys = [];
      for (let i = 0; i < e.points.length; i += 2) { xs.push(e.x + e.points[i]); ys.push(e.y + e.points[i + 1]); }
      return { x: Math.min(...xs), y: Math.min(...ys), w: Math.max(...xs) - Math.min(...xs), h: Math.max(...ys) - Math.min(...ys) };
    }
    if ("w" in e) return { x: e.x, y: e.y, w: e.w, h: e.h };
    return null;
  }

  // ---------- element painters ----------
  function drawContainer(g, e) {
    el("rect", { x: e.x, y: e.y, width: e.w, height: e.h, rx: 12, fill: "var(--b-container)", stroke: "var(--b-stroke)", "stroke-width": 1.2 }, g);
    foreign(g, e.x, e.y - 34, Math.max(e.w, 400), 30, esc(e.text || ""), "bt free bt-title");
  }

  function shapePath(g, e, fill, stroke, sw) {
    const { x, y, w, h } = e;
    const common = { fill, stroke, "stroke-width": sw };
    switch (e.kind) {
      case "rect": return el("rect", { x, y, width: w, height: h, rx: 2, ...common }, g);
      case "pill": return el("rect", { x, y, width: w, height: h, rx: Math.min(h, w) / 2, ...common }, g);
      case "ellipse": return el("ellipse", { cx: x + w / 2, cy: y + h / 2, rx: w / 2, ry: h / 2, ...common }, g);
      case "database": {
        const ry = Math.min(14, h / 6);
        el("path", { d: `M${x},${y + ry} v${h - 2 * ry} a${w / 2},${ry} 0 0 0 ${w},0 v${-(h - 2 * ry)}`, ...common }, g);
        return el("ellipse", { cx: x + w / 2, cy: y + ry, rx: w / 2, ry, ...common }, g);
      }
      default: return el("rect", { x, y, width: w, height: h, rx: 10, ...common }, g);
    }
  }

  function drawShape(g, e) {
    const hollow = !!e.hollow;
    const fill = hollow ? "none" : color(e.color || "green", FILL, "var(--b-white)");
    const stroke = e.stroke ? color(e.stroke, INK, "var(--b-stroke)") : "var(--b-stroke)";
    shapePath(g, e, fill, stroke, e.stroke ? 2 : 1.1);
    const size = SIZE[e.size] || 15;
    const align = e.textAlign || "center";
    const pad = e.kind === "pill" ? "8px 22px" : e.kind === "database" ? "22px 12px 8px" : "8px 12px";
    foreign(g, e.x, e.y, e.w, e.h, richText(e.text || ""), "bt",
      `font-size:${size}px;text-align:${align};align-items:${align === "left" ? "flex-start" : "center"};padding:${pad}`);
  }

  function drawText(g, e) {
    const b = textBox(e);
    const size = SIZE[e.size] || 16;
    const weight = e.size === "title" || e.size === "huge" ? 700 : 400;
    const ws = e.wrap ? "normal" : "nowrap";
    foreign(g, e.x, e.y, b.w, b.h + 20, richText(e.text || ""), "bt free",
      `font-size:${size}px;font-weight:${weight};white-space:${ws};text-align:${e.textAlign || "left"};color:${color(e.color, INK, "var(--ink)")}`);
  }

  function drawStroke(g, e) {
    const pts = [];
    for (let i = 0; i < e.points.length; i += 2) pts.push(`${e.x + e.points[i]},${e.y + e.points[i + 1]}`);
    el("polyline", { points: pts.join(" "), fill: "none", stroke: color(e.color, INK, "var(--b-lifeline)"), "stroke-width": e.width || 2, "stroke-linecap": "round" }, g);
  }

  function drawLink(g, e) {
    const a = el("a", { href: e.href, target: "_blank", class: "board-link" }, g);
    el("rect", { x: e.x, y: e.y, width: 320, height: 56, rx: 10, fill: "var(--b-white)", stroke: "var(--b-stroke)" }, a);
    foreign(a, e.x, e.y, 320, 56, `<b>${esc(e.title || e.href)}</b><div style="font-size:12px;color:var(--muted)">${esc(e.kind || "link")} · opens the live artifact</div>`,
      "bt", "font-size:14px;align-items:flex-start");
  }

  // ---------- arrows ----------
  function anchor(box, side) {
    switch (side) {
      case "left": return { x: box.x, y: box.y + box.h / 2, dir: "h" };
      case "right": return { x: box.x + box.w, y: box.y + box.h / 2, dir: "h" };
      case "top": return { x: box.x + box.w / 2, y: box.y, dir: "v" };
      default: return { x: box.x + box.w / 2, y: box.y + box.h, dir: "v" };
    }
  }
  function guessSide(box, other) {
    const cx = box.x + box.w / 2, cy = box.y + box.h / 2;
    const dx = other.x - cx, dy = other.y - cy;
    if (Math.abs(dx) / (box.w / 2 || 1) > Math.abs(dy) / (box.h / 2 || 1)) return dx > 0 ? "right" : "left";
    return dy > 0 ? "bottom" : "top";
  }
  function center(box) { return { x: box.x + box.w / 2, y: box.y + box.h / 2 }; }

  function endpoint(ref, side, otherRef, boxes) {
    if (Array.isArray(ref)) return { x: ref[0], y: ref[1], dir: null };
    const box = boxes[ref];
    if (!box) return null;
    if (side) return anchor(box, side);
    const other = Array.isArray(otherRef) ? { x: otherRef[0], y: otherRef[1] } : boxes[otherRef] ? center(boxes[otherRef]) : center(box);
    return anchor(box, guessSide(box, other));
  }

  function drawArrow(g, e, boxes, defs) {
    const a = endpoint(e.from, e.fromSide, e.to, boxes);
    const b = endpoint(e.to, e.toSide, e.from, boxes);
    if (!a || !b) return;
    const dir = a.dir || b.dir || (Math.abs(b.x - a.x) >= Math.abs(b.y - a.y) ? "h" : "v");
    let pts;
    if (a.x === b.x || a.y === b.y) pts = [a, b];
    else if (dir === "h" && (b.dir || "h") === "h") { const mx = (a.x + b.x) / 2; pts = [a, { x: mx, y: a.y }, { x: mx, y: b.y }, b]; }
    else if (dir === "v" && (b.dir || "v") === "v") { const my = (a.y + b.y) / 2; pts = [a, { x: a.x, y: my }, { x: b.x, y: my }, b]; }
    else if (dir === "h") pts = [a, { x: b.x, y: a.y }, b];
    else pts = [a, { x: a.x, y: b.y }, b];

    const stroke = color(e.color, INK, "var(--ink)");
    const attrs = {
      points: pts.map((p) => `${p.x},${p.y}`).join(" "), fill: "none", stroke, "stroke-width": 1.5,
      "marker-end": e.head === "none" ? null : "url(#tw-arrow)",
      "marker-start": e.head === "both" ? "url(#tw-arrow-start)" : null,
      "stroke-dasharray": e.dash ? "6 5" : null,
    };
    el("polyline", attrs, g);
    if (e.label) {
      // label sits on the middle segment
      const i = Math.floor((pts.length - 1) / 2);
      const p = pts[i], q = pts[i + 1];
      const mx = (p.x + q.x) / 2, my = (p.y + q.y) / 2;
      const w = Math.max(40, e.label.length * 7.4 + 16);
      foreign(g, mx - w / 2, my - 11, w, 22, `<span class="bt-label">${esc(e.label)}</span>`, "bt free", "text-align:center");
    }
  }

  function markers(svg) {
    const defs = el("defs", {}, svg);
    const m1 = el("marker", { id: "tw-arrow", viewBox: "0 0 10 10", refX: 9, refY: 5, markerWidth: 7, markerHeight: 7, orient: "auto-start-reverse" }, defs);
    el("path", { d: "M0,0 L10,5 L0,10 z", fill: "var(--ink)" }, m1);
    const m2 = el("marker", { id: "tw-arrow-start", viewBox: "0 0 10 10", refX: 9, refY: 5, markerWidth: 7, markerHeight: 7, orient: "auto-start-reverse" }, defs);
    el("path", { d: "M0,0 L10,5 L0,10 z", fill: "var(--ink)" }, m2);
    return defs;
  }

  // ---------- main ----------
  function render(host, data) {
    const order = data.order || Object.keys(data.elements);
    const els = order.map((id) => ({ id, ...data.elements[id] })).filter((e) => e.type);
    const boxes = {};
    for (const e of els) { const b = bounds(e); if (b) boxes[e.id] = b; }
    // arrows are anchored to the real shape boxes, not to the extended container box
    for (const e of els) if (e.type === "container") boxes[e.id] = { x: e.x, y: e.y, w: e.w, h: e.h };

    const all = Object.values(boxes);
    const minX = Math.min(...all.map((b) => b.x)) - 60, minY = Math.min(...all.map((b) => b.y)) - 60;
    const maxX = Math.max(...all.map((b) => b.x + b.w)) + 60, maxY = Math.max(...all.map((b) => b.y + b.h)) + 60;
    const full = { x: minX, y: minY, w: maxX - minX, h: maxY - minY };

    const sections = els.filter((e) => e.type === "container" && e.text);
    host.innerHTML = `<div class="board">
      <div class="board-tools">
        <button class="btn" type="button" data-a="fit">Fit</button>
        <button class="btn" type="button" data-a="out">−</button>
        <button class="btn" type="button" data-a="in">+</button>
        <label><span class="visually-hidden">Jump to section</span>
          <select data-a="jump"><option value="">Jump to section…</option>
          ${sections.map((s) => `<option value="${esc(s.id)}">${esc(s.text)}</option>`).join("")}</select></label>
        <span class="hint">Drag to pan, scroll to zoom. ${esc(data.source && data.source.note ? data.source.note : "")}</span>
      </div>
      <div class="board-canvas"></div></div>`;
    const canvas = host.querySelector(".board-canvas");
    const svg = el("svg", { role: "img", "aria-label": data.title || "Whiteboard" }, canvas);
    const defs = markers(svg);
    const g = el("g", {}, svg);

    for (const e of els) {
      try {
        if (e.type === "container") drawContainer(g, e);
        else if (e.type === "shape") drawShape(g, e);
        else if (e.type === "text") drawText(g, e);
        else if (e.type === "stroke") drawStroke(g, e);
        else if (e.type === "link") drawLink(g, e);
        else if (e.type === "arrow") drawArrow(g, e, boxes, defs);
      } catch (err) {
        console.warn("board: cannot draw", e.id, err);
      }
    }

    // ---------- view box handling ----------
    let vb = { ...full };
    const setVB = () => svg.setAttribute("viewBox", `${vb.x} ${vb.y} ${vb.w} ${vb.h}`);
    function fitTo(box, pad) {
      const r = canvas.getBoundingClientRect();
      const aspect = r.width / Math.max(1, r.height);
      let w = box.w + pad * 2, h = box.h + pad * 2;
      if (w / h > aspect) h = w / aspect; else w = h * aspect;
      vb = { x: box.x + box.w / 2 - w / 2, y: box.y + box.h / 2 - h / 2, w, h };
      setVB();
    }
    function zoom(factor, cx, cy) {
      const nx = cx - (cx - vb.x) * factor, ny = cy - (cy - vb.y) * factor;
      vb = { x: nx, y: ny, w: vb.w * factor, h: vb.h * factor };
      setVB();
    }
    function toBoard(clientX, clientY) {
      const r = canvas.getBoundingClientRect();
      return { x: vb.x + ((clientX - r.left) / r.width) * vb.w, y: vb.y + ((clientY - r.top) / r.height) * vb.h };
    }
    requestAnimationFrame(() => fitTo(full, 0));

    canvas.addEventListener("wheel", (ev) => {
      ev.preventDefault();
      const p = toBoard(ev.clientX, ev.clientY);
      zoom(ev.deltaY > 0 ? 1.12 : 1 / 1.12, p.x, p.y);
    }, { passive: false });

    let drag = null;
    canvas.addEventListener("pointerdown", (ev) => {
      if (ev.target.closest && ev.target.closest("a")) return;
      drag = { x: ev.clientX, y: ev.clientY, vb: { ...vb } };
      canvas.classList.add("dragging");
      canvas.setPointerCapture(ev.pointerId);
    });
    canvas.addEventListener("pointermove", (ev) => {
      if (!drag) return;
      const r = canvas.getBoundingClientRect();
      vb = { ...drag.vb, x: drag.vb.x - ((ev.clientX - drag.x) / r.width) * drag.vb.w, y: drag.vb.y - ((ev.clientY - drag.y) / r.height) * drag.vb.h };
      setVB();
    });
    const end = () => { drag = null; canvas.classList.remove("dragging"); };
    canvas.addEventListener("pointerup", end);
    canvas.addEventListener("pointercancel", end);

    host.querySelector(".board-tools").addEventListener("click", (ev) => {
      const a = ev.target.getAttribute && ev.target.getAttribute("data-a");
      const c = { x: vb.x + vb.w / 2, y: vb.y + vb.h / 2 };
      if (a === "fit") fitTo(full, 0);
      if (a === "in") zoom(1 / 1.25, c.x, c.y);
      if (a === "out") zoom(1.25, c.x, c.y);
    });
    host.querySelector('select[data-a="jump"]').addEventListener("change", (ev) => {
      const b = boxes[ev.target.value];
      if (b) fitTo({ x: b.x, y: b.y - 40, w: b.w, h: b.h + 40 }, 30);
    });
  }

  window.TwBoard = { render };
})();
