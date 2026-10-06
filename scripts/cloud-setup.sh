#!/usr/bin/env bash
# Setup do ambiente de nuvem (Claude Code na web) para compilar o VaultCam.
# Idempotente: pode rodar várias vezes; pula o que já está instalado.
# Uso: colar o conteúdo (ou `bash scripts/cloud-setup.sh`) no campo "Setup script" do ambiente.
set -euo pipefail

ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"
CMDLINE_TOOLS_ZIP="commandlinetools-linux-11076708_latest.zip"
PLATFORM="platforms;android-35"
BUILD_TOOLS="build-tools;35.0.0"

export ANDROID_HOME ANDROID_SDK_ROOT="$ANDROID_HOME"
SDKMANAGER="$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager"

# 1) Command-line tools
if [ ! -x "$SDKMANAGER" ]; then
  mkdir -p "$ANDROID_HOME/cmdline-tools"
  tmp="$(mktemp -d)"
  curl -fsSL --retry 4 --retry-delay 2 -o "$tmp/cmdline-tools.zip" \
    "https://dl.google.com/android/repository/$CMDLINE_TOOLS_ZIP"
  unzip -q "$tmp/cmdline-tools.zip" -d "$tmp"
  rm -rf "$ANDROID_HOME/cmdline-tools/latest"
  mv "$tmp/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest"
  rm -rf "$tmp"
fi

# 2) Licenças + pacotes (só baixa se faltar; saída do sdkmanager silenciada)
yes | "$SDKMANAGER" --licenses >/dev/null 2>&1 || true
[ -d "$ANDROID_HOME/platforms/android-35" ] || "$SDKMANAGER" --install "$PLATFORM" >/dev/null
[ -d "$ANDROID_HOME/build-tools/35.0.0" ]   || "$SDKMANAGER" --install "$BUILD_TOOLS" >/dev/null
[ -d "$ANDROID_HOME/platform-tools" ]       || "$SDKMANAGER" --install "platform-tools" >/dev/null

# 3) Variáveis para as sessões seguintes (shell + Gradle)
{
  echo "export ANDROID_HOME=$ANDROID_HOME"
  echo "export ANDROID_SDK_ROOT=$ANDROID_HOME"
  echo "export PATH=\$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"
} > "$HOME/.android-env.sh"
grep -qs ".android-env.sh" "$HOME/.bashrc" 2>/dev/null || echo '. "$HOME/.android-env.sh"' >> "$HOME/.bashrc"

# 4) Gradle: daemon com mais memória (AGP + Compose + Kotlin)
mkdir -p "$HOME/.gradle"
grep -qs "org.gradle.jvmargs" "$HOME/.gradle/gradle.properties" 2>/dev/null || \
  echo "org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8" >> "$HOME/.gradle/gradle.properties"

# 5) Aquece o cache do Gradle (wrapper + dependências + compilação) se o repo já estiver clonado.
#    O Maven Central devolve 429 em rajadas a partir do IP de saída da nuvem; cada tentativa
#    guarda no cache o que baixou, então repetimos (máx. WARMUP_TRIES). Nunca bloqueia a sessão.
#    Desligue com WARM_GRADLE=0.
WARM_GRADLE="${WARM_GRADLE:-1}"
WARMUP_TRIES="${WARMUP_TRIES:-6}"
REPO_DIR="${REPO_DIR:-$(git rev-parse --show-toplevel 2>/dev/null || true)}"
if [ "$WARM_GRADLE" = "1" ] && [ -n "$REPO_DIR" ] && [ -f "$REPO_DIR/gradlew" ]; then
  cd "$REPO_DIR"
  echo "sdk.dir=$ANDROID_HOME" > local.properties
  ok=0
  for i in $(seq 1 "$WARMUP_TRIES"); do
    if bash gradlew --console=plain --max-workers=1 --no-configuration-cache \
        :app:assembleDebug >/dev/null 2>&1; then ok=1; break; fi
    echo "warm-up Gradle: tentativa $i/$WARMUP_TRIES falhou (provável 429), repetindo..."
  done
  [ "$ok" = "1" ] && echo "Gradle aquecido (APK debug compilado)" \
    || echo "aviso: warm-up do Gradle não concluiu; o build manual continua funcionando"
fi

echo "Android SDK pronto em $ANDROID_HOME"
