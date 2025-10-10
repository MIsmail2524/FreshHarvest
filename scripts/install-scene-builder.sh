#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

VERSION="latest"
INSTALL_DIR="/opt/scenebuilder"

usage() {
    echo "Usage: $0 [-v VERSION]"
    echo "  -v VERSION   version like '25.0.0' or 'latest' (default)"
    exit 1
}

while getopts "v:h" opt; do
    case "$opt" in
        v) VERSION="$OPTARG" ;;
        h) usage ;;
        *) usage ;;
    esac
done

command_exists() { command -v "$1" >/dev/null 2>&1; }

if ! command_exists curl; then
    echo "Error: curl is required"
    exit 1
fi

# Fetch the HTML page for downloads
GLUON_PAGE="https://gluonhq.com/products/scene-builder/thanks/?dl="
BASE_DL="https://download2.gluonhq.com/scenebuilder"

# Based on version, build URL prefix
if [ "$VERSION" = "latest" ]; then
    # Get version number from the main page listing
    # e.g. "Scene Builder 25.0.0 was released" in HTML
    VERSION="$(curl -s https://gluonhq.com/products/scene-builder/ | grep -oP 'Scene Builder ([0-9]+\.[0-9]+\.[0-9]+)' | head -n1 | grep -oP '[0-9]+\.[0-9]+\.[0-9]+')"
    if [ -z "$VERSION" ]; then
        echo "Error: Could not detect latest version from Gluon page."
        exit 1
    fi
    echo "Detected latest version: $VERSION"
fi

# Build DEB URL
DEB_URL="${BASE_DL}/${VERSION}/install/linux/SceneBuilder-${VERSION}.deb"

echo "Trying URL:"
echo "  DEB: $DEB_URL"

DL_URL="$DEB_URL"
PKG_TYPE="deb"

# Test if it exists (HTTP HEAD)
if ! curl --head --silent --fail "$DEB_URL" >/dev/null; then
    echo "Error: Could not find the DEB download for version $VERSION"
    exit 1
fi

echo "Selected download: $DL_URL (type $PKG_TYPE)"

# Download file
TEMPFILE="$(mktemp)"
curl -L --fail -o "$TEMPFILE" "$DL_URL"

# Install as deb
echo "Installing .deb package..."
sudo dpkg -i "$TEMPFILE"
# Fix dependencies if needed
sudo apt-get install -f -y

rm -f "$TEMPFILE"

echo "SceneBuilder version $VERSION installed (type: $PKG_TYPE)."
