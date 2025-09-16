# HostLauncher - IntelliJ IDEA 主机配置管理插件

HostLauncher 是一个专为 IntelliJ IDEA 开发的插件，旨在帮助开发者快速管理和切换不同的主机配置环境。通过直观的图形界面和快捷键操作，大幅提升开发效率。

## ✨ 功能特性

### 🚀 核心功能
- **多配置管理**：创建、编辑、删除多个主机配置
- **一键切换**：快速在不同配置环境间切换
- **可视化界面**：清晰的配置项展示和选择
- **快捷键支持**：支持键盘快速操作


### ⚡ 效率提升
- **快速访问**：通过Tools菜单或快捷键直接访问
- **批量操作**：支持配置项的批量管理

## 📦 安装方法

### 方式：手动安装
1. 下载最新版本的 `HostLauncher.jar`
2. 打开 IntelliJ IDEA
3. 进入 `File → Settings → Plugins`
4. 点击 ⚙️ 图标选择 "Install Plugin from Disk..."
5. 选择下载的jar文件并重启IDEA

## 🎮 使用方法

### 基本操作
1. **打开插件面板**：
    - 菜单栏: `Tools → HostLauncher`
    - 快捷键: `Ctrl+Alt+U` (Win/Linux) 或 `Cmd+Option+U` (Mac)

2. **管理配置项**：
    - 点击"新增"创建新配置
    - 双击修改名称
    - 删除配置

3. **切换配置**：
    - 单选按钮选择所需配置
    - 实时生效，无需重启
 

### 自定义快捷键
1. 进入 `File → Settings → Keymap`
2. 搜索 "Host"
3. 自定义快捷键绑定


## 📋 系统要求

- **IntelliJ IDEA**: 2020.3 或更高版本
- **操作系统**: Windows, macOS，建议在Windows上使用

## 🔧 开发指南

### 环境搭建
```bash
# 克隆项目
git clone https://github.com/tartea/HostLauncher.git

# 导入项目
使用IntelliJ IDEA打开项目，配置Plugin SDK

# 运行测试
./gradlew test

# 构建插件
./gradlew buildPlugin
```

## 🤝 参与贡献

我们欢迎任何形式的贡献！


## 🐛 问题反馈

如果您遇到任何问题或有改进建议，请：

1. 查看 [常见问题解答](FAQ.md)
2. 搜索 [已有的Issue](https://github.com/tartea/HostLauncher/issues)
3. 如果没有找到相关问题，请[创建新Issue](https://github.com/tartea/HostLauncher/issues/new)

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

感谢以下开源项目提供的灵感和技术支持：
- [IntelliJ Platform SDK](https://github.com/JetBrains/intellij-community)
- [IntelliJ Plugin Verifier](https://github.com/JetBrains/intellij-plugin-verifier)
- [Gradle IntelliJ Plugin](https://github.com/JetBrains/gradle-intellij-plugin)


## 🔄 更新日志

### v1.0.0 (2025-09-16)
- ✅ 初始版本发布
- ✅ 基础配置管理功能
- ✅ 图形化界面
- ✅ 快捷键支持

---

⭐ 如果这个项目对您有帮助，请给我们一个Star！您的支持是我们持续改进的动力。