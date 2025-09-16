package org.host.launcher.host;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class HostsFileWriter {

    private static final String WINDOWS_HOSTS_PATH = "C:\\Windows\\System32\\drivers\\etc\\hosts";
    private static final String UNIX_HOSTS_PATH = "/etc/hosts";
    private static final String START_MARKER = "# ===== SWITCHHOSTS START =====";
    private static final String END_MARKER = "# ===== SWITCHHOSTS END =====";

    /**
     * 带标识的追加内容到hosts文件
     * 会先删除上次追加的内容，然后添加新的内容
     *
     * @param content 要追加的内容
     * @return 是否成功
     */
    public static boolean appendToHostsFileWithIdentifier(String content) {
        String hostsPath = getHostsPath();

        try {
            // 首先尝试直接操作（如果程序以管理员身份运行）
            updateHostsWithIdentifier(hostsPath, content);
            System.out.println("直接更新成功");
            return true;
        } catch (IOException e) {
            System.out.println("直接更新失败，尝试权限提升: " + e.getMessage());
            // 权限不足，尝试提权操作
            return updateWithElevation(content, hostsPath);
        }
    }

    /**
     * 获取hosts文件路径
     */
    public static String getHostsPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return WINDOWS_HOSTS_PATH;
        } else {
            return UNIX_HOSTS_PATH;
        }
    }


    /**
     * 直接更新hosts文件（带标识）
     */
    private static void updateHostsWithIdentifier(String filePath, String newContent) throws IOException {
        // 读取当前文件内容
        String currentContent = "";
        try {
            currentContent = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
        } catch (IOException e) {
            // 文件不存在或无法读取，使用空内容
            currentContent = "";
        }

        // 构造新的文件内容
        String updatedContent = buildUpdatedContent(currentContent, newContent);

        // 写入更新后的内容
        Files.write(Paths.get(filePath), updatedContent.getBytes("UTF-8"));
    }

    /**
     * 构造更新后的文件内容
     * 删除旧的标识内容块，添加新的标识内容块
     */
    private static String buildUpdatedContent(String currentContent, String newContent) {
        // 移除现有的标识内容块
        String contentWithoutOldBlock = removeExistingBlock(currentContent);

        // 如果新内容为空，则只返回清理后的内容（用于清除操作）
        if (newContent == null || newContent.trim().isEmpty()) {
            return contentWithoutOldBlock;
        }

        // 构造新的标识内容块
        StringBuilder newBlock = new StringBuilder();
        newBlock.append("\n").append(START_MARKER).append("\n");
        if (newContent != null && !newContent.trim().isEmpty()) {
            newBlock.append(newContent.trim()).append("\n");
        }
        newBlock.append(END_MARKER).append("\n");

        // 如果原内容末尾没有换行符，添加一个
        if (!contentWithoutOldBlock.isEmpty() && !contentWithoutOldBlock.endsWith("\n")) {
            contentWithoutOldBlock += "\n";
        }

        // 返回更新后的内容
        return contentWithoutOldBlock + newBlock.toString();
    }

    /**
     * 移除现有的标识内容块（修复后的版本）
     */
    private static String removeExistingBlock(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        // 构造正则表达式，使用 Pattern.DOTALL 标志让 . 匹配换行符
        String regex = Pattern.quote(START_MARKER) + "[\\s\\S]*?" + Pattern.quote(END_MARKER) + "\\s*";

        // 编译带有 DOTALL 标志的正则表达式
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

        // 替换标识块内容
        String result = pattern.matcher(content).replaceAll("");

        return result.trim();
    }

    /**
     * 权限提升更新
     */
    private static boolean updateWithElevation(String newContent, String hostsPath) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                return updateWindowsHostsWithAdmin(newContent, hostsPath);
            } else {
                return updateUnixHostsWithSudo(newContent, hostsPath);
            }
        } catch (Exception e) {
            System.err.println("权限提升更新失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * Windows系统使用PowerShell提权更新
     */
    private static boolean updateWindowsHostsWithAdmin(String newContent, String hostsPath) {
        try {
            // 创建临时文件存储新的内容
            String updatedContent = buildUpdatedContent(readCurrentHosts(), newContent);
            Path tempContentFile = Files.createTempFile("hosts_update_", ".tmp");
            Files.write(tempContentFile, updatedContent.getBytes("UTF-8"));

            // 创建PowerShell脚本
            String psScript = String.format(
                    "Set-Content -Path \"%s\" -Value (Get-Content \"%s\" -Raw)",
                    hostsPath,
                    tempContentFile.toAbsolutePath().toString()
            );

            // 写入临时PS脚本文件
            Path psScriptFile = Files.createTempFile("update_hosts_", ".ps1");
            Files.write(psScriptFile, psScript.getBytes("UTF-8"));

            // 使用管理员权限执行
            String elevateScript = String.format(
                    "Start-Process powershell -ArgumentList '-ExecutionPolicy Bypass -File \"%s\"' -Verb RunAs -Wait",
                    psScriptFile.toAbsolutePath().toString()
            );

            // 创建提权执行脚本
            Path elevateScriptFile = Files.createTempFile("elevate_", ".ps1");
            Files.write(elevateScriptFile, elevateScript.getBytes("UTF-8"));

            ProcessBuilder pb = new ProcessBuilder(
                    "powershell",
                    "-ExecutionPolicy", "Bypass",
                    "-File", elevateScriptFile.toAbsolutePath().toString()
            );

            Process process = pb.start();
            int exitCode = process.waitFor();

            // 清理临时文件
            Files.deleteIfExists(tempContentFile);
            Files.deleteIfExists(psScriptFile);
            Files.deleteIfExists(elevateScriptFile);

            return exitCode == 0;

        } catch (Exception e) {
            System.err.println("Windows提权更新失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * Unix/Linux/Mac系统使用sudo更新
     */
    private static boolean updateUnixHostsWithSudo(String newContent, String hostsPath) {
        try {
            // 创建临时文件存储更新后的内容
            String updatedContent = buildUpdatedContent(readCurrentHosts(), newContent);
            Path tempContentFile = Files.createTempFile("hosts_update_", ".tmp");
            Files.write(tempContentFile, updatedContent.getBytes("UTF-8"));

            try {
                // 使用 osascript 调用 AppleScript，弹出系统级密码对话框
                ProcessBuilder pb = new ProcessBuilder(
                        "osascript", "-e",
                        "do shell script \"cp '" + tempContentFile.toAbsolutePath().toString() + "' '" + hostsPath + "'\" with administrator privileges"
                );

                Process process = pb.start();
                int exitCode = process.waitFor();


                // 捕获错误输出（非常重要！）
                String errorOutput = new String(process.getErrorStream().readAllBytes());
                if (exitCode != 0) {
                    System.out.println(errorOutput);
                }
                Files.deleteIfExists(tempContentFile);
                return exitCode != 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } catch (Exception e) {
            System.err.println("Unix sudo更新失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 读取当前hosts文件内容
     */
    public static String readCurrentHosts() {
        try {
            String hostsPath = getHostsPath();
            return new String(Files.readAllBytes(Paths.get(hostsPath)), "UTF-8");
        } catch (IOException e) {
            System.err.println("读取hosts文件失败: " + e.getMessage());
            return "";
        }
    }

    /**
     * 清除标识内容块（完全删除上次添加的内容，不添加新标识）
     */
    public static boolean clearIdentifierBlock() {
        return appendToHostsFileWithIdentifier(null);
    }

}