#!/bin/bash
# 母后令 APK 一键构建脚本
# 适用于 Windows (Git Bash), Mac, Linux

set -e

echo "=========================================="
echo "       母后令 APK 构建工具"
echo "=========================================="
echo ""

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "[错误] 需要安装 Java 17"
    echo ""
    echo "下载地址:"
    echo "  https://adoptium.net/temurin/releases/?version=17"
    echo ""
    echo "安装后重新运行此脚本"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "[错误] Java 版本太低，需要 17+，当前: $JAVA_VERSION"
    exit 1
fi

echo "[OK] Java $JAVA_VERSION 已安装"

# 设置 Android SDK 路径
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_HOME="$HOME/Android/Sdk"
    elif [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    elif [ -d "$HOME/android-sdk" ]; then
        export ANDROID_HOME="$HOME/android-sdk"
    else
        echo "[提示] 未找到 Android SDK，正在自动下载..."
        mkdir -p "$HOME/android-sdk"
        cd "$HOME/android-sdk"
        
        echo "下载 Android SDK 命令行工具..."
        if command -v curl &> /dev/null; then
            curl -L "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip" -o cmdtools.zip
        else
            wget "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip" -O cmdtools.zip
        fi
        
        unzip -q cmdtools.zip
        mkdir -p cmdline-tools/latest
        mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null || true
        
        export ANDROID_HOME="$HOME/android-sdk"
        export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
        
        echo "安装 SDK 组件..."
        yes | sdkmanager --licenses > /dev/null 2>&1
        sdkmanager "platforms;android-35" "build-tools;35.0.0"
        
        rm cmdtools.zip
        echo "[OK] Android SDK 安装完成"
    fi
fi

echo "[OK] Android SDK: $ANDROID_HOME"

# 下载 Gradle
GRADLE_VERSION="8.9"
GRADLE_DIR="$HOME/.gradle/wrapper/dists/gradle-${GRADLE_VERSION}-bin"

if [ ! -d "$GRADLE_DIR" ]; then
    echo "下载 Gradle $GRADLE_VERSION..."
    mkdir -p /tmp/gradle-download
    cd /tmp/gradle-download
    
    if command -v curl &> /dev/null; then
        curl -L "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o gradle.zip
    else
        wget "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O gradle.zip
    fi
    
    unzip -q gradle.zip
    mkdir -p "$GRADLE_DIR"
    mv "gradle-${GRADLE_VERSION}"/* "$GRADLE_DIR/"
    
    rm -rf /tmp/gradle-download
    echo "[OK] Gradle 安装完成"
fi

GRADLE_BIN="$GRADLE_DIR/bin/gradle"
echo "[OK] Gradle: $GRADLE_BIN"

# 构建 APK
echo ""
echo "开始构建 APK..."
echo ""

cd "$(dirname "$0")"
"$GRADLE_BIN" assembleDebug

# 检查结果
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo ""
    echo "=========================================="
    echo "       构建成功!"
    echo "=========================================="
    echo ""
    echo "APK 位置: $(pwd)/$APK_PATH"
    echo "APK 大小: $APK_SIZE"
    echo ""
    echo "安装方法:"
    echo "  1. 用数据线连接手机"
    echo "  2. 打开 USB 调试"
    echo "  3. 运行: adb install $APK_PATH"
    echo ""
    echo "或者把 APK 文件传到手机，点击安装"
else
    echo ""
    echo "[错误] 构建失败，请查看上面的错误信息"
    exit 1
fi
