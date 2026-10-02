# CLAUDE.md — VaultCam: Decentralized Evidence Network
**Hackathon:** CLOCK IN — The Solana Mobile Hackathon  
**Deadline:** 08/out/2026 · **Prêmio alvo:** Top 5 ($10k–$30k) + SKR Prize ($10k)  
**Pasta:** `C:\Projetos\clock-in-mobile`  
**Spec câmera base:** `C:\Projetos\mvp-ideas\018-dashcam-android.md`

---

## Conceito

App Android de dashcam com rede descentralizada de evidências.

**Três camadas de produto:**

```
B2C — Individual
  Grava → 10 vídeos locais grátis → acima do limite: criptografa + sobe para Irys
  Recuperar vídeo = assinar com wallet Solana (ou pagar USDC por vídeo)
  Contribuir vídeo como prova = ganhar créditos para desbloquear os próprios

REDE — Evidências P2P
  Parada brusca detectada → alerta Bluetooth LE (offline) + Waku (online)
  Dispositivos no raio de 500m: vídeos auto-bloqueados para deleção
  Usuário toca "Disponibilizar para rede" → upload Irys → créditos → pode deletar local
  Incidente registrado on-chain (hash + GPS + timestamp)

B2B — Frota empresarial
  Empresário conecta ao vivo câmeras da frota via LiveKit
  Dashboard web: N câmeras simultâneas, sub-500ms latência
  Billing: USDC/mês por device ativo (NFT de licença on-chain)
```

---

## Análise competitiva — Colosseum histórico

**Nenhum vencedor anterior explorou esta combinação.** Projetos adjacentes mapeados:

| Projeto | Hackathon | O que fez | Diferença do VaultCam |
|---|---|---|---|
| **TAPEDRIVE** | Breakout ($50k Grand) | Storage layer Solana com Merkle proofs | Storage genérico — sem caso de uso de evidência |
| **Chakra Drive** | Radar ($15k) | E2E encryption + Irys + Lit Protocol | Arquivos genéricos — sem câmera, sem rede de alertas |
| **CrowdBrain** | Frontier ($30k Grand) | DePIN robótica, teleoperation via vídeo | Robótica — sem evidência veicular, sem frota |
| **Humanship ID** | Cypherpunk ($5k) | Identidade auto-soberana via NFC + biometria | Identidade — não armazena vídeo |

**Conclusão:** VaultCam é o primeiro projeto a combinar dashcam + rede P2P de evidências + frota ao vivo + Solana. Espaço limpo.

---

## Stack de decisões fechadas

| Camada | Tecnologia | Por quê |
|---|---|---|
| Linguagem | Kotlin | — |
| UI | Jetpack Compose | — |
| Câmera | CameraX 1.3+ (`camera-camera2`, `camera-video`) | Dual front+back simultâneo |
| Criptografia | Lit Protocol (chave nunca no device) | Rooted device não consegue extrair chave |
| Storage permanente | Irys REST API via OkHttp | Arweave-backed, imutável |
| Wallet | Mobile Wallet Adapter 2.0 (`mobile-wallet-adapter-clientlib-ktx`) | Oficial Solana Mobile |
| Live streaming | LiveKit Android SDK | SFU open source, sub-500ms, N câmeras |
| P2P alerts (online) | Waku Protocol via HTTP/REST | P2P Web3 nativo, sem servidor central |
| P2P alerts (offline) | Bluetooth LE nativo Android | Funciona sem internet, ~100m range |
| Impacto | `SensorManager TYPE_LINEAR_ACCELERATION` | G-force > 2.5G = possível colisão |
| Pagamento | USDC + SKR token on Solana | Pay-per-video + SKR para prize extra |
| On-chain | Solana Anchor | Hash + GPS + owner + estado do vídeo |
| Arquitetura | MVVM + StateFlow + Coroutines | — |

---

## Dependências (build.gradle.kts)

```kotlin
dependencies {
    // CameraX
    val cameraxVersion = "1.3.4"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-video:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // Solana Mobile Wallet Adapter 2.0
    implementation("com.solanamobile:mobile-wallet-adapter-clientlib-ktx:2.0.3")

    // LiveKit (fleet live streaming)
    implementation("io.livekit:livekit-android:2.7.0")

    // WebRTC — GetStream fork (P2P 1-to-1)
    implementation("io.getstream:stream-webrtc-android:1.1.2")

    // HTTP (Irys REST + Waku REST)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Coroutines + ViewModel
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Localização + DataStore
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
}
```

---

## Versões — do MVP ao produto completo

### v0.1 — Core Dashcam (MÍNIMO PARA SUBMISSÃO)
**Prazo:** Dias 1–4 (01–04/out)

- [ ] Projeto Android Studio: Kotlin + Compose + CameraX básico
- [ ] Gravação câmera traseira em loop com limite de 10 vídeos locais
- [ ] Criptografia via Lit Protocol (chave gerenciada pelos nós Lit, nunca no device)
- [ ] Upload de vídeo criptografado para Irys via REST
- [ ] Mobile Wallet Adapter 2.0: conectar wallet → assinar → descriptografar vídeo
- [ ] Pay-per-video: pagar USDC → liberar acesso
- [ ] UI básica: tela de gravação + galeria + tela de recuperação

**Demo mínima:** gravar → criptografar → subir Irys → recuperar com assinatura de wallet

---

### v0.2 — Rede de Evidências (DIFERENCIAL DO PITCH)
**Prazo:** Dias 5–6 (05–06/out)

- [ ] `SensorManager`: detecção de parada brusca (G-force > 2.5G)
- [ ] Ao detectar impacto: auto-lock dos últimos 2 min de vídeo
- [ ] Bluetooth LE: broadcast de alerta de incidente para devices próximos (~100m)
- [ ] Waku Protocol: publicar incidente no tópico P2P para devices no raio de 500m
- [ ] Devices recebem notificação: "Incidente detectado. Vídeos bloqueados para deleção."
- [ ] Botão "Disponibilizar para rede": upload Irys + hash on-chain + créditos ao usuário
- [ ] Sistema de créditos: 1 vídeo contribuído = X créditos = Y desbloqueios gratuitos
- [ ] GPS + timestamp gravados como metadata de cada vídeo (para matching geográfico)

**Demo v0.2:** impacto simulado → alerta nos devices → usuário contribui → ganha créditos

---

### v0.3 — Frota Empresarial (SE DER TEMPO)
**Prazo:** Dia 7 (07/out) — ou roadmap pós-hackathon

- [ ] LiveKit: streaming ao vivo de cada device para dashboard central
- [ ] Dashboard web (Next.js): visualizar N câmeras simultaneamente
- [ ] Billing on-chain: NFT de licença por device ativo (USDC/mês)
- [ ] Smart contract verifica licença antes de aceitar stream
- [ ] SKR token integration: opção de pagar assinatura em SKR → SKR Prize $10k

**Demo v0.3:** empresário abre dashboard → vê câmeras ao vivo da frota em tempo real

---

### v0.4 — Plataforma Completa (ROADMAP)
Pós-hackathon:

- [ ] Acesso judicial: multisig (usuário + autoridade) atualiza condição Lit on-chain
- [ ] Mercado de evidências: listar vídeos como prova disponível para compradores
- [ ] Integração com seguradoras via API
- [ ] Fontes externas de incidentes: Waze API, OpenStreetMap incidents
- [ ] Dual camera simultâneo front+back (per spec 018 v0.3)
- [ ] Overlay GPS + data/hora no vídeo (per spec 018 v0.5)
- [ ] Foreground Service para gravação em background (per spec 018 v0.4)

---

## Arquitetura de criptografia (Lit Protocol)

```
Gravação → MP4 local
    ↓
Lit Protocol SDK: criptografa com chave gerenciada pelos nós Lit
(chave NUNCA é armazenada no device)
    ↓
Arquivo .enc sobe para Irys → txId retornado
    ↓
[txId + hash SHA-256 + owner pubkey + GPS + timestamp] → on-chain Solana
    ↓
Para descriptografar:
  Usuário assina mensagem com wallet
    → Lit verifica condição on-chain (é o owner? pagou? recebeu acesso?)
    → Nós Lit reconstroem chave → vídeo descriptografado em memória
    → Nunca persiste localmente após sessão
```

---

## Segurança em tempo real

| Vetor de ataque | Proteção | Como |
|---|---|---|
| Device rooteado extrai chave | Lit Protocol | Chave nunca existe no device |
| Stream interceptado | WebRTC DTLS-SRTP | E2E criptografado por padrão |
| Alerta P2P interceptado | Waku E2E encryption | Mensagens efêmeras criptografadas |
| Acesso não autorizado ao stream da frota | LiveKit JWT token | Token com expiração gerado on-chain |
| Vídeo deletado antes de ser prova | Auto-lock + Irys imutável | Lock impede deleção; Arweave é permanente |

---

## LGPD — Conformidade por design

- Dados sob controle do usuário (princípio de autodeterminação)
- Upload para Irys = opt-in explícito com consentimento on-chain
- Sem obrigação legal de armazenar vídeo pessoal (pesquisa verificada)
- Para frotas empresariais: câmera de cabine requer política de privacidade formal

---

## MCPs para esta sessão

| MCP | Como usar | Instalar |
|---|---|---|
| **Android Studio MCP** | Build/lint/run/ADB/emulator | `git clone github.com/MauricePutinas/Android-Studio-MCP-Claude-Code && uv sync` |
| **ADB MCP** | Controle device via ADB (alternativa leve) | `npx @modelcontextprotocol/android-mcp-server` |

### .mcp.json do projeto
```json
{
  "mcpServers": {
    "android-studio": {
      "command": "uv",
      "args": ["run", "python", "-m", "android_studio_mcp"],
      "cwd": "C:\\caminho\\para\\Android-Studio-MCP-Claude-Code"
    }
  }
}
```

---

## O que instalar antes de abrir a sessão

| Ferramenta | Status | Ação |
|---|---|---|
| **Android Studio** | Verificar | Se não: `winget install Google.AndroidStudio` |
| **Android SDK 35** | Via SDK Manager | API Level 35 obrigatório |
| **JDK 17+** | Vem com Android Studio | — |
| **uv** | ✅ Instalado | — |
| **Android Studio MCP** | A instalar | Comandos acima |
| **Device físico Android** | Necessário | ConcurrentCamera não funciona em emulador |

---

## Requisitos de submissão CLOCK IN (08/out)

- [ ] APK Android funcional (não AAB)
- [ ] Repositório GitHub público
- [ ] Vídeo demo (gravar → criptografar → incidente → alerta → recuperar)
- [ ] Pitch deck (5 slides: problema / solução / demo / monetização / equipe)
- [ ] Integração SKR para prize extra $10k (billing de frota em SKR)

---

## Links

- Solana Mobile docs: https://docs.solanamobile.com
- MWA Android guide: https://docs.solanamobile.com/android-native/using_mobile_wallet_adapter
- LiveKit Android: https://docs.livekit.io/client-sdk-android
- Lit Protocol: https://developer.litprotocol.com
- Waku: https://docs.waku.org
- Irys: https://docs.irys.xyz
- CLOCK IN hackathon: https://solanamobile.com/blog/clock-in-the-solana-mobile-hackathon
- Spec câmera (018): `C:\Projetos\mvp-ideas\018-dashcam-android.md`
