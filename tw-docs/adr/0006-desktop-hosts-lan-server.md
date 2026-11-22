# 0006. Desktop app hosts a LAN server for tablets

- **Status:** Proposed
- **Date:** 2026-11-21
- **Deciders:** project owner (pending)

## Context

Mechanics work at the stand, away from the front-desk PC. The owner wants phones and tablets to talk to the workshop PC over Wi-Fi. The desktop app already runs Spring Boot.

## Decision

We will run an embedded HTTP server inside the desktop app (`app-desktop-standalone`): REST + WebSocket from `tw-rest`, and a mobile web UI from `tw-shell-web`, on the local network.

- Devices pair by scanning a QR code with the PC address and a one-time device token.
- The PC is found by mDNS (`twister.local`) or by the IP in the QR code.
- The installer adds a Windows Firewall rule for the chosen port.
- HTTP on the LAN for the first version; HTTPS (and PWA/offline mode) later if needed.

## Options considered

- **Native mobile app** — install and update burden, a second codebase.
- **Cloud server** — needs internet and hosting; postponed to the product phase.
- **Embedded LAN server** — proposed: one process, nothing to install on tablets.

## Consequences

- If the PC is off, tablets do not work.
- Guest Wi-Fi with client isolation blocks the connection; the setup guide must mention it.
- Customer-facing features (status page by QR, approval by link) need internet access and wait for the cloud server.
