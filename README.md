<<<<<<< HEAD
# 小逆输入法

一个专注于极速语音输入的 Android 输入法。

## 核心特性

- 🎤 **极速语音输入** - 集成讯飞语音识别，识别速度快
- 🔄 **连续识别模式** - 自动保持语音输入，无需反复点击
- ⚡ **自动发送** - 识别完成后自动回车发送，继续监听
- 🎯 **专注效率** - 减少操作步骤，让语音输入更流畅

## 项目结构

```
xiaoni-ime/
├── app/                    # 主应用模块
│   ├── src/main/
│   │   ├── java/com/xiaoni/ime/
│   │   │   ├── service/    # 输入法服务
│   │   │   ├── ui/         # 键盘界面
│   │   │   ├── voice/      # 语音输入模块
│   │   │   └── utils/      # 工具类
│   │   ├── res/            # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── sdk/                    # 第三方 SDK
├── docs/                   # 文档
└── README.md
```

## 开发计划

1. [x] 项目初始化
2. [x] 基础输入法服务搭建
3. [x] 键盘 UI 界面
4. [x] 语音输入模块集成
5. [x] 连续识别逻辑实现
6. [x] 自动发送功能
7. [x] 设置页面
8. [ ] 测试与优化

## 技术栈

- Kotlin
- Android SDK
- 讯飞语音 SDK
- InputMethodService

## 快速开始

### 1. 配置讯飞 SDK

1. 访问 [讯飞开放平台](https://www.xfyun.cn/) 注册账号
2. 创建应用并获取 AppID
3. 下载 Android 语音识别 SDK
4. 将 `Msc.jar` 放入 `app/libs/` 目录
5. 复制 `local.properties.example` 为 `local.properties` 并填入 AppID

### 2. 构建运行

```bash
./gradlew assembleDebug
```

### 3. 启用输入法

1. 安装 APK
2. 进入设置 → 语言和输入法
3. 启用"小逆输入法"
4. 选择为默认输入法

## 使用说明

### 语音输入
- 点击键盘上的 🎤 按钮进入语音模式
- 对着手机说话
- 识别结果自动输入并发送（如果开启自动发送）
- 连续模式下会自动继续监听

### 设置选项
- **连续识别模式**: 识别完成后自动继续监听
- **自动发送**: 识别完成后自动按回车发送
- **按键音效**: 开关按键声音
- **按键震动**: 开关按键震动反馈

## 文档

- [开发文档](docs/DEVELOPMENT.md)
- [集成指南](docs/INTEGRATION.md)

## 许可证

MIT
=======
# xiaoni-ime
j
>>>>>>> 28b52cb7f3294f630dad6c25c1e6f248d88ba16d
