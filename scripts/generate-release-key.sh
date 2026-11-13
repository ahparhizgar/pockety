#!/usr/bin/env bash
set -euo pipefail

# Scripts directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

KEYSTORE_PATH="${PROJECT_ROOT}/android-release.keystore"
ENV_FILE="${PROJECT_ROOT}/android-release-signing.env"
KEY_ALIAS="pockety-release"

if [ -f "$KEYSTORE_PATH" ]; then
  echo "Keystore already exists at $KEYSTORE_PATH"
  exit 1
fi

if [ -f "$ENV_FILE" ]; then
  echo "Credentials file already exists at $ENV_FILE"
  exit 1
fi

# Generate strong random passwords
PASSWORD=$(openssl rand -base64 24)

# Create credentials file with strict permissions (chmod 600)
touch "$ENV_FILE"
chmod 600 "$ENV_FILE"

cat <<EOF > "$ENV_FILE"
RELEASE_KEYSTORE_PATH=android-release.keystore
RELEASE_KEYSTORE_PASSWORD=${PASSWORD}
RELEASE_KEY_ALIAS=${KEY_ALIAS}
RELEASE_KEY_PASSWORD=${PASSWORD}
EOF

# Generate the keystore using keytool
keytool -genkeypair \
  -v \
  -keystore "$KEYSTORE_PATH" \
  -alias "$KEY_ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass "$PASSWORD" \
  -keypass "$PASSWORD" \
  -dname "CN=Pockety, OU=Mobile, O=Pockety, L=Unknown, ST=Unknown, C=US" \
  > /dev/null 2>&1

echo "Generated release keystore at: $KEYSTORE_PATH"
echo "Saved signing credentials to: $ENV_FILE (mode 600)"
