# VaultCam

**47% dos motoristas que sofreram batida sem vídeo perderam a disputa com a seguradora.**  
VaultCam transforma qualquer celular Android em dashcam com prova on-chain imutável — gravação automática, lock no impacto, armazenamento permanente no Arweave.

> Seu celular já está no carro. Faltava só a prova que ninguém consegue deletar.

---

## O problema

Câmeras dashcam convencionais gravam em loop — o vídeo do acidente de semana passada não existe mais. Mesmo quando o vídeo existe, está em um cartão SD que pode sumir, ou em um servidor centralizado que pode ser desligado. Uma câmera que deleta é uma câmera que mente.

VaultCam resolve os três problemas de uma vez:

| Problema | Como VaultCam resolve |
|---|---|
| Vídeo deletado em loop | Lock automático ao detectar impacto — vídeo protegido antes de você reagir |
| Storage centralizado que some | Irys/Arweave — armazenamento permanente, sem servidor central |
| Você sozinho contra a seguradora | Rede P2P — dispositivos próximos viram testemunhas automáticas |

---

## A rede de evidências (DePIN)

VaultCam não é só um app — é uma rede descentralizada de câmeras físicas. Cada motorista é um nó.

```
Impacto detectado (G-force > 2,5G)
        ↓
Alerta BLE enviado (100m, offline)   +   Alerta Waku P2P (500m, online)
        ↓
Dispositivos próximos: vídeos bloqueados automaticamente
        ↓
Usuários contribuem vídeo → Hash on-chain + GPS + timestamp
        ↓
Prova de múltiplos ângulos, irrefutável, permanente
```

Quanto mais motoristas com VaultCam em uma cidade, mais robusta a rede. Cada câmera ligada é um voto pela transparência no trânsito.

---

## Para quem é

### Motorista individual
Você dirige todo dia. Uma batida sem prova pode custar R$8.000 em honorários e 14 meses de processo.  
VaultCam grava em background, detecta impacto automaticamente, e protege o vídeo antes de você perceber o que aconteceu.  
**Pay-per-video: $0,10 USDC ou SKR por vídeo protegido permanentemente.**

### Motorista de aplicativo (Uber, 99, iFood)
Você está em risco todo dia — clientes problemáticos, rotas perigosas, disputas sem testemunha.  
VaultCam contribui seus vídeos para a rede e você ganha **0,5 SKR por contribuição** — usa para desbloquear os próprios vídeos ou resgatar na wallet.

### Gestor de frota
Você precisa saber o que está acontecendo com 20, 50, 200 veículos agora.  
VaultCam transmite câmera ao vivo via LiveKit (sub-500ms). N câmeras simultâneas. Wallet-gated.  
**Billing em USDC por device ativo.**

---

## Como funciona

| O que você vê | O que acontece |
|---|---|
| "Câmera gravando" | CameraX loop — 10 vídeos locais, rotação automática |
| "Impacto detectado" | Acelerômetro G-force > 2,5G — automático, sem ação sua |
| "Vídeo protegido" | AES-256-GCM via Lit Protocol — chave nunca fica no aparelho |
| "Registro permanente" | Upload Irys → Arweave-backed — nem você consegue deletar |
| "Prova on-chain" | Hash SHA-256 + GPS + timestamp registrados na Solana |
| "Câmeras vizinhas alertadas" | BLE advertising (100m, offline) + Waku P2P (500m, online) |
| "Resgatar SKR" | SPL Token transfer treasury → sua wallet via MWA 2.0 |

---

## Custo

| Ação | VaultCam | Alternativa |
|---|---|---|
| Proteger 1 vídeo permanentemente | **$0,10** | Dashcam convencional: vídeo deletado em 7 dias |
| Contribuir vídeo como testemunho | **Grátis + ganha 0,5 SKR** | Nenhuma câmera convencional faz isso |
| Streaming frota ao vivo | **USDC/mês por device** | Soluções centralizadas: $50–200/device/mês |

`$0,10 = $0,02 Irys/Arweave + $0,001 Solana + $0,079 protocolo`

---

## O que está funcionando hoje

- [x] CameraX gravação em loop (10 vídeos locais, rotação automática)
- [x] Detecção de impacto via acelerômetro (G-force > 2,5G → auto-lock)
- [x] Lit Protocol AES-256-GCM (chave gerenciada pelos nós Lit, nunca no device)
- [x] Upload Irys via REST (Arweave-backed, imutável)
- [x] Mobile Wallet Adapter 2.0 — connect + signMessage + signAndSendTransactions
- [x] Pay-per-video USDC e SKR
- [x] Alerta BLE (100m, offline) — advertising + scan
- [x] Alerta Waku P2P (500m, online) — publish/subscribe
- [x] SKR rewards (0,5 SKR por contribuição à rede P2P)
- [x] LiveKit fleet streaming — câmera traseira ao vivo
- [x] GPS capturado em cada vídeo (FusedLocationProvider)
- [x] Sistema de créditos (3 créditos por contribuição)
- [x] Recuperação: owner grátis / terceiros pagam USDC ou SKR

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin + Jetpack Compose |
| Câmera | CameraX 1.3.4 |
| Wallet | Mobile Wallet Adapter 2.0.3 |
| Criptografia | Lit Protocol (REST) — AES-256-GCM |
| Storage permanente | Irys REST → Arweave |
| Token | SPL Token — USDC + SKR (`SKRbvo6Gf7GondiT3BbTfuRDPqLWei4j2Qy2NPGZhW3`) |
| Live streaming | LiveKit Android SDK 2.7.0 |
| P2P online | Waku Protocol (REST) |
| P2P offline | Bluetooth LE nativo Android |
| HTTP | OkHttp 4.12.0 + Moshi |
| Persistência | DataStore Preferences |
| Arquitetura | MVVM + StateFlow + Coroutines |

---

## Versões

| Versão | O que entregou |
|---|---|
| **v0.1** | Dashcam core — gravação, criptografia, Irys, MWA, pay-per-video |
| **v0.2** | Rede P2P — detecção de impacto, BLE, Waku, GPS, créditos |
| **v0.3** | Frota empresarial — LiveKit streaming ao vivo, wallet gating |
| **SKR** | Pay-per-video em SKR + 0,5 SKR reward por contribuição à rede |

---

## Submissão — CLOCK IN Hackathon

**Deadline:** 08/out/2026  
**Parceiro:** RadiantsDAO  
**Prize pool:** $125k USDC (top 10) + $10k SKR (integração criativa)

- [ ] APK release assinado
- [ ] Vídeo demo 3 min
- [ ] Pitch video 3 min
- [ ] Pitch deck 5 slides
- [ ] Treasury wallet com SKR real (claim de rewards end-to-end)
