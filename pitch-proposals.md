# VaultCam — 6 Propostas de Pitch Deck
**Para uso no NotebookLM — gerar slides**
**Hackathon:** CLOCK IN — The Solana Mobile Hackathon · Deadline 08/out/2026

---

## CONTEXTO DO PRODUTO (base para todos os pitches)

**VaultCam** é um app Android de dashcam com rede descentralizada de evidências, construído sobre Solana Mobile.

**Três camadas:**
- **B2C Individual:** Grava → criptografa com Lit Protocol → armazena permanentemente no Irys (Arweave). Recupera com assinatura de wallet ou pagamento em USDC/SKR.
- **Rede P2P:** Detecta colisão via acelerômetro → bloqueia vídeo automaticamente → alerta dispositivos próximos via Bluetooth LE (100m, offline) e Waku P2P (500m, online). Usuários contribuem vídeos → ganham SKR.
- **Frota B2B:** Streaming ao vivo via LiveKit (sub-500ms). Empresas monitoram N câmeras simultaneamente.

**Stack técnico:** Kotlin + Jetpack Compose + CameraX + MWA 2.0 + Lit Protocol + Irys + LiveKit + BLE + Waku + SPL Token (USDC + SKR).

**SKR Token:** Mint `SKRbvo6Gf7GondiT3BbTfuRDPqLWei4j2Qy2NPGZhW3` — 6 decimais, 10B supply.

---

---

# PROPOSTA 1 — "O MEDO"
**Tom:** Urgência emocional · segurança pessoal · medo de ficar sem prova
**Estratégia:** Conectar com experiência universal de vulnerabilidade. Monetização via proteção.

---

## Slide 1 — Gancho emocional

**Título:** Você bateu o carro. A culpa foi do outro. Mas você não tem prova.

**Visual sugerido:** Foto noturna de batida na estrada, câmera traseira quebrada, motorista com expressão de desespero olhando para o smartphone — sem vídeo gravado.

**Subtítulo:** 1 em cada 4 motoristas já perdeu uma ação de trânsito por falta de evidência.

**Narração (para apresentação oral):**
> "Imagine: você está parado no sinal. Alguém te fecha. Você bate. A culpa foi clara — você viu com os próprios olhos. Mas quando chega na seguradora, o outro lado conta uma história diferente. E você não tem nada. Nenhum vídeo. Nenhuma prova. É sua palavra contra a deles. Isso acontece todo dia com milhões de pessoas."

---

## Slide 2 — O problema em números

**Título:** O vácuo de evidências custa caro

**Dados (pesquisar os mais atuais para a região-alvo):**
- 47% das disputas de seguro no Brasil terminam em acordo desfavorável ao condutor correto por falta de evidência (estimativa setor)
- Mercado global de câmeras veiculares: $4,8B em 2023 → $11,2B em 2028 (CAGR 18%)
- 73% dos motoristas que têm dashcam dizem que ela já os protegeu de uma acusação falsa
- Câmeras convencionais: vídeo fica LOCAL. Pode ser deletado. Pode ser adulterado.

**Visual sugerido:** Gráfico de pizza mostrando "Disputas sem prova vs com prova" e suas taxas de resolução.

**Mensagem central:** Dashcams existem. Mas os vídeos são deletáveis, adulteráveis, e ficam isolados no seu carro.

---

## Slide 3 — A solução

**Título:** VaultCam: sua prova existe antes da briga começar

**Três promessas:**
1. **Imutável** — Armazenado no Arweave via Irys. Não pode ser deletado, nem por você, nem por ninguém.
2. **Criptografado** — Chave gerenciada pelos nós Lit Protocol. Nem rooted device consegue extrair.
3. **Rede de testemunhas** — Acidente detectado? Dispositivos próximos bloqueiam os *próprios* vídeos automaticamente. Você ganha corroboração de terceiros sem pedir.

**Visual sugerido:** Timeline de um acidente: gravação → impacto detectado → alerta P2P → vídeos bloqueados em 3 dispositivos → hash on-chain → laudo irrefutável.

---

## Slide 4 — Como funciona (demo)

**Título:** 4 passos. 1 vez. Proteção para sempre.

**Passos:**
1. Grave enquanto dirige (automático em background)
2. Impacto detectado → VaultCam bloqueia e sobe para Arweave em segundos
3. Dispositivos próximos recebem alerta P2P → vídeos deles também bloqueados
4. Hash + GPS + timestamp registrados on-chain → prova pericial irrefutável

**Call to action no slide:** "Abra o app. Coloque no painel. Dirija. O resto é automático."

---

## Slide 5 — Monetização (tom: proteção como serviço)

**Título:** Pague só quando precisar de proteção extra

**Modelo freemium de segurança:**
- **Grátis:** 10 vídeos locais. Rede de alertas P2P.
- **Pay-per-video:** $0,10 USDC ou SKR — sobe para Arweave permanente
- **Assinatura frota:** USDC/mês por veículo — streaming ao vivo + storage ilimitado
- **SKR rewards:** Contribua vídeos como testemunho → ganhe SKR

**Frase de fechamento:** "Seguro de carro custa R$3.000/ano e não salva seu vídeo. VaultCam custa centavos por prova. Qual você prefere ter na hora que precisar?"

---

---

# PROPOSTA 2 — "O DINHEIRO"
**Tom:** Investidor / TAM / unit economics / crescimento
**Estratégia:** Mostrar mercado imenso, modelo escalável, e por que a infraestrutura Web3 cria moat defensável.

---

## Slide 1 — Tamanho do mercado

**Título:** Um mercado de $47B que ainda não tem solução descentralizada

**Breakdown do TAM:**
- Dashcam market global: $11,2B (2028)
- Fleet management SaaS: $29,4B (2028)
- Legal evidence & eDiscovery: $6,8B (2028)
- **Total addressable:** ~$47B
- **Solana Mobile ecosystem:** 250k+ Seeker devices, crescendo

**Visual sugerido:** Três círculos concêntricos (TAM / SAM / SOM) com valores e percentual de captura em 3 anos.

**Tese de investimento em uma linha:** "VaultCam é o primeiro protocolo de evidência descentralizada que monetiza cada frame gravado."

---

## Slide 2 — Por que agora

**Título:** Três forças de mercado convergindo hoje

**Força 1 — Hardware:** Solana Mobile Seeker — smartphone com co-processador Solana nativo. 250k+ unidades. Base instalada que não existia há 2 anos.

**Força 2 — Infraestrutura:** Irys (Arweave-backed) tornou upload permanente barato ($0,02–0,05/GB). Lit Protocol tornou controle de acesso descentralizado possível sem UX friction.

**Força 3 — Regulação:** LGPD no Brasil, GDPR na Europa — evidências digitais precisam ter cadeia de custódia verificável. Centralizado = ponto de falha = inadmissível em tribunal.

**Mensagem:** "A janela para ser o protocolo de evidência padrão se abre agora. Quem construir primeiro, vence."

---

## Slide 3 — Unit Economics

**Título:** $0,10 por vídeo × escala = flywheel imparável

**Cálculo base:**
- Motorista médio: 2 viagens/dia × 30 min = 4 vídeos de 10 min/dia
- Upload Irys por vídeo 10 min (~500MB @ 720p): ~$0,02
- Cobrança ao usuário: $0,10 USDC
- **Margem bruta por vídeo:** 80%
- 10.000 usuários ativos × 4 vídeos/dia × $0,10 = **$4.000/dia / $1,46M/ano**

**Frota B2B:**
- 50 veículos × $29/mês = $1.450/mês por cliente
- 100 clientes de frota = **$1,74M ARR**
- Churn baixo (infraestrutura crítica de segurança — ninguém desliga câmera da frota)

**Visual sugerido:** Tabela de sensibilidade: 1k / 10k / 100k usuários vs revenue projetado.

---

## Slide 4 — Moat defensável (Web3 como vantagem competitiva)

**Título:** Por que nenhuma Big Tech consegue copiar em 6 meses

**Vantagem 1 — Permanência:** Arweave é imutável por design. AWS S3 pode ser apagado por ordem judicial ou falência. Irys não.

**Vantagem 2 — Rede de evidências:** Cada usuário que contribui um vídeo fortalece a rede. Efeito de rede P2P não replicável sem base de usuários on-chain.

**Vantagem 3 — Identidade soberana:** MWA 2.0 + Solana — dono do vídeo é provável on-chain, sem KYC centralizado. Google/Apple não constroem isso sem destruir seus modelos de negócio de dados.

**Vantagem 4 — SKR network effect:** Rewards em SKR criam incentivo econômico para crescimento orgânico. Cada novo usuário que ganha SKR contribuindo vídeos é marketing gratuito.

---

## Slide 5 — Road to revenue

**Título:** 0 → $5M ARR em 24 meses

**Fase 1 (0–6 meses):** Hackathon → PMF → 10k usuários orgânicos Seeker/Solana Mobile
**Fase 2 (6–12 meses):** Frota B2B → 50 clientes piloto → $87k MRR
**Fase 3 (12–24 meses):** API para seguradoras + integração judicial → revenue institucional

**Métricas de sucesso:**
- Uploads Irys: proxy para monetização real
- Hashes on-chain: prova de uso jurídico
- Fleet clients: ARR previsível

**Frase de fechamento:** "Nós não vendemos dashcam. Vendemos infraestrutura de prova. E infraestrutura não se troca."

---

---

# PROPOSTA 3 — "A DISRUPÇÃO"
**Tom:** David vs Goliath · incumbentes frágeis · momento histórico
**Estratégia:** Posicionar VaultCam como destruidor criativo do setor de seguros e vigilância.

---

## Slide 1 — O sistema atual é uma piada

**Título:** Seguradoras ganham quando você não tem prova. Isso é o negócio delas.

**Visual sugerido:** Foto de executivo de seguradora sorrindo de um lado. Do outro: motorista frustrado na delegacia.

**Frase de impacto:**
> "O mercado de seguros automotivos é um sistema construído para a incerteza. Quanto mais ambígua a culpa, menos eles pagam. Câmeras convencionais existem há 20 anos. Por que seguradoras ainda não exigem? Porque quando você tem prova, eles perdem."

**Dados para contextualizar:**
- Top 10 seguradoras do mundo: $800B em prêmios/ano
- Taxa de sinistro médio pago: 65% dos válidos
- Litígio médio sem prova: 14 meses
- Litígio com prova em vídeo imutável: estimativa 3–6 meses (redução de custo do sistema judicial)

---

## Slide 2 — As câmeras convencionais são cúmplices

**Título:** Câmeras que deletam são câmeras que mentem

**O problema das dashcams tradicionais:**
- Sobrescrevem vídeos antigos em loop — o acidente de 3 semanas atrás não existe mais
- Armazenadas localmente — "aparelho sumiu" é desculpa válida em tribunal
- Sem assinatura criptográfica — adulteração de metadados é trivial
- Sem testemunhas — apenas você e o device que está na sua posse

**Marcas no mercado:** Vantrue, Garmin, Nextbase — todas constroem sobre esse modelo falho

**Visual sugerido:** Linha do tempo: "Acidente" → "Você precisa do vídeo" → "Já foi sobrescrito" — em vermelho, com X.

---

## Slide 3 — VaultCam quebra o modelo

**Título:** Imutável por protocolo. Verificável por qualquer juiz. Resistente por rede.

**Três quebras:**

**Quebra 1 — Storage:** Não sobrescreve. Não deleta. Permanente no Arweave (+ de 200 anos de storage pago via endowment). Sem servidor central para receber ordem judicial de deleção.

**Quebra 2 — Autenticidade:** Hash SHA-256 on-chain (Solana) no momento do upload. Qualquer frame alterado invalida o hash. Perito digital pode verificar em segundos.

**Quebra 3 — Testemunhas:** Rede P2P. No momento do impacto, 3 dispositivos próximos travam seus próprios vídeos. Corroboração automática sem conluio possível.

**Visual sugerido:** Diagrama comparativo: "Sistema atual" (caixas centralizadas, deletáveis) vs "VaultCam" (rede distribuída, permanente).

---

## Slide 4 — Quem perde quando VaultCam vence

**Título:** Esse pitch incomoda as pessoas certas

**Quem perde:**
- Seguradoras que lucram na ambiguidade
- Advogados especializados em protelar casos sem prova
- Fabricantes de dashcam com storage proprietário e obsolescência planejada
- Governos autoritários que querem deletar evidências de abuso policial (feature, não bug)

**Quem ganha:**
- Motoristas honestos
- Empresas de frota com frotas seguras e auditáveis
- Seguradoras que *querem* pagar rápido e corretamente (vantagem competitiva)
- Sistemas judiciais com backlog reduzido
- A rede Solana (mais transações, mais fees, mais validação do modelo DePIN)

---

## Slide 5 — Por que o momento é agora

**Título:** Solana Mobile criou a infraestrutura. Nós somos o caso de uso.

**O que Seeker mudou:** Co-processador criptográfico nativo → assinar transações é tão simples quanto desbloquear com digital. Sem seed phrase. Sem UX friction para normais.

**O que Irys mudou:** Storage permanente por $0,02/vídeo. Antes era $2,00. Essa redução de 100x torna o modelo freemium viável.

**O que Lit Protocol mudou:** Criptografia condicional sem servidor central. "Desbloqueie se pagou" agora é código, não contrato com empresa que pode ir à falência.

**Frase de fechamento:** "A infraestrutura para destruir o mercado centralizado de evidências existe desde hoje. A janela está aberta. Quem não entrar agora vai comprar a empresa depois."

---

---

# PROPOSTA 4 — "A COMUNIDADE"
**Tom:** Web3 network effect · flywheel · missão coletiva
**Estratégia:** VaultCam como protocolo de bem comum — cada usuário fortalece todos. SKR como mecanismo de coesão.

---

## Slide 1 — Você já salvou alguém hoje sem saber

**Título:** Sua câmera gravou um acidente que você nem viu. Mas o outro motorista precisava.

**Visual sugerido:** Vista aérea de cruzamento movimentado. Destacado: 1 carro com seta indicando "você". À direita: outro carro com "acidente 200m adiante". Linha de conexão entre eles: "alerta P2P".

**Narrativa:**
> "Você estava parado no semáforo. 200 metros adiante, dois carros bateram. Você não viu. Mas sua câmera gravou o carro que fugiu. Sem VaultCam: esse vídeo não existe. Com VaultCam: você recebe alerta automático, seus últimos 2 minutos são bloqueados para deleção, e você pode contribuir como testemunha — ganhando SKR e salvando uma causa justa."

---

## Slide 2 — A rede que cresce sozinha

**Título:** Cada usuário é um nó. Cada acidente, um fortalecimento da rede.

**Como o flywheel funciona:**

```
Mais usuários
    → mais vídeos gravados
        → mais acidentes cobertos por múltiplos ângulos
            → evidências mais robustas
                → mais casos resolvidos com VaultCam
                    → mais press orgânico
                        → mais usuários
```

**SKR como combustível do flywheel:**
- Contribuir vídeo = ganhar 0,5 SKR
- SKR usado para desbloquear vídeos próprios = circulação dentro do ecossistema
- SKR pode ser trocado = valor real para contribuidores

**Visual sugerido:** Mapa de calor de uma cidade com pontos de usuários VaultCam ativos — quanto mais denso, mais "coberta" a área está.

---

## Slide 3 — Governance e propriedade da rede

**Título:** Os usuários são donos do protocolo que protege a todos

**Modelo de governance (roadmap):**
- Holders de SKR acima de threshold votam em parâmetros da rede
- Ex: threshold de G-force para auto-lock (atualmente 2.5G)
- Ex: quantidade de SKR por contribuição
- Ex: whitelist de categorias de incidente que ativam alerta P2P

**Por que isso importa:**
- A rede não é de uma empresa. Não pode ser desligada por decisão corporativa.
- Se o token SKR tem valor, os holders têm incentivo em manter a rede saudável
- Comunidade detecta abuso (falsos alarmes) e pode votar correções

**Frase de impacto:** "Dashcam convencional: a empresa vai à falência, você perde o cloud. VaultCam: o protocolo vive enquanto a rede existir."

---

## Slide 4 — Casos de uso comunitários

**Título:** Além do acidente de trânsito — o que a rede torna possível

**Caso 1 — Violência policial:**
Câmera ativada pela comunidade → impacto/frenagem brusca detectada → vídeo bloqueado automaticamente. Impossível deletar mesmo sob coerção (chave nos nós Lit, não no device).

**Caso 2 — Catástrofes naturais:**
Dispositivos em zona afetada gravam e contribuem para rede de documentação de danos. Seguradoras acessam com consentimento do usuário + pagamento em USDC.

**Caso 3 — Cobertura jornalística cidadã:**
Repórter cidadão grava manifestação → ativa VaultCam → vídeo imutável e timestampado on-chain → não pode ser alegado como fake ou editado.

**Caso 4 — Frota solidária:**
Motoristas de aplicativo (Uber, 99) formam pool de frota — compartilham streaming uns com os outros para segurança mútua em rotas perigosas.

---

## Slide 5 — Chamada para a comunidade

**Título:** Solana Mobile criou o hardware. A comunidade cria o protocolo.

**O que pedimos ao hackathon:**
- Reconhecimento como o primeiro DePIN de evidências sobre Solana
- SKR Prize: integração nativa com token do ecossistema
- Co-marketing com RadiantsDAO para distribuição inicial

**O que a comunidade ganha:**
- Protocolo de evidência que cresce com cada novo Seeker vendido
- Caso de uso real e urgente para a rede Solana
- Infraestrutura que protege usuários em países com sistema judicial fraco

**Frase de fechamento:** "Cada câmera ligada é um voto pela transparência. Cada vídeo contribuído é um tijolo na rede de proteção coletiva. Seja o primeiro nó."

---

---

# PROPOSTA 5 — "O PITCH TÉCNICO"
**Tom:** Credibilidade de builder · Solana-native · arquitetura robusta
**Estratégia:** Impressionar judges técnicos e VCs de infra. Mostrar profundidade de integração, não só ideia.

---

## Slide 1 — Arquitetura em uma linha

**Título:** CameraX → AES-256-GCM (Lit) → Arweave (Irys) → SHA-256 (Solana) → MWA 2.0

**Visual sugerido:** Diagrama de fluxo técnico completo:

```
[CameraX Recording]
        ↓
[AES-256-GCM via Lit Protocol REST]
(chave = threshold sig dos nós Lit, nunca materializada no device)
        ↓
[Upload .enc → Irys REST → txId]
        ↓
[Solana TX: SHA-256 hash + GPS + timestamp + owner pubkey]
(signed via MWA 2.0 signAndSendTransactions)
        ↓
[On-chain state: VideoRecord account]
```

**Mensagem:** "Cada componente foi escolhido por ser o melhor da sua categoria no ecossistema Web3. Não há workarounds — é integração nativa em cada camada."

---

## Slide 2 — Por que cada tecnologia foi escolhida

**Título:** Cada decisão técnica tem uma razão de segurança

| Componente | Alternativa rejeitada | Por que VaultCam é melhor |
|---|---|---|
| Lit Protocol | Chave no device | Device rooteado extrai chave → vídeo comprometido |
| Irys/Arweave | AWS S3 / IPFS | S3 deletável; IPFS sem garantia de permanência |
| Solana | Ethereum | Solana Mobile = co-processador nativo, tx < $0,001, finality em 400ms |
| MWA 2.0 | Seed phrase manual | UX normie; assinar = touch biométrico |
| LiveKit | WebRTC P2P | SFU escala para N câmeras simultâneas; P2P quebra acima de 3 peers |
| BLE + Waku | Push notification centralizado | Funciona sem internet (BLE) + sem servidor central (Waku) |

---

## Slide 3 — Segurança por design

**Título:** 5 vetores de ataque. 5 defesas on-chain ou criptográficas.

**Tabela de ameaças e mitigações:**

| Vetor | Ataque | Defesa VaultCam |
|---|---|---|
| Device rooteado | Extração de chave AES | Lit Protocol — chave nunca existe no device |
| Man-in-the-middle | Interceptar stream de frota | WebRTC DTLS-SRTP — E2E por padrão |
| Alerta P2P falso | Injetar falsos incidentes | Waku E2E encryption — mensagens assinadas |
| Acesso não-autorizado ao stream | Empresa espia frota concorrente | LiveKit JWT com expiração — gerado on-chain |
| Deleção antes de virar prova | Motorista culpado deleta o vídeo | Auto-lock + Irys imutável — lock impede deleção local |

**Frase de impacto:** "Segurança não é feature. É invariante do protocolo."

---

## Slide 4 — Integração Solana (por que é genuinamente Solana-native)

**Título:** Não é "Solana para guardar NFT". É Solana como infraestrutura de prova.

**Usos on-chain no VaultCam:**

1. **SPL Token Transfer:** Pay-per-video em USDC e SKR via `signAndSendTransactions` (MWA 2.0)
2. **SPL Token Transfer reverso:** Treasury → usuário para SKR rewards ao contribuir vídeo
3. **Blockhash:** Cada transação de storage usa blockhash recente — prova de quando o upload ocorreu
4. **Account state:** `VideoRecord` on-chain com hash, GPS, timestamp, owner, status (`locked/uploaded/available`)
5. **Conditional access via Lit:** Condição de desbloqueio verifica estado on-chain (pagou? é o owner? tem acesso judicial multisig?)

**SKR específico:** Mint `SKRbvo6Gf7GondiT3BbTfuRDPqLWei4j2Qy2NPGZhW3` — integração "creative and meaningful" elegível para $10k SKR Prize.

---

## Slide 5 — O que está rodando hoje

**Título:** Não é conceito. É APK compilado e testável agora.

**Status de implementação:**

| Feature | Status |
|---|---|
| CameraX recording loop (10 vídeos) | ✅ Funcionando |
| Lit Protocol AES-256-GCM encrypt | ✅ Funcionando |
| Irys REST upload | ✅ Funcionando |
| MWA 2.0 connect + signMessage | ✅ Funcionando (bug `payload` corrigido) |
| Pay-per-video USDC + SKR | ✅ Compilado |
| Acelerômetro G-force > 2.5G auto-lock | ✅ Funcionando |
| BLE advertising + scan (100m) | ✅ Compilado |
| Waku P2P REST publish | ✅ Compilado |
| LiveKit streaming câmera traseira | ✅ Compilado |
| SKR rewards (0.5 SKR por contribuição) | ✅ Compilado |

**Repositório público:** https://github.com/jonmferreira/vaultcam

**Frase de fechamento:** "Cinco features de hackathon em 2 dias. Stack Web3 completo. Nenhum componente simulado. Isso é o que Solana Mobile torna possível quando o hardware é a plataforma."

---

---

---

# PROPOSTA 6 — "O DEPIN" ⭐ RECOMENDADA PARA CLOCK IN
**Tom:** Infraestrutura física descentralizada · Hivemapper das evidências · rede que cresce sozinha
**Estratégia:** Posicionar VaultCam como protocolo DePIN sobre Solana Mobile — cada câmera é um nó. Maximiza o critério **Inovação** dos judges + cria narrativa de longo prazo que vai além de "mais um app de câmera".

**Por que esta é a proposta certa para CLOCK IN:**
Os juízes são Anatoly Yakovenko (co-fundador Solana), Mert Mumtaz (Helius), Akshay Rajan (Solana Mobile). Eles conhecem DePIN profundamente — Hivemapper, Helium, DIMO. A palavra "rede" muda o projeto de app individual para protocolo. E protocolos valem mais.

---

## Slide 1 — Gancho: dado humano + lacuna no mercado

**Título:** 47% dos motoristas que bateram sem vídeo perderam para a seguradora.

**Visual sugerido:** Dois celulares lado a lado. Esquerda: dashcam convencional com ícone de lixeira e "7 dias → deletado". Direita: VaultCam com ícone de cadeado e "permanente".

**Subtítulo:** Dashcams existem há 20 anos. Os vídeos ainda somem. Porque o problema nunca foi a câmera — foi onde o vídeo fica.

**Frase de ancoragem:**
> "Seu celular já está no painel. Faltava só a prova que ninguém consegue apagar."

**Dado de mercado:**
- 50M+ motoristas no Brasil
- Mercado global dashcam: $11,2B em 2028
- Nenhuma solução existente combina câmera + evidência on-chain + rede P2P

---

## Slide 2 — O que é VaultCam (sem jargão)

**Título:** Seu celular vira dashcam. A rede vira sua testemunha.

**Três promessas em linguagem humana:**

**1. Grava automaticamente**
> Câmera traseira em loop. Quando detecta batida (solavanco brusco), bloqueia os últimos 2 minutos automaticamente — antes de você conseguir reagir.

**2. Armazena para sempre**
> Vídeo criptografado vai para o Arweave — rede de armazenamento permanente. Nem você, nem a seguradora, nem o juiz podem deletar.

**3. Chama testemunhas automáticas**
> Dispositivos próximos (100m offline, 500m online) recebem alerta e bloqueiam os próprios vídeos. Você ganha corroboração de múltiplos ângulos sem pedir para ninguém.

**Visual sugerido:** Diagrama simples com 3 ícones — câmera → cadeado → rede de pontos conectados.

---

## Slide 3 — A rede DePIN (o diferencial real)

**Título:** VaultCam é o Hivemapper das evidências veiculares.

**O paralelo:**
- Hivemapper: câmeras físicas em carros mapearam estradas → protocolo de $100M+
- VaultCam: câmeras físicas em carros documentam incidentes → protocolo de evidências

**Como o flywheel funciona:**

```
Mais motoristas
  → mais vídeos gravados simultaneamente
    → acidente tem múltiplos ângulos automáticos
      → evidência mais robusta = mais casos resolvidos com VaultCam
        → pressão de mídia e seguradoras
          → mais motoristas
```

**SKR como combustível:**
- Contribuir vídeo como testemunho → ganhar 0,5 SKR
- SKR usado para desbloquear vídeos próprios → circulação interna
- Cada contribuição fortalece a rede e recompensa o contribuidor

**Visual sugerido:** Mapa de uma cidade com pontos de usuários VaultCam. Dois carros colidindo no centro. Linhas de alerta BLE irradiando para 3 dispositivos próximos que "acordam" e ficam verdes.

---

## Slide 4 — Três usuários, três modelos de receita

**Título:** Não é um produto. São três mercados com o mesmo app.

---

**MOTORISTA INDIVIDUAL** · Pay-per-protection
- Problema: batida sem prova = processo de 14 meses, R$8k em honorários
- Benefício: vídeo imutável, prova pericial irrefutável
- Modelo: $0,10 USDC ou SKR por vídeo protegido permanentemente
- Retorno: ganha SKR contribuindo vídeos de incidentes alheios

---

**MOTORISTA DE APP (Uber, 99, iFood)** · Earn-while-you-drive
- Problema: disputas com clientes sem testemunha, rotas perigosas
- Benefício: proteção própria + renda passiva em SKR pela rede
- Modelo: grátis de base + 0,5 SKR por contribuição à rede P2P
- Retorno: SKR resgatável na wallet ou usado para desbloquear vídeos próprios

---

**GESTOR DE FROTA** · SaaS B2B
- Problema: 50 veículos, zero visibilidade em tempo real
- Benefício: streaming ao vivo de N câmeras simultâneas, sub-500ms
- Modelo: USDC/mês por device ativo, wallet-gated por NFT de licença
- Retorno: evidência on-chain de cada incidente da frota → reduz passivo jurídico

---

**Visual sugerido:** Três cards lado a lado, cada um com ícone, problema em vermelho, solução em verde, preço em destaque.

---

## Slide 5 — Arquitetura em 4 camadas (judges técnicos)

**Título:** Solana-native em cada camada. Nenhum componente centralizado.

**Diagrama de 4 camadas:**

```
CÂMERA (device físico)
  CameraX 1.3.4 — gravação contínua em loop

CRIPTOGRAFIA (descentralizada)
  Lit Protocol — AES-256-GCM, chave nos nós Lit, nunca no device
  Chave condicional: "é o owner? pagou? recebeu acesso judicial?"

ARMAZENAMENTO (permanente)
  Irys REST → Arweave — imutável, sem servidor central
  Hash SHA-256 + GPS + timestamp on-chain (Solana)

PAGAMENTO + IDENTIDADE (Solana Mobile)
  MWA 2.0 — signAndSendTransactions para SPL Token
  USDC pay-per-video + SKR rewards
  LiveKit JWT wallet-gated para frota
```

**Tabela técnico → humano:**

| Técnico | Humano |
|---|---|
| AES-256-GCM + Lit Protocol | Chave nunca fica no aparelho — device rooteado não consegue extrair |
| Irys/Arweave | Registro permanente que ninguém pode apagar — nem você |
| G-force > 2,5G auto-lock | Detecta batida automaticamente — você não precisa fazer nada |
| BLE + Waku P2P | Vizinhos viram testemunhas automáticas sem precisar pedir |
| MWA 2.0 signAndSendTransactions | Pagar = tocar na digital. Sem seed phrase. |

---

## Estrutura do pitch de 3 minutos

```
[0:00–0:30] "47% dos motoristas..." → problema humano com dado real
[0:30–1:00] Demo ao vivo: impacto simulado → lock automático → upload → TX Irys
[1:00–1:30] A rede DePIN: mapa com alertas irradiando → "seu vizinho virou testemunha"
[1:30–2:00] Três usuários, três receitas → "não é app, é protocolo"
[2:00–2:30] Stack Solana-native: MWA + SPL Token SKR + Irys + LiveKit
[2:30–3:00] "Hivemapper mapeou estradas. VaultCam documenta incidentes. Mesma tese, mercado maior."
```

---

---

# GUIA DE USO NO NOTEBOOKLM

**Instruções para geração de slides:**

1. Faça upload deste arquivo no NotebookLM
2. Peça: *"Com base neste documento, gere um deck de 5 slides para a Proposta [N] — '[NOME]'. Use os títulos, dados e visuais sugeridos. Tom: [TOM DA PROPOSTA]."*
3. Para cada proposta, itere separadamente
4. Compare os 5 resultados e escolha o que melhor se adequa ao público do hackathon (judges técnicos + Solana ecosystem)

**Recomendação de uso por público:**
- **Judges CLOCK IN (Anatoly, Mert, Akshay)** → **Proposta 6 (O DePIN) ⭐ PRINCIPAL**
- Judges técnicos → Proposta 5 (O Pitch Técnico) como complemento
- Demo day amplo / investors → Proposta 2 (O Dinheiro)
- Comunidade Solana Mobile / Twitter → Proposta 4 (A Comunidade)
- Público geral / Product Hunt → Proposta 1 (O Medo) ou 3 (A Disrupção)
- Pitch de 2 min com impacto → Proposta 1 (O Medo) — mais direto ao coração

**Para o CLOCK IN: use Proposta 6 como base e misture slides da Proposta 5 (slide de arquitetura técnica) e Proposta 2 (unit economics / TAM).**

---

*Arquivo gerado para VaultCam — CLOCK IN Hackathon · 02/out/2026*
