# VaultCam — Checklist de Entrega
**Deadline:** 08/out/2026

---

## O que entregar

| Item | Formato | Status |
|---|---|---|
| APK funcional | `.apk` (não AAB) | [ ] |
| Repositório GitHub | público | [ ] |
| Vídeo demo | link YouTube/Vimeo | [ ] |
| Pitch deck | 5 slides | [ ] |
| Integração SKR | billing em SKR token | [ ] |

---

## Como gerar o APK

Android Studio → **Build → Build Bundle(s) / APK(s) → Build APK(s)**  
Arquivo gerado em: `app/build/outputs/apk/debug/app-debug.apk`

---

## O que os juízes do Colosseum avaliam

Fonte: [Perfecting Your Hackathon Submission](https://blog.colosseum.com/perfecting-your-hackathon-submission/)

- **Validação real** — evidência de conversa com usuários, feedback social, tração
- **Product-market fit** — quem paga, por quê, tamanho do mercado
- **Como usa Solana** — integração técnica genuína, não cosmética
- **Visão long-term** — o hackathon é o início da startup, não o fim
- **Equipe** — ideal 2–3 pessoas; solo deve mostrar experiência relevante

---

## Formato obrigatório (Colosseum padrão)

| Material | Limite | Conteúdo |
|---|---|---|
| **Pitch video** | 3 min | Equipe + problema + público-alvo + validação |
| **Demo técnico** | 3 min | Como foi construído + stack + como usa Solana |
| GitHub repo | público | código completo |
| Descrição curta | 1 parágrafo | nome, o que faz, para quem |

---

## Roteiro do vídeo demo (3 min)

1. **(0:00–0:20)** App abre — câmera traseira gravando em loop. Mostrar que é o próprio celular fixado no painel
2. **(0:20–0:45)** Galeria: 10 vídeos locais, limite visual claro
3. **(0:45–1:15)** Simular impacto (agitar device) → alerta G-force → vídeo bloqueado automaticamente
4. **(1:15–1:45)** Conectar wallet Solana (Phantom) via MWA → upload → criptografar (Lit) → Irys TX confirmado
5. **(1:45–2:30)** Fechar app, reabrir → tela de recuperação → assinar com wallet → vídeo reproduzido em memória
6. **(2:30–3:00)** Mostrar TX no explorador Irys/Arweave (prova imutável on-chain)

---

## Roteiro do pitch video (3 min)

1. **(0:00–0:30)** Problema: acidentes sem prova, câmeras dashcam caras ($200+), dados em servidores que somem
2. **(0:30–1:00)** Solução: VaultCam — seu celular já tem câmera, já está no carro, falta só a prova on-chain
3. **(1:00–1:45)** Como funciona: gravação contínua → impacto detectado → lock automático → Lit + Irys → prova eterna
4. **(1:45–2:15)** Mercado: 50M+ motoristas no Brasil, seguradoras pagam por prova de acidente, frotas empresariais
5. **(2:15–3:00)** Monetização: B2C pay-per-video (USDC), B2B frota (SKR/mês), rede P2P de evidências

---

## Pitch deck — 5 slides

1. **Problema** — acidentes sem prova, câmeras caras, dados centralizados que somem
2. **Solução** — VaultCam: seu celular vira dashcam + prova on-chain imutável
3. **Demo** — screenshots das 3 telas + diagrama Lit → Irys → Solana
4. **Monetização** — B2C (pay-per-video USDC), B2B frota (USDC/SKR/mês), créditos P2P
5. **Equipe + Roadmap** — v0.1 demo → v0.2 rede P2P → v0.3 frota ao vivo

---

## Referências de vencedores para assistir

- [Breakout Hackathon — playlist de demos](https://www.youtube.com/playlist?list=PL0gE4a2eG0_O2pCHA56NCd0mCMYNIhudl)
- [Cypherpunk Winners — Breakpoint 2025](https://www.youtube.com/watch?v=iSg1bBa3uRs)
- [Breakout Winners announcement](https://www.youtube.com/watch?v=wzp_HQm5t78)
- [Arena Colosseum — projetos para explorar](https://arena.colosseum.org/projects/explore/fanplay)

---

## SKR Prize ($10k extra)

Integrar SKR token como opção de pagamento no billing de frota (v0.3).  
Detalhes: verificar página do hackathon para endereço do contrato SKR.

---

## Links de submissão

- Hackathon: https://solanamobile.com/blog/clock-in-the-solana-mobile-hackathon
- Superteam BR: https://hackathon.superteam.com.br
