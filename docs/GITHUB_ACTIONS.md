# 小逆输入法 - GitHub Actions 自动构建指南

## 快速开始

### 1. 创建 GitHub 仓库

1. 访问 https://github.com/new
2. 仓库名称填 `xiaoni-ime`
3. 选择 "Public" 或 "Private"
4. 点击 "Create repository"

### 2. 上传代码到 GitHub

```bash
# 1. 进入项目目录
cd xiaoni-ime

# 2. 初始化 git
git init

# 3. 添加所有文件
git add .

# 4. 提交
git commit -m "Initial commit: 小逆输入法"

# 5. 关联远程仓库（替换 YOUR_USERNAME 为你的 GitHub 用户名）
git remote add origin https://github.com/YOUR_USERNAME/xiaoni-ime.git

# 6. 推送代码
git push -u origin main
```

### 3. 触发构建

推送代码后，GitHub Actions 会自动开始构建：

1. 在仓库页面点击 "Actions" 标签
2. 查看构建进度
3. 构建完成后，在 "Artifacts" 中下载 APK

### 4. 下载 APK

构建完成后，你可以：

**方式 A：从 Artifacts 下载（临时）**
- 进入 Actions 页面
- 点击最新的 workflow 运行
- 在页面底部找到 "Artifacts"
- 下载 `xiaoni-ime-debug` 或 `xiaoni-ime-release`

**方式 B：从 Releases 下载（永久）**
- 构建成功后会自动创建 Release
- 进入仓库的 "Releases" 页面
- 下载 APK 文件

---

## 自动发布配置（可选）

如果你想每次推送都自动发布到 Releases，需要配置签名：

### 生成签名密钥

```bash
keytool -genkey -v -keystore xiaoni-ime.keystore -alias xiaoni -keyalg RSA -keysize 2048 -validity 10000
```

### 添加签名 Secrets

在 GitHub Secrets 中添加：

| Name | Value |
|------|-------|
| `STORE_PASSWORD` | 密钥库密码 |
| `KEY_PASSWORD` | 密钥密码 |
| `KEY_ALIAS` | 密钥别名（如：xiaoni）|

### 上传密钥到 Secrets（Base64 编码）

```bash
# 将 keystore 转为 base64
cat xiaoni-ime.keystore | base64
```

然后在 workflow 中添加解码步骤。

---

## 常见问题

### Q: 构建失败？
A: 检查 Actions 日志，通常是 Gradle 或 SDK 问题。

### Q: 如何手动触发构建？
A: 进入 Actions 页面，选择 workflow，点击 "Run workflow"。

### Q: 语音识别需要联网吗？
A: Android 原生语音识别通常需要联网，但部分设备支持离线识别。

---

## 下一步

构建成功后：
1. 下载 APK 到手机
2. 安装并启用输入法
3. 开始使用小逆输入法！
