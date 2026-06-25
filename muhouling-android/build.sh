#!/bin/bash
# 母后令 Android 构建脚本
# 使用方法: ./build.sh

set -e

echo "=== 母后令 Android 构建 ==="

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "错误: 需要安装 Java 17"
    echo "下载: https://adoptium.net/"
    exit 1
fi

# 检查 ANDROID_HOME
if [ -z "$ANDROID_HOME" ]; then
    echo "警告: ANDROID_HOME 未设置"
    echo "请安装 Android SDK 或设置 ANDROID_HOME 环境变量"
    echo ""
    echo "安装方法:"
    echo "1. 下载 Android Studio: https://developer.android.com/studio"
    echo "2. 或下载命令行工具: https://developer.android.com/studio#command-tools"
    echo ""
    echo "设置环境变量:"
    echo "  export ANDROID_HOME=\$HOME/Android/Sdk"
    echo "  export PATH=\$PATH:\$ANDROID_HOME/platform-tools"
fi

# 构建
echo "开始构建..."
./gradlew assembleDebug

# 检查结果
APK="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK" ]; then
    echo ""
    echo "构建成功!"
    echo "APK 位置: $APK"
    echo "大小: $(du -h $APK | cut -f1)"
    echo ""
    echo "安装到手机:"
    echo "  adb install $APK"
else
    echo "构建失败"
    exit 1
fi
