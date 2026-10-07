# Assinatura do APK (release) — guia de configuração

O APK **debug** não precisa de nada disso: o Android gera uma chave de debug sozinho.
Este guia é só para o APK **release assinado** (`assembleRelease`).

> **Regra de ouro:** o keystore (`.jks`) e as senhas **nunca** vão para o git. O repositório é
> público. O `.gitignore` já bloqueia `keystore/`, `*.jks`, `*.keystore` e `keystore.properties`.

## O que você precisa ter em mãos

| Item | Valor atual |
|---|---|
| Arquivo do keystore | `vaultcam-release.jks` |
| Alias da chave | `vaultcam` |
| Senha do keystore | está no `keystore.properties` que foi enviado a você |
| Senha da chave | a mesma do keystore |
| SHA-256 do certificado | `DF:6E:8E:C6:F5:3E:DA:E7:32:42:A0:E2:A1:99:7C:C1:D2:B8:CE:99:7A:34:D9:F3:31:75:FC:0D:FD:5C:2D:5A` |

**Faça backup** do `.jks` e do `keystore.properties` num lugar seguro (gerenciador de senhas ou
pen drive). Se perder a chave, você não consegue publicar atualizações assinadas pela mesma
identidade. O SHA-256 acima serve para conferir que é a mesma chave.

---

## Parte 1 — Assinar localmente (seu PC)

### 1. Colocar os arquivos no projeto

```
vaultcam/
├── keystore/
│   └── vaultcam-release.jks        <- copie o arquivo para cá
├── keystore.properties             <- crie este arquivo na RAIZ
├── app/
└── ...
```

### 2. Conteúdo do `keystore.properties` (raiz do projeto)

```properties
storeFile=keystore/vaultcam-release.jks
storePassword=COLE_A_SENHA_AQUI
keyAlias=vaultcam
keyPassword=COLE_A_SENHA_AQUI
```

- Use barras normais (`/`) no caminho, também no Windows.
- O `storeFile` é relativo à raiz do projeto.
- Sem espaços antes ou depois do `=`.

### 3. Compilar

Windows (PowerShell, na pasta do projeto):

```powershell
.\gradlew.bat assembleRelease
```

Linux/macOS:

```bash
./gradlew assembleRelease
```

O APK fica em `app\build\outputs\apk\release\app-release.apk`.

> Se o `keystore.properties` não existir (e as variáveis `VAULTCAM_*` também não), o build
> **continua funcionando**, mas o release sai **sem assinatura** e o Android não instala.

### 4. Conferir a assinatura

```powershell
# o apksigner fica no SDK: %LOCALAPPDATA%\Android\Sdk\build-tools\<versão>\apksigner.bat
apksigner.bat verify --verbose --print-certs app\build\outputs\apk\release\app-release.apk
```

Esperado: `Verifies`, `Verified using v2 scheme: true` e o SHA-256 do certificado igual ao da
tabela acima. Se aparecer `CN=Android Debug`, ficou com a chave de debug.

### 5. Instalar no aparelho

```powershell
adb install -r app\build\outputs\apk\release\app-release.apk
```

Ou copie o APK para o celular e abra o arquivo (permita "instalar apps desconhecidos").

---

## Parte 2 — Assinar no GitHub Actions

O workflow `.github/workflows/build-apk.yml` compila o release e publica o APK como artefato.
Ele lê **4 secrets** do repositório. Os nomes precisam ser exatamente estes:

| Nome do secret | O que colocar |
|---|---|
| `VAULTCAM_KEYSTORE_BASE64` | o `.jks` convertido em base64 (passo 1 abaixo) |
| `VAULTCAM_KEYSTORE_PASSWORD` | a senha do keystore |
| `VAULTCAM_KEY_ALIAS` | `vaultcam` |
| `VAULTCAM_KEY_PASSWORD` | a senha da chave (a mesma do keystore) |

### 1. Converter o keystore em base64 (copia para a área de transferência)

Windows (PowerShell):

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("keystore\vaultcam-release.jks")) | Set-Clipboard
```

macOS:

```bash
base64 -i keystore/vaultcam-release.jks | pbcopy
```

Linux:

```bash
base64 -w0 keystore/vaultcam-release.jks | xclip -selection clipboard
```

O texto copiado é uma linha longa sem quebras. Não precisa abrir nem editar.

### 2. Cadastrar os secrets no GitHub (cliques)

1. Abra o repositório: `https://github.com/jonmferreira/vaultcam`.
2. Clique na aba **Settings** (a última da barra superior do repositório).
3. No menu da esquerda, abra **Secrets and variables** e clique em **Actions**.
4. Na aba **Secrets**, clique no botão verde **New repository secret**.
5. Em **Name**, digite `VAULTCAM_KEYSTORE_BASE64`. Em **Secret**, cole o base64 do passo 1.
6. Clique em **Add secret**.
7. Repita os passos 4 a 6 para os outros três: `VAULTCAM_KEYSTORE_PASSWORD`,
   `VAULTCAM_KEY_ALIAS` e `VAULTCAM_KEY_PASSWORD`.

Ao final, a lista deve mostrar os 4 nomes. Depois de salvos, os valores **não podem mais ser
lidos**, só substituídos. Isso é normal.

Observações:
- Use **repository secrets** (não "Environment secrets"), a não ser que você tenha criado um
  Environment e ajustado o workflow.
- Secrets não ficam disponíveis para PRs vindos de forks.
- Nunca cole senha ou chave em issue, PR ou chat.

### 3. Rodar o workflow (cliques)

1. Abra a aba **Actions** do repositório.
2. No menu da esquerda, clique em **Build APK**.
3. Clique em **Run workflow** (botão à direita), escolha a branch e clique no botão verde
   **Run workflow**.
4. Espere o job ficar verde (alguns minutos). Clique na execução.
5. Role até a seção **Artifacts** no fim da página e clique em **vaultcam-release-apk** para
   baixar um `.zip` com o `app-release.apk`.
6. Extraia o zip e instale o APK no aparelho (`adb install -r app-release.apk`).

O workflow também verifica a assinatura com o `apksigner` e **falha** se os secrets não
estiverem cadastrados, para você não receber um APK sem assinatura achando que está assinado.

> O artefato expira em 14 dias. Baixe o APK que for usar na submissão.

---

## Problemas comuns

| Sintoma | Causa provável |
|---|---|
| Release sai como `app-release-unsigned.apk` | Faltou `keystore.properties` (local) ou os secrets (Actions). |
| `Keystore was tampered with, or password was incorrect` | Senha errada no `keystore.properties` ou no secret. |
| `Failed to read key vaultcam from store` | `keyAlias` ou `keyPassword` errados. |
| `keystore file ... not found` | Caminho do `storeFile` errado ou o `.jks` não está em `keystore/`. |
| Actions falha em "Decodificar keystore" | O secret `VAULTCAM_KEYSTORE_BASE64` está vazio ou com quebras de linha. Gere de novo. |
| Android recusa instalar por conflito | Já existe o app instalado com outra assinatura (ex.: debug). Desinstale antes. |
