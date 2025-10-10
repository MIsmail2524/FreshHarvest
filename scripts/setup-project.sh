#!/usr/bin/env bash
set -euo pipefail

# =============================================================================
# FreshHarvest Project Setup Script
# 
# This script sets up the development environment for the FreshHarvest project
# by installing JDK 21, Gradle, and JavaFX SDK on Ubuntu systems.
# =============================================================================

# --- Configuration ---
readonly JAVAFX_VERSION="21.0.5"
readonly JAVAFX_BASE_URL="https://download2.gluonhq.com/openjfx/${JAVAFX_VERSION}/openjfx-${JAVAFX_VERSION}_linux-x64_bin-sdk.zip"
readonly TARGET_DIR="libs/javafx"
readonly GRADLE_VERSION="9.1.0"
readonly GRADLE_INSTALL_DIR="/opt/gradle"

# --- Global Variables ---
TEMP_DIR=""
EXTRACT_DIR=""
ZIP_FILE=""
KEEP_CACHE="${KEEP_CACHE:-false}"

# --- Utility Functions ---

# Print informational message
log_info() {
    echo "ℹ️  $1"
}

# Print success message
log_success() {
    echo "✅ $1"
}

# Print warning message
log_warning() {
    echo "⚠️  $1"
}

# Print error message and exit
error_exit() {
    echo "❌ Error: $1" >&2
    cleanup
    exit 1
}

# Clean up temporary resources
cleanup() {
    log_info "Cleaning up temporary resources..."
    [ -n "$TEMP_DIR" ] && rm -rf "$TEMP_DIR"
    [ -n "$EXTRACT_DIR" ] && rm -rf "$EXTRACT_DIR"
    
    if [ -f "$ZIP_FILE" ] && [ "$KEEP_CACHE" != "true" ]; then
        rm -f "$ZIP_FILE"
    fi
}

# Check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Update apt cache
update_apt_cache() {
    log_info "Updating package cache..."
    sudo apt-get update -y >/dev/null 2>&1 || log_warning "Failed to update package cache"
}

# Install package via apt
install_package() {
    local package="$1"
    log_info "Installing $package..."
    sudo DEBIAN_FRONTEND=noninteractive apt-get install -y "$package" >/dev/null 2>&1
}

# Download file with progress
download_file() {
    local url="$1"
    local output="$2"
    
    if command_exists wget; then
        log_info "Downloading with wget..."
        wget --show-progress -O "$output" "$url" || return 1
    elif command_exists curl; then
        log_info "Downloading with curl..."
        curl -L --progress-bar -o "$output" "$url" || return 1
    else
        error_exit "Neither wget nor curl is available for downloading"
    fi
}

# --- Installation Functions ---

# Check if Java 21 is installed and working
is_java21_installed() {
    if ! command_exists java; then
        return 1
    fi
    
    # Check Java version contains "21"
    java -version 2>&1 | grep -q "\b21\b"
}



# Install JDK 21 via apt
install_java21() {
    log_info "Installing OpenJDK 21..."
    
    if ! command_exists apt-get; then
        error_exit "This script requires apt-get (Ubuntu/Debian). Please install JDK 21 manually."
    fi
    
    update_apt_cache
    
    # Install JDK packages
    if install_package "openjdk-21-jdk" && is_java21_installed; then
        log_success "OpenJDK 21 installed successfully"
        return 0
    fi
    
    error_exit "Failed to install OpenJDK 21. Please install it manually and re-run this script."
}

# Ensure Java 21 is available
ensure_java21() {
    if is_java21_installed; then
        log_success "OpenJDK 21 is already installed"
        return 0
    fi
    
    install_java21
}

# Check if compatible Gradle version is installed
is_gradle_version_installed() {
    if ! command_exists gradle; then
        return 1
    fi
    
    local installed_version
    installed_version=$(gradle --version 2>/dev/null | grep "Gradle" | head -1 | awk '{print $2}')
    
    if [ -n "$installed_version" ]; then
        log_info "Found Gradle version: $installed_version"
        # Accept any Gradle version 8.x or higher
        if [[ "$installed_version" =~ ^[8-9]\. ]] || [[ "$installed_version" =~ ^[1-9][0-9]\. ]]; then
            return 0
        else
            log_warning "Gradle version $installed_version found, but version 8.0+ is recommended"
            return 1
        fi
    fi
    
    return 1
}

# Install Gradle from official distribution
install_gradle() {
    local gradle_url="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
    local zip_dest="/tmp/gradle-${GRADLE_VERSION}-bin.zip"
    local gradle_dir="${GRADLE_INSTALL_DIR}/gradle-${GRADLE_VERSION}"
    
    log_info "Installing Gradle ${GRADLE_VERSION}..."
    
    if ! command_exists apt-get; then
        error_exit "This script requires apt-get (Ubuntu/Debian). Please install Gradle manually."
    fi
    
    # Ensure required tools are available
    update_apt_cache
    install_package "unzip"
    if ! command_exists wget && ! command_exists curl; then
        install_package "wget"
    fi
    
    # Download Gradle
    log_info "Downloading Gradle ${GRADLE_VERSION}..."
    if ! download_file "$gradle_url" "$zip_dest"; then
        error_exit "Failed to download Gradle"
    fi
    
    # Extract Gradle
    log_info "Installing Gradle to ${GRADLE_INSTALL_DIR}..."
    sudo mkdir -p "$GRADLE_INSTALL_DIR"
    
    if ! sudo unzip -q "$zip_dest" -d "$GRADLE_INSTALL_DIR"; then
        error_exit "Failed to extract Gradle"
    fi
    
    # Verify extraction
    if [ ! -d "$gradle_dir" ]; then
        error_exit "Gradle installation directory not found after extraction"
    fi
    
    # Create system-wide environment setup
    log_info "Configuring system PATH for Gradle..."
    sudo tee /etc/profile.d/gradle.sh >/dev/null <<EOF
#!/usr/bin/env bash
export GRADLE_HOME="$gradle_dir"
export PATH="\$GRADLE_HOME/bin:\$PATH"
EOF
    sudo chmod +x /etc/profile.d/gradle.sh
    
    # Create convenient symlink
    if [ -x "$gradle_dir/bin/gradle" ]; then
        sudo ln -sf "$gradle_dir/bin/gradle" /usr/local/bin/gradle
    fi
    
    # Refresh current shell
    export GRADLE_HOME="$gradle_dir"
    export PATH="$GRADLE_HOME/bin:$PATH"
    hash -r 2>/dev/null || true
    
    # Cleanup download
    rm -f "$zip_dest"
    
    # Final verification
    if ! is_gradle_version_installed; then
        error_exit "Failed to verify Gradle installation. Please install Gradle manually."
    fi
    
    log_success "Gradle ${GRADLE_VERSION} installed successfully"
}

# Ensure Gradle is available
ensure_gradle() {
    if is_gradle_version_installed; then
        log_success "Compatible Gradle version is already installed"
        return 0
    fi
    
    install_gradle
}

# Check if JavaFX is properly installed
is_javafx_installed() {
    [ -d "$TARGET_DIR" ] && \
    [ -f "$TARGET_DIR/lib/javafx.base.jar" ] && \
    [ -f "$TARGET_DIR/lib/javafx.controls.jar" ] && \
    [ -f "$TARGET_DIR/lib/javafx.fxml.jar" ] && \
    [ -f "$TARGET_DIR/lib/javafx.graphics.jar" ]
}

# Verify JavaFX installation integrity
verify_javafx_installation() {
    local required_jars=(
        "javafx.base.jar"
        "javafx.controls.jar"
        "javafx.fxml.jar"
        "javafx.graphics.jar"
        "javafx.media.jar"
        "javafx.swing.jar"
        "javafx.web.jar"
    )
    
    log_info "Verifying JavaFX installation..."
    
    for jar in "${required_jars[@]}"; do
        local jar_path="$TARGET_DIR/lib/$jar"
        if [ ! -f "$jar_path" ]; then
            log_warning "Missing JavaFX component: $jar"
            return 1
        fi
        
        # Check if JAR file is not empty and is a valid ZIP archive
        if [ ! -s "$jar_path" ] || ! unzip -t "$jar_path" >/dev/null 2>&1; then
            log_warning "Corrupted JavaFX component: $jar"
            return 1
        fi
    done
    
    log_success "JavaFX ${JAVAFX_VERSION} installation verified"
    return 0
}

# Install JavaFX SDK
install_javafx() {
    log_info "Installing JavaFX SDK ${JAVAFX_VERSION}..."
    
    # Create temporary directories
    TEMP_DIR=$(mktemp -d)
    EXTRACT_DIR=$(mktemp -d)
    ZIP_FILE="/tmp/openjfx-${JAVAFX_VERSION}.zip"
    
    # Ensure unzip is available
    if ! command_exists unzip; then
        update_apt_cache
        install_package "unzip"
    fi
    
    # Download JavaFX if not cached
    if [ ! -f "$ZIP_FILE" ]; then
        log_info "Downloading JavaFX SDK ${JAVAFX_VERSION}..."
        if ! download_file "$JAVAFX_BASE_URL" "$ZIP_FILE"; then
            error_exit "Failed to download JavaFX SDK"
        fi
    else
        log_info "Using cached JavaFX archive: $ZIP_FILE"
    fi
    
    # Verify download integrity
    if [ ! -s "$ZIP_FILE" ] || ! unzip -t "$ZIP_FILE" >/dev/null 2>&1; then
        log_warning "Downloaded file appears corrupted, re-downloading..."
        rm -f "$ZIP_FILE"
        if ! download_file "$JAVAFX_BASE_URL" "$ZIP_FILE"; then
            error_exit "Failed to re-download JavaFX SDK"
        fi
    fi
    
    # Extract JavaFX SDK
    log_info "Extracting JavaFX SDK..."
    if ! unzip -q "$ZIP_FILE" -d "$EXTRACT_DIR"; then
        error_exit "Failed to extract JavaFX SDK"
    fi
    
    # Find extracted SDK directory
    local sdk_dir
    sdk_dir=$(find "$EXTRACT_DIR" -maxdepth 1 -type d -name "javafx-sdk-${JAVAFX_VERSION}" | head -n1)
    
    if [ -z "$sdk_dir" ] || [ ! -d "$sdk_dir" ]; then
        error_exit "JavaFX SDK directory not found after extraction"
    fi
    
    # Install atomically by using a temporary target
    local temp_target="${TARGET_DIR}.tmp"
    rm -rf "$temp_target"
    mkdir -p "$temp_target"
    
    log_info "Installing JavaFX to $TARGET_DIR..."
    if ! cp -r "$sdk_dir"/* "$temp_target"/; then
        error_exit "Failed to copy JavaFX SDK files"
    fi
    
    # Atomic move to final location
    rm -rf "$TARGET_DIR"
    if ! mv "$temp_target" "$TARGET_DIR"; then
        error_exit "Failed to finalize JavaFX installation"
    fi
    
    # Verify installation
    if verify_javafx_installation; then
        log_success "JavaFX ${JAVAFX_VERSION} installed successfully at $TARGET_DIR"
    else
        error_exit "JavaFX installation verification failed"
    fi
}

# Ensure JavaFX is available
ensure_javafx() {
    if verify_javafx_installation; then
        log_success "JavaFX ${JAVAFX_VERSION} is already installed"
        return 0
    fi
    
    if [ -d "$TARGET_DIR" ]; then
        log_warning "Existing JavaFX installation appears incomplete or corrupted, reinstalling..."
        rm -rf "$TARGET_DIR"
    fi
    
    install_javafx
}

# Print project setup summary
print_setup_summary() {
    echo ""
    echo "🎉 FreshHarvest project setup completed successfully!"
    echo ""
    echo "📋 Installation Summary:"
    echo "   • Java: $(java -version 2>&1 | head -n1 | cut -d'"' -f2)"
    echo "   • Gradle: $(gradle --version 2>/dev/null | grep "Gradle" | head -1 | awk '{print $2}')"
    echo "   • JavaFX: ${JAVAFX_VERSION} at ${TARGET_DIR}"
    echo ""
    echo "🚀 You can now build and run the FreshHarvest project!"
    echo ""
}

# --- Main Script Execution ---

main() {
    # Set up cleanup trap
    trap cleanup EXIT
    
    log_info "Setting up FreshHarvest development environment..."
    echo ""
    
    # Verify we're on Ubuntu/Debian
    if ! command_exists apt-get; then
        error_exit "This script requires Ubuntu or Debian. Please install dependencies manually."
    fi
    
    # Install all components
    ensure_java21
    ensure_gradle  
    ensure_javafx
    
    # Print summary
    print_setup_summary
}

# Run main function
main "$@"
