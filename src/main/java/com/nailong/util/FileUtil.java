package com.nailong.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @brief 文件工具类
 * @details 提供文件上传、下载、删除、读取等操作的工具方法
 * @author Nailong
 */
public class FileUtil {

    /**
     * @brief 上传文件
     * @param file 要上传的文件
     * @param uploadPath 上传路径
     * @return 上传后的文件路径
     * @throws IOException IO异常
     */
    public static String uploadFile(MultipartFile file, String uploadPath) throws IOException {
        // 确保上传目录存在
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = generateUniqueFilename(fileExtension);

        // 保存文件
        String filePath = uploadPath + File.separator + newFilename;
        file.transferTo(new File(filePath));

        return filePath;
    }

    /**
     * @brief 读取文件内容为字符串
     * @param filePath 文件路径
     * @return 文件内容
     * @throws IOException IO异常
     */
    public static String readFileToString(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * @brief 写入字符串到文件
     * @param content 要写入的内容
     * @param filePath 文件路径
     * @throws IOException IO异常
     */
    public static void writeStringToFile(String content, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        // 确保目录存在
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @brief 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * @brief 获取文件大小（字节）
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file.length() : 0;
    }

    /**
     * @brief 获取文件扩展名
     * @param filename 文件名
     * @return 文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * @brief 生成唯一文件名
     * @param extension 文件扩展名
     * @return 唯一文件名
     */
    public static String generateUniqueFilename(String extension) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        if (extension != null && !extension.isEmpty()) {
            return timestamp + "_" + uuid + "." + extension;
        }
        return timestamp + "_" + uuid;
    }

    /**
     * @brief 检查文件是否存在
     * @param filePath 文件路径
     * @return 是否存在
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * @brief 创建目录
     * @param directoryPath 目录路径
     * @return 是否创建成功
     */
    public static boolean createDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        return !directory.exists() && directory.mkdirs();
    }

    /**
     * @brief 获取文件的MIME类型
     * @param filePath 文件路径
     * @return MIME类型
     * @throws IOException IO异常
     */
    public static String getContentType(String filePath) throws IOException {
        return Files.probeContentType(Paths.get(filePath));
    }

    /**
     * @brief 复制文件
     * @param sourcePath 源文件路径
     * @param destinationPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void copyFile(String sourcePath, String destinationPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path destination = Paths.get(destinationPath);
        // 确保目标目录存在
        Path parentDir = destination.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        Files.copy(source, destination);
    }

    /**
     * @brief 移动文件
     * @param sourcePath 源文件路径
     * @param destinationPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void moveFile(String sourcePath, String destinationPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path destination = Paths.get(destinationPath);
        // 确保目标目录存在
        Path parentDir = destination.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        Files.move(source, destination);
    }

    /**
     * @brief 清理目录下的所有文件
     * @param directoryPath 目录路径
     * @return 是否清理成功
     */
    public static boolean cleanDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return false;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return true;
        }

        boolean allDeleted = true;
        for (File file : files) {
            if (file.isDirectory()) {
                allDeleted = cleanDirectory(file.getAbsolutePath()) && allDeleted;
                allDeleted = file.delete() && allDeleted;
            } else {
                allDeleted = file.delete() && allDeleted;
            }
        }

        return allDeleted;
    }

    /**
     * @brief 获取文件的MD5哈希值
     * @param filePath 文件路径
     * @return MD5哈希值
     * @throws IOException IO异常
     */
    public static String getFileMd5(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            // 使用hutool库的DigestUtil计算MD5哈希值
            return cn.hutool.crypto.digest.DigestUtil.md5Hex(inputStream);
        }
    }
}