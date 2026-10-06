<template>
  <!-- ── Navbar ─────────────────────────────────────── -->
  <nav class="navbar">
    <div class="wrap navbar-inner">
      <a href="#" class="navbar-logo">
        <span>Vault</span>Cam
      </a>
      <div class="nav-actions">
        <a href="#como-funciona" class="btn btn-secondary btn-sm">Como funciona</a>
        <a href="#download" class="btn btn-primary btn-sm">Baixar APK</a>
      </div>
    </div>
  </nav>

  <!-- ── Hero ──────────────────────────────────────── -->
  <section class="section hero-section" id="hero">
    <div class="wrap">
      <div class="hero-content anim-up">
        <p class="eyebrow">DePIN · Solana · Evidências Veiculares</p>
        <h1 class="hero-title">
          Sua dashcam vira<br>
          <span class="hero-title-accent">testemunha automática</span>
        </h1>
        <p class="body-text hero-subtitle">
          47% dos motoristas que sofreram batida sem prova perderam para a seguradora.
          VaultCam criptografa, sobe para blockchain e disponibiliza como evidência imutável —
          sem servidor central, sem intermediário.
        </p>

        <div class="hero-cta anim-up anim-delay-1">
          <a href="#download" class="btn btn-primary btn-lg">
            <i class="pi pi-download"></i>
            Baixar APK (Android)
          </a>
          <a href="#demo" class="btn btn-secondary btn-lg">
            <i class="pi pi-play"></i>
            Assista a demo
          </a>
        </div>

        <div class="hero-stats anim-up anim-delay-2">
          <div class="stat-chip">
            <span class="stat-chip-value">$0.10</span>
            <span class="stat-chip-label">por vídeo criptografado</span>
          </div>
          <div class="stat-chip">
            <span class="stat-chip-value">500m</span>
            <span class="stat-chip-label">raio de alerta P2P</span>
          </div>
          <div class="stat-chip">
            <span class="stat-chip-value">&lt;500ms</span>
            <span class="stat-chip-label">latência ao vivo (frota)</span>
          </div>
          <div class="stat-chip">
            <span class="stat-chip-value">∞</span>
            <span class="stat-chip-label">retenção no Arweave</span>
          </div>
        </div>
      </div>

      <div class="hero-badge-row anim-up anim-delay-3">
        <span class="tag tag-green"><i class="pi pi-shield"></i> Lit Protocol E2E</span>
        <span class="tag tag-muted"><i class="pi pi-database"></i> Irys / Arweave</span>
        <span class="tag tag-muted">Solana Mobile</span>
        <span class="tag tag-purple"><i class="pi pi-bolt"></i> SKR Token</span>
      </div>
    </div>
  </section>

  <hr class="divider" />

  <!-- ── Problema ───────────────────────────────────── -->
  <section class="section" id="problema">
    <div class="wrap">
      <div class="section-header">
        <p class="eyebrow">O Problema</p>
        <h2 class="section-title">Câmeras que não protegem ninguém</h2>
        <p class="body-text section-subtitle">
          Dashcams convencionais gravam em loop local — fáceis de apagar, difíceis de autenticar,
          inúteis sem prova de integridade.
        </p>
      </div>

      <div class="grid-2 problem-grid">
        <div class="card card-red">
          <p class="problem-label"><i class="pi pi-times-circle"></i> Dashcam tradicional</p>
          <ul class="check-list">
            <li class="check-item">
              <span class="check-icon" style="color: var(--color-red)">✕</span>
              Vídeo deletado facilmente pelo motorista
            </li>
            <li class="check-item">
              <span class="check-icon" style="color: var(--color-red)">✕</span>
              Sem prova de integridade (facilmente editado)
            </li>
            <li class="check-item">
              <span class="check-icon" style="color: var(--color-red)">✕</span>
              Servidor central = ponto único de falha
            </li>
            <li class="check-item">
              <span class="check-icon" style="color: var(--color-red)">✕</span>
              Nenhum incentivo para compartilhar
            </li>
          </ul>
        </div>
        <div class="card card-green">
          <p class="problem-label"><i class="pi pi-check-circle"></i> VaultCam</p>
          <ul class="check-list">
            <li class="check-item">
              <span class="check-icon">✓</span>
              Auto-lock ao detectar impacto — impossível deletar
            </li>
            <li class="check-item">
              <span class="check-icon">✓</span>
              Hash SHA-256 + timestamp on-chain (Solana)
            </li>
            <li class="check-item">
              <span class="check-icon">✓</span>
              Armazenamento permanente Arweave (via Irys)
            </li>
            <li class="check-item">
              <span class="check-icon">✓</span>
              Rede de incentivos: compartilhe prova, ganhe créditos
            </li>
          </ul>
        </div>
      </div>

      <div class="cost-bar">
        <div>
          <p class="cost-vs">Custo por vídeo de evidência</p>
          <p class="cost-value">$0.10</p>
          <p class="body-sm">Arweave $0.02 + Solana $0.001 + protocolo $0.079</p>
        </div>
        <div class="cost-comparison">
          <p class="cost-vs">vs. perito judicial</p>
          <p class="cost-old">$800–$3.000</p>
        </div>
      </div>
    </div>
  </section>

  <hr class="divider" />

  <!-- ── Como Funciona ──────────────────────────────── -->
  <section class="section" id="como-funciona">
    <div class="wrap-sm">
      <div class="section-header">
        <p class="eyebrow">Como Funciona</p>
        <h2 class="section-title">Da gravação à evidência em 4 passos</h2>
      </div>

      <div class="flow-list">
        <div class="flow-step">
          <div class="flow-step-num">1</div>
          <div>
            <p class="flow-step-title">Grava automaticamente</p>
            <p class="body-sm">
              App roda em segundo plano. CameraX grava em loop — os últimos 10 vídeos
              ficam locais e gratuitos.
            </p>
          </div>
        </div>

        <div class="flow-step">
          <div class="flow-step-num">2</div>
          <div>
            <p class="flow-step-title">Detecta o impacto</p>
            <p class="body-sm">
              Acelerômetro monitora força G. Acima de 2,5G: vídeo auto-bloqueado para
              deleção + alerta Bluetooth transmitido para devices próximos.
            </p>
          </div>
        </div>

        <div class="flow-step">
          <div class="flow-step-num">3</div>
          <div>
            <p class="flow-step-title">Criptografa e sobe</p>
            <p class="body-sm">
              Lit Protocol criptografa com chave que nunca toca o device.
              Upload para Irys (Arweave-backed). Hash + GPS + timestamp registrado on-chain.
            </p>
          </div>
        </div>

        <div class="flow-step">
          <div class="flow-step-num">4</div>
          <div>
            <p class="flow-step-title">Acessa com wallet</p>
            <p class="body-sm">
              Assine com sua Solana wallet → Lit verifica condição on-chain →
              vídeo descriptografado. Ou pague USDC para liberar acesso por terceiros.
            </p>
          </div>
        </div>
      </div>

      <div class="alert-banner alert-banner-green flow-note">
        <i class="pi pi-info-circle"></i>
        <span>
          <strong>Rede P2P:</strong> ao disponibilizar um vídeo de incidente alheio,
          você ganha créditos SKR e desbloqueia seus próprios vídeos gratuitamente.
        </span>
      </div>
    </div>
  </section>

  <hr class="divider" />

  <!-- ── Perfis de usuário ───────────────────────────── -->
  <section class="section" id="perfis">
    <div class="wrap">
      <div class="section-header">
        <p class="eyebrow">Para quem é</p>
        <h2 class="section-title">Três usuários, três modelos</h2>
      </div>

      <div class="grid-3">
        <div class="profile-card">
          <div class="profile-badge profile-badge-green">
            <i class="pi pi-user"></i>
            Individual
          </div>
          <h3 class="profile-card-title">Motorista comum</h3>
          <p class="body-sm">
            10 vídeos locais grátis. Acima do limite: criptografa e sobe por $0.10/vídeo.
            Recupera com assinatura de wallet.
          </p>
          <div class="profile-pricing">
            <span class="tag tag-green">Grátis até 10 vídeos</span>
          </div>
        </div>

        <div class="profile-card">
          <div class="profile-badge profile-badge-amber">
            <i class="pi pi-car"></i>
            App Driver
          </div>
          <h3 class="profile-card-title">Motorista de app</h3>
          <p class="body-sm">
            Histórico permanente de viagens. Proteção automática em roubos e acidentes.
            Créditos ganhos ao compartilhar evidências da rede.
          </p>
          <div class="profile-pricing">
            <span class="tag tag-amber">Pay-per-video</span>
          </div>
        </div>

        <div class="profile-card">
          <div class="profile-badge profile-badge-purple">
            <i class="pi pi-building"></i>
            Frota
          </div>
          <h3 class="profile-card-title">Empresa de frota</h3>
          <p class="body-sm">
            Dashboard web com N câmeras ao vivo (LiveKit, sub-500ms).
            Billing on-chain em USDC ou SKR — NFT de licença por device.
          </p>
          <div class="profile-pricing">
            <span class="tag tag-purple">USDC/mês por device</span>
          </div>
        </div>
      </div>
    </div>
  </section>

  <hr class="divider" />

  <!-- ── Tecnologia ─────────────────────────────────── -->
  <section class="section section-sm" id="tech">
    <div class="wrap-sm">
      <div class="section-header">
        <p class="eyebrow">Stack técnica</p>
        <h2 class="section-title">Tecnologia sem jargão</h2>
      </div>

      <div class="card">
        <table class="tech-table">
          <tbody>
            <tr>
              <td>Lit Protocol</td>
              <td>Chave de criptografia nunca toca o celular — nem device rooteado consegue extrair</td>
            </tr>
            <tr>
              <td>Irys + Arweave</td>
              <td>Vídeo armazenado para sempre — nenhuma empresa pode deletar</td>
            </tr>
            <tr>
              <td>Solana on-chain</td>
              <td>Timestamp + GPS + hash registrados publicamente — impossível forjar retroativamente</td>
            </tr>
            <tr>
              <td>Bluetooth LE + Waku</td>
              <td>Alerta de incidente chega em devices próximos mesmo sem internet</td>
            </tr>
            <tr>
              <td>LiveKit WebRTC</td>
              <td>Câmeras da frota aparecem ao vivo no dashboard da empresa com menos de 500ms de atraso</td>
            </tr>
            <tr>
              <td>Mobile Wallet Adapter 2.0</td>
              <td>Autenticação via carteira Solana — sem login, sem senha, sem conta no servidor</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>

  <hr class="divider" />

  <!-- ── Checklist do que está pronto ──────────────── -->
  <section class="section section-sm" id="status">
    <div class="wrap-sm">
      <div class="section-header">
        <p class="eyebrow">Status atual</p>
        <h2 class="section-title">O que está funcionando hoje</h2>
      </div>

      <div class="grid-2">
        <div class="card">
          <p class="feature-group-label">Core &amp; Criptografia</p>
          <ul class="check-list">
            <li class="check-item"><span class="check-icon">✓</span> Gravação CameraX em loop local</li>
            <li class="check-item"><span class="check-icon">✓</span> Criptografia Lit Protocol (chave off-device)</li>
            <li class="check-item"><span class="check-icon">✓</span> Upload Irys com hash on-chain</li>
            <li class="check-item"><span class="check-icon">✓</span> MWA 2.0 — assinar + descriptografar</li>
            <li class="check-item"><span class="check-icon">✓</span> Pay-per-video em USDC</li>
          </ul>
        </div>
        <div class="card">
          <p class="feature-group-label">Rede &amp; Frota</p>
          <ul class="check-list">
            <li class="check-item"><span class="check-icon">✓</span> Detecção de impacto (G-force &gt; 2.5G)</li>
            <li class="check-item"><span class="check-icon">✓</span> Alerta BLE (offline, ~100m)</li>
            <li class="check-item"><span class="check-icon">✓</span> Alerta Waku P2P (online, 500m)</li>
            <li class="check-item"><span class="check-icon">✓</span> Sistema de créditos SKR</li>
            <li class="check-item"><span class="check-icon">✓</span> Fleet live streaming (LiveKit)</li>
          </ul>
        </div>
      </div>

      <div class="alert-banner alert-banner-amber status-note">
        <i class="pi pi-clock"></i>
        <span>
          <strong>Hackathon CLOCK IN · 08 out 2026.</strong> Construído em 7 dias para
          Solana Mobile. APK disponível para download.
        </span>
      </div>
    </div>
  </section>

  <hr class="divider" />

  <!-- ── CTA Download ───────────────────────────────── -->
  <section class="section" id="download">
    <div class="wrap-sm cta-section">
      <p class="eyebrow">Disponível agora</p>
      <h2 class="section-title">Pronto para instalar</h2>
      <p class="body-text cta-subtitle">
        Android 8.0+. Wallet Solana necessária para criptografia e recuperação de vídeos.
        Recomendamos Phantom ou Solflare.
      </p>
      <div class="cta-actions">
        <a href="#" class="btn btn-primary btn-lg">
          <i class="pi pi-download"></i>
          Baixar APK (Android)
        </a>
        <a href="https://github.com/jonmferreira/clock-in-mobile" target="_blank" class="btn btn-secondary btn-lg">
          <i class="pi pi-github"></i>
          Ver no GitHub
        </a>
      </div>
      <div class="cta-tags">
        <span class="tag tag-muted">Android 8.0+</span>
        <span class="tag tag-muted">Solana Wallet</span>
        <span class="tag tag-muted">Open Source</span>
        <span class="tag tag-green">CLOCK IN Hackathon 2026</span>
      </div>
    </div>
  </section>

  <!-- ── Footer ─────────────────────────────────────── -->
  <footer class="footer">
    <div class="wrap footer-inner">
      <div class="footer-brand">
        <a href="#" class="navbar-logo">
          <span>Vault</span>Cam
        </a>
        <p class="footer-text">Primeira rede descentralizada de evidências veiculares.</p>
      </div>
      <div class="footer-links">
        <a href="https://solanamobile.com" target="_blank" class="footer-link">Solana Mobile</a>
        <a href="https://docs.litprotocol.com" target="_blank" class="footer-link">Lit Protocol</a>
        <a href="https://docs.irys.xyz" target="_blank" class="footer-link">Irys</a>
        <a href="https://github.com/jonmferreira/clock-in-mobile" target="_blank" class="footer-link">GitHub</a>
      </div>
      <p class="footer-text footer-copy">
        Built on Solana · CLOCK IN Hackathon 2026
      </p>
    </div>
  </footer>
</template>

<script setup lang="ts">
</script>

<style src="./home.css" scoped></style>
