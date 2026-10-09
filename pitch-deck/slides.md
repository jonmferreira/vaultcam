---
theme: default
colorSchema: dark
title: VaultCam — Decentralized Evidence Network
info: false
highlighter: shiki
drawings:
  persist: false
transition: fade
mdc: true
fonts:
  sans: Inter
  weights: '400,500,600,700,800'
---

<!-- SLIDE 1 — CAPA -->
<div class="h-full flex flex-col items-center justify-center text-center">

<div style="font-size:56px;margin-bottom:14px;">🔐</div>

<h1 style="font-size:68px;font-weight:800;line-height:1;margin-bottom:10px;">
  Vault<span class="accent">Cam</span>
</h1>

<p style="font-size:16px;font-weight:500;letter-spacing:0.14em;text-transform:uppercase;color:rgba(255,255,255,0.65);margin-bottom:36px;">
  Decentralized Evidence Network
</p>

<div class="divider" style="width:480px;"></div>

<div style="display:flex;align-items:center;gap:8px;justify-content:center;margin:28px 0 32px;">
  <span class="badge"><span class="dot" style="background:#9945ff;"></span>Solana</span>
  <span style="color:rgba(255,255,255,0.2)">·</span>
  <span class="badge"><span class="dot" style="background:#f97316;"></span>Lit Protocol</span>
  <span style="color:rgba(255,255,255,0.2)">·</span>
  <span class="badge"><span class="dot" style="background:#3b82f6;"></span>Arweave</span>
  <span style="color:rgba(255,255,255,0.2)">·</span>
  <span class="badge"><span class="dot" style="background:#10b981;"></span>LiveKit</span>
  <span style="color:rgba(255,255,255,0.2)">·</span>
  <span class="badge"><span class="dot" style="background:#f59e0b;"></span>SKR Token</span>
</div>

<div class="hackathon-badge">
  <span class="hack-label">Submitted to</span>
  <span style="width:1px;height:18px;background:rgba(255,255,255,0.18);display:inline-block;"></span>
  <span style="font-size:13px;font-weight:600;color:#fff;">CLOCK IN · The Solana Mobile Hackathon</span>
</div>

</div>

---

<!-- SLIDE 2 — PROBLEMA -->
<div class="label-top">The Problem</div>

<div style="display:grid;grid-template-columns:1fr 1fr;gap:28px;align-items:start;">

<div class="card card-red">
  <div style="font-size:88px;font-weight:800;line-height:1;color:#ef4444;">47<span style="font-size:44px">%</span></div>
  <p style="color:rgba(255,255,255,0.9);font-size:17px;margin-top:10px;font-weight:500;line-height:1.5;">
    of drivers <strong style="color:#fff;">without recorded footage</strong><br>lost their insurance dispute
  </p>
  <p style="color:rgba(255,255,255,0.4);font-size:11px;margin-top:10px;">NHTSA Research 2025 · 14,200 incidents analyzed</p>
</div>

<div style="display:flex;flex-direction:column;gap:14px;">

  <div class="card" style="display:flex;gap:14px;align-items:flex-start;">
    <span style="font-size:24px;">🎥</span>
    <div>
      <div style="font-weight:700;color:#fff;font-size:15px;margin-bottom:4px;">No proof, no justice</div>
      <div style="color:rgba(255,255,255,0.6);font-size:13px;line-height:1.5;">Accidents happen in seconds. Without video, it's your word against theirs.</div>
    </div>
  </div>

  <div class="card" style="display:flex;gap:14px;align-items:flex-start;">
    <span style="font-size:24px;">🔓</span>
    <div>
      <div style="font-weight:700;color:#fff;font-size:15px;margin-bottom:4px;">Existing dashcams fail</div>
      <div style="color:rgba(255,255,255,0.6);font-size:13px;line-height:1.5;">Local storage is mutable. No chain of custody. Easily disputed in court.</div>
    </div>
  </div>

  <div class="card" style="display:flex;gap:14px;align-items:flex-start;">
    <span style="font-size:24px;">👤</span>
    <div>
      <div style="font-weight:700;color:#fff;font-size:15px;margin-bottom:4px;">Witnesses drive away</div>
      <div style="color:rgba(255,255,255,0.6);font-size:13px;line-height:1.5;">Bystanders with crucial footage have no incentive to stop and share.</div>
    </div>
  </div>

</div>
</div>

---

<!-- SLIDE 3 — SOLUÇÃO -->
<div class="label-top">The Solution</div>

<h2 style="font-size:30px;font-weight:800;margin-bottom:24px;line-height:1.3;">
  Three product layers. One network effect.
</h2>

<div style="display:grid;grid-template-columns:repeat(3,1fr);gap:18px;margin-bottom:20px;">

  <div class="card card-purple">
    <div style="font-weight:700;font-size:13px;letter-spacing:0.1em;text-transform:uppercase;color:rgba(139,92,246,0.8);margin-bottom:10px;">B2C · Individual drivers</div>
    <div style="color:rgba(255,255,255,0.85);font-size:13px;line-height:1.65;">
      Continuous dashcam recording. On collision detection, footage is encrypted (Lit Protocol) and stored permanently on Arweave. Recovery requires wallet signature — the key never touches the device.
    </div>
  </div>

  <div class="card card-teal">
    <div style="font-weight:700;font-size:13px;letter-spacing:0.1em;text-transform:uppercase;color:rgba(20,184,166,0.8);margin-bottom:10px;">P2P · Evidence network</div>
    <div style="color:rgba(255,255,255,0.85);font-size:13px;line-height:1.65;">
      Impact triggers BLE + Waku alerts to nearby vehicles. Drivers who contribute footage earn SKR token rewards. Every incident gets an on-chain hash — GPS, timestamp, and contributing wallet addresses.
    </div>
  </div>

  <div class="card card-green">
    <div style="font-weight:700;font-size:13px;letter-spacing:0.1em;text-transform:uppercase;color:rgba(16,185,129,0.8);margin-bottom:10px;">B2B · Fleet management</div>
    <div style="color:rgba(255,255,255,0.85);font-size:13px;line-height:1.65;">
      Live streaming of fleet cameras via LiveKit (&lt;500ms latency). Multi-camera dashboard for operators. Monthly USDC or SKR subscription per active device.
    </div>
  </div>

</div>

<div style="padding:14px 18px;border-radius:10px;background:rgba(255,255,255,0.03);border:1px solid rgba(255,255,255,0.07);font-size:13px;color:rgba(255,255,255,0.6);">
  🔐 <strong style="color:#fff;">Encryption key stays on-chain, never on the device.</strong> Lit Protocol nodes reconstruct it only when on-chain conditions are met — owner signature, payment, or court order.
</div>

---

<!-- SLIDE 4 — COMPETIDORES (MERCADO REAL) -->
<div class="label-top">Competitive Landscape</div>

<h2 style="font-size:24px;font-weight:800;margin-bottom:14px;">
  The dashcam market has no blockchain-native competitor
</h2>

<table class="compact-table">
  <thead>
    <tr>
      <th style="width:22%;">Product</th>
      <th>Immutable Storage</th>
      <th>P2P Alert Network</th>
      <th>On-Chain Proof</th>
      <th>Crypto Rewards</th>
      <th>Mobile Native</th>
    </tr>
  </thead>
  <tbody>
    <tr class="highlight">
      <td><strong style="color:#8b5cf6;">VaultCam</strong></td>
      <td class="check">✓</td>
      <td class="check">✓</td>
      <td class="check">✓</td>
      <td class="check">✓</td>
      <td class="check">✓</td>
    </tr>
    <tr>
      <td><span style="color:rgba(255,255,255,0.7);">Nextbase 622GW</span></td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
    </tr>
    <tr>
      <td><span style="color:rgba(255,255,255,0.7);">BlackVue DR970X</span></td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
    </tr>
    <tr>
      <td><span style="color:rgba(255,255,255,0.7);">Samsara Fleet</span></td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
    </tr>
    <tr>
      <td><span style="color:rgba(255,255,255,0.7);">Garmin Dash Cam</span></td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
      <td class="cross">✗</td>
    </tr>
  </tbody>
</table>

<div style="display:grid;grid-template-columns:1fr 1fr;gap:14px;margin-top:14px;">
  <div class="card" style="padding:12px 16px;">
    <div style="font-size:10px;color:rgba(255,255,255,0.45);font-weight:700;text-transform:uppercase;letter-spacing:0.1em;margin-bottom:4px;">Total addressable market</div>
    <div style="font-weight:700;color:#fff;font-size:14px;">$8.5B dashcam market · +12% YoY</div>
    <div style="font-size:11px;color:rgba(255,255,255,0.6);margin-top:3px;line-height:1.45;">500M+ dashcam-equipped vehicles by 2027. US insurance fraud costs $40B/year — video evidence is the fastest-growing countermeasure.</div>
  </div>
  <div class="card" style="padding:12px 16px;">
    <div style="font-size:10px;color:rgba(255,255,255,0.45);font-weight:700;text-transform:uppercase;letter-spacing:0.1em;margin-bottom:4px;">Why now</div>
    <div style="font-weight:700;color:#fff;font-size:14px;">Hardware commoditized. Blockchain mature.</div>
    <div style="font-size:11px;color:rgba(255,255,255,0.6);margin-top:3px;line-height:1.45;">Every phone is a dashcam. Solana runs 65,000 TPS at &lt;$0.001/tx — real-time evidence at scale is viable today.</div>
  </div>
</div>

---

<!-- SLIDE 5 — CTA -->
<div class="h-full flex flex-col items-center justify-center text-center">

<div class="label-top" style="text-align:center;">Business Model</div>

<h2 style="font-size:32px;font-weight:800;margin-bottom:32px;line-height:1.2;">
  Three revenue streams.<br>One network that grows itself.
</h2>

<div style="display:grid;grid-template-columns:repeat(3,1fr);gap:16px;text-align:left;margin-bottom:28px;width:100%;">

  <div class="card card-purple" style="padding:20px;">
    <div style="font-weight:700;font-size:13px;letter-spacing:0.1em;text-transform:uppercase;color:rgba(139,92,246,0.8);margin-bottom:10px;">B2C — Pay per recovery</div>
    <div style="color:rgba(255,255,255,0.8);font-size:13px;line-height:1.6;">Owner recovers footage free. Third parties pay <strong style="color:#fff;">USDC or SKR</strong> per video access. Evidence requests from insurers and legal teams generate recurring revenue.</div>
  </div>

  <div class="card card-teal" style="padding:20px;">
    <div style="font-weight:700;font-size:13px;letter-spacing:0.1em;text-transform:uppercase;color:rgba(20,184,166,0.8);margin-bottom:10px;">P2P — Token economy</div>
    <div style="color:rgba(255,255,255,0.8);font-size:13px;line-height:1.6;">Drivers earn <strong style="color:#fff;">SKR</strong> for contributing footage to active incidents. Token demand grows with network adoption. No marketing spend required — incentive drives acquisition.</div>
  </div>

  <div class="card card-green" style="padding:20px;">
    <div style="font-weight:700;font-size:13px;letter-spacing:0.1em;text-transform:uppercase;color:rgba(16,185,129,0.8);margin-bottom:10px;">B2B — Fleet SaaS</div>
    <div style="color:rgba(255,255,255,0.8);font-size:13px;line-height:1.6;">Monthly subscription per active vehicle. Live camera dashboard, incident reporting, and on-chain audit trail. Target: logistics, ride-hailing, and municipal fleets.</div>
  </div>

</div>

<div class="divider" style="width:480px;"></div>

<div style="margin-top:22px;display:flex;align-items:center;justify-content:center;gap:40px;">
  <div style="text-align:center;">
    <div style="font-size:24px;font-weight:800;color:#fff;">Android MVP</div>
    <div style="font-size:12px;color:rgba(255,255,255,0.5);margin-top:2px;">All 3 layers functional</div>
  </div>
  <div style="width:1px;height:36px;background:rgba(255,255,255,0.12);"></div>
  <div style="text-align:center;">
    <div style="font-size:24px;font-weight:800;color:#fff;">MWA 2.0</div>
    <div style="font-size:12px;color:rgba(255,255,255,0.5);margin-top:2px;">Solana Mobile native</div>
  </div>
  <div style="width:1px;height:36px;background:rgba(255,255,255,0.12);"></div>
  <div style="text-align:center;">
    <div style="font-size:24px;font-weight:800;color:#fff;">Arweave</div>
    <div style="font-size:12px;color:rgba(255,255,255,0.5);margin-top:2px;">Permanent storage live</div>
  </div>
  <div style="width:1px;height:36px;background:rgba(255,255,255,0.12);"></div>
  <div style="text-align:center;">
    <div style="font-size:24px;font-weight:800;color:#fff;">github.com/jonmferreira/vaultcam</div>
    <div style="font-size:12px;color:rgba(255,255,255,0.5);margin-top:2px;">Open source</div>
  </div>
</div>

</div>
