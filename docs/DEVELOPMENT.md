# 小逆输入法 - 开发文档

## 项目概述

小逆输入法是一款专注于极速语音输入的 Android 输入法应用。

## 核心功能

### 1. 语音输入
- 集成讯飞语音识别 SDK
- 支持连续识别模式
- 自动发送（模拟回车键）
- 音量可视化反馈

### 2. 键盘输入
- QWERTY 字母键盘
- 符号键盘
- 基础编辑功能（删除、空格、回车）

### 3. 设置选项
- 连续识别模式开关
- 自动发送开关
- 按键音效开关
- 按键震动开关

## 技术架构

```
app/src/main/java/com/xiaoni/ime/
├── XiaoNiApplication.kt          # 应用入口
├── service/
│   └── XiaoNiInputMethodService.kt  # 输入法核心服务
├── ui/
│   ├── KeyboardViewManager.kt    # 键盘视图管理
│   └── SettingsActivity.kt       # 设置页面
├── voice/
│   └── VoiceInputManager.kt      # 语音输入管理
└── utils/
    └── PreferenceManager.kt      # 偏好设置
```

## 关键类说明

### XiaoNiInputMethodService
继承 `InputMethodService`，是输入法的核心服务。
- 管理键盘视图生命周期
- 处理按键事件
- 协调语音输入模块

### VoiceInputManager
封装讯飞语音识别功能。
- 初始化语音识别器
- 开始/停止监听
- 处理识别结果回调

### KeyboardViewManager
管理键盘 UI。
- 切换键盘布局
- 显示/隐藏语音面板
- 更新语音状态

## 讯飞 SDK 集成

### 1. 申请 AppID
访问讯飞开放平台：https://www.xfyun.cn/
注册账号并创建应用，获取 AppID。

### 2. 下载 SDK
下载 Android 版语音识别 SDK，将 `Msc.jar` 放入 `app/libs/` 目录。

### 3. 配置 AppID
在 `local.properties` 中添加：
```
IFLYTEK_APPKEY=你的AppID
```

## 构建与运行

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34

### 构建步骤
1. 打开项目
2. 同步 Gradle
3. 配置讯飞 AppID
4. 运行到设备或模拟器

### 安装输入法
1. 安装 APK 后，进入系统设置
2. 找到"语言和输入法"
3. 启用"小逆输入法"
4. 选择小逆输入法作为默认输入法

## 权限说明

| 权限 | 用途 |
|-----|------|
| RECORD_AUDIO | 语音输入需要录音权限 |
| INTERNET | 语音识别需要网络连接 |
| WRITE_EXTERNAL_STORAGE | 讯飞 SDK 需要写入缓存 |

## 后续优化方向

1. **词库优化** - 添加常用词汇、个性化词库
2. **手势输入** - 滑动输入、手写识别
3. **主题皮肤** - 多种键盘主题
4. **剪贴板管理** - 历史剪贴板内容
5. **表情输入** - 表情包、颜文字
6. **云同步** - 用户词库同步

## 常见问题

### Q: 语音输入没有反应？
A: 检查是否授予录音权限，以及网络连接是否正常。

### Q: 如何切换键盘？
A: 点击键盘左下角的"符"按钮切换符号键盘。

### Q: 连续识别模式是什么？
A: 开启后，语音输入会自动保持，识别完成后自动发送并继续监听，无需反复点击麦克风。
