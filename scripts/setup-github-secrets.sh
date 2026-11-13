#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

KEYSTORE_PATH="${PROJECT_ROOT}/android-release.keystore"
ENV_FILE="${PROJECT_ROOT}/android-release-signing.env"
REPO="${REPO:-ahparhizgar/pockety}"

if [ ! -f "$KEYSTORE_PATH" ]; then
  echo "Keystore not found at $KEYSTORE_PATH"
  exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
  echo "Credentials file not found at $ENV_FILE"
  exit 1
fi

# Load variables from env file
KEYSTORE_PASS=$(grep '^RELEASE_KEYSTORE_PASSWORD=' "$ENV_FILE" | cut -d= -f2-)
ALIAS=$(grep '^RELEASE_KEY_ALIAS=' "$ENV_FILE" | cut -d= -f2-)
KEY_PASS=$(grep '^RELEASE_KEY_PASSWORD=' "$ENV_FILE" | cut -d= -f2-)

if [ -z "$KEYSTORE_PASS" ] || [ -z "$ALIAS" ] || [ -z "$KEY_PASS" ]; then
  echo "Missing required credentials in $ENV_FILE"
  exit 1
fi

# Encode keystore to base64 (single line without wrapping)
KEYSTORE_BASE64=$(base64 -w 0 "$KEYSTORE_PATH" 2>/dev/null || base64 "$KEYSTORE_PATH" | tr -d '\n')

echo "Setting GitHub secrets for repository $REPO..."

# Set repository secrets via gh CLI
printf "%s" "$KEYSTORE_BASE64" | gh secret set ANDROID_KEYSTORE_BASE64 --repo "$REPO"
printf "%s" "$KEYSTORE_PASS" | gh secret set KEYSTORE_PASSWORD --repo "$REPO"
printf "%s" "$ALIAS" | gh secret set KEY_ALIAS --repo "$REPO"
printf "%s" "$KEY_PASS" | gh secret set KEY_PASSWORD --repo "$REPO"

echo "GitHub repository secrets set successfully."
