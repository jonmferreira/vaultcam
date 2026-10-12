# SKR & Solana — Mecânica completa

Documento técnico dos fluxos on-chain do VaultCam: mint, pagamento, recompensa a
colaboradores, resgate, ciclo de vida da transação, tratamento de erros e premissas
antifraude.

**Código-fonte de referência:**
- `app/src/main/kotlin/com/vaultcam/service/SolanaTokenService.kt` — construção da transação SPL Token Transfer
- `app/src/main/kotlin/com/vaultcam/service/PaymentService.kt` — pay-per-video (USDC/SKR)
- `app/src/main/kotlin/com/vaultcam/service/RewardService.kt` — recompensas + resgate
- `app/src/main/kotlin/com/vaultcam/service/WalletService.kt` — Mobile Wallet Adapter 2.0

---

## 1. Token SKR

| Campo | Valor |
|---|---|
| Mint | `SKRbvo6Gf7GondiT3BbTfuRDPqLWei4j2Qy2NPGZhW3` |
| Decimais | 6 |
| Programa | SPL Token (`TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA`) |
| USDC (paridade) | `EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v` |

O SKR não é mintado pelo app — é um token existente. O VaultCam **usa** o SKR como
meio de pagamento (pay-per-video) e de recompensa (contribuição à rede). A treasury
distribui SKR aos colaboradores; os pagamentos dos usuários retornam à treasury.

---

## 2. Construção da transação (SolanaTokenService)

Toda operação on-chain é um **SPL Token Transfer** montado manualmente em bytes e
assinado pela wallet do usuário via MWA. O app nunca detém a chave privada.

```
buildTransferTransaction(fromAta, toAta, owner, tokenProgramId, amount)
  1. getRecentBlockhash()  → Solana JSON-RPC getLatestBlockhash (commitment: finalized)
  2. buildSplTransferMessage():
       instruction data = [tag=3 (Transfer), amount u64 LE]
       accounts = [owner(signer), fromAta, toAta, tokenProgram]
       header = (1 required sig, 0 readonly signed, 1 readonly unsigned)
       → message bytes prontos para assinar
```

- Montagem do message em formato wire Solana (header, contas, blockhash, instruções)
- `amount` em unidades base: `toBaseUnits(0.10, 6) = 100_000`
- Base58 encode/decode implementados localmente (sem dependência pesada)

**RPC:** mainnet-beta por padrão; devnet comentado no código para testes
(`SolanaTokenService.SOL_RPC`).

---

## 3. Fluxo de pagamento (pay-per-video) — PaymentService

Terceiros (ex: seguradora) pagam para acessar um vídeo que não é deles.

```
payForVideo(activity, videoId, currency = USDC | SKR)
  1. walletService.getConnectedAddress() ?: connect(activity)   → public key
  2. seleciona mint/decimais/amount conforme a moeda (0.10 USDC ou 0.10 SKR)
  3. deriveAta(owner, mint)     → ATA de origem (pagador)
     deriveAta(treasury, mint)  → ATA de destino (VaultCam treasury)
  4. buildTransferTransaction(...)  → bytes da transação
  5. walletAdapter.transact { signAndSendTransactions([txBytes]) }   (MWA 2.0)
  6. TransactionResult.Success → retorna assinatura (base58)
```

Owner recupera o próprio vídeo **de graça** (só assina uma mensagem, sem transferência).
Pagamento só é exigido de terceiros.

---

## 4. Recompensa a colaboradores — RewardService

Quem contribui um vídeo para um incidente da rede ganha SKR.

```
addReward(0.50)   // 0.5 SKR por contribuição
  → acumula em DataStore (pending_skr), offline-first
```

A recompensa é **creditada localmente** no momento da contribuição (UX instantânea) e
fica como saldo pendente até o resgate em lote. Isso evita uma transação on-chain por
contribuição (custo e latência).

---

## 5. Resgate (claim) — RewardService.claimRewards

```
claimRewards(activity)
  1. lê pending_skr do DataStore; aborta se <= 0
  2. public key do usuário via MWA
  3. deriveAta(treasury, SKR)  → origem
     deriveAta(user, SKR)      → destino
  4. buildTransferTransaction(owner = treasury, amount = pending)
  5. walletAdapter.transact { signAndSendTransactions([txBytes]) }
  6. Success → zera pending_skr no DataStore e retorna a assinatura
```

O saldo só é zerado **após** a confirmação de sucesso — se a transação falhar, o SKR
pendente é preservado para nova tentativa.

---

## 6. Ciclo de vida da transação

```
BUILD        getLatestBlockhash (RPC) → message bytes
   ↓
SIGN+SEND    MWA 2.0 transact { signAndSendTransactions }
             (wallet do usuário assina; app nunca vê a chave privada)
   ↓
RESULT       TransactionResult.Success → signatures[0] (base58)
   ↓
CONFIRM      assinatura verificável em explorer Solana
```

---

## 7. Tratamento de erros

`MobileWalletAdapter.transact` retorna um `TransactionResult` tratado explicitamente:

| Resultado | Tratamento |
|---|---|
| `Success` (sem assinatura) | lança "Transação sem assinatura" |
| `NoWalletFound` | "Wallet não encontrada" — orienta instalar wallet MWA |
| `Failure` | propaga `result.e?.message` para a UI (snackbar) |
| RPC não-2xx | `getRecentBlockhash` lança "RPC error: <code>" |
| Claim sem saldo | "Sem SKR para resgatar" antes de qualquer tx |

Toda operação é `runCatching` → `Result<T>`; a UI (ViewModels) expõe o erro em
`uiState.error` sem quebrar o app.

---

## 8. Premissas e antifraude (estado atual vs produção)

Transparência sobre o que é **demo** e o que é **produção** — para auditoria justa:

| Item | Estado atual (hackathon) | Produção |
|---|---|---|
| Treasury | Endereço stub (`VCam1111…`) | Multisig ou programa Solana dedicado |
| Derivação de ATA | SHA-256 simplificada dos seeds | `findProgramAddress` completo (ATA canônica) |
| Preço por vídeo | Constante (0.10) | Oracle de preço on-chain |
| Rewards | Creditado local (DataStore) + claim em lote | Programa Solana valida a contribuição antes de creditar |
| Antifraude de contribuição | — | Prova de que o vídeo é de um incidente real (hash on-chain + GPS + timestamp + assinatura do contribuidor) impede reivindicação falsa |
| Decimais SKR | Assumido 6 (confirmar on-chain) | Lido do mint account |

**Vetor antifraude central (desenhado):** cada incidente é registrado on-chain com
hash SHA-256 + GPS + timestamp. Uma contribuição só é recompensável se o vídeo
corresponder a um incidente registrado e a janela geográfica/temporal bater — o que
impede farmar SKR com vídeos aleatórios. A verificação on-chain desse vínculo é o
próximo passo pós-hackathon (hoje o crédito é otimista/local).
