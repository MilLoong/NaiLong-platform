package com.nailong.common.utils;

import cn.hutool.core.io.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @brief 文件工具类
 * @details 提供文件操作相关的工具方法
 * @author Nailong
 */
public class NailongFileUtil {
    
    /**
     * @brief 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名（小写），无扩展名时返回空字符串
     */
    public static String getExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * @brief 获取上传文件的扩展名
     * @param file 上传文件
     * @return 扩展名（小写），文件为空时返回空字符串
     */
    public static String getExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        return getExtension(file.getOriginalFilename());
    }
    
    /**
     * @brief 生成安全的文件名（防止文件名冲突）
     * @param originalFileName 原始文件名
     * @return 带时间戳和随机串的安全文件名
     */
    public static String generateSafeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        
        String extension = getExtension(originalFileName);
        String baseName = cn.hutool.core.io.FileUtil.mainName(originalFileName);
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String randomStr = UUID.randomUUID().toString().substring(0, 8);
        
        if (extension.isEmpty()) {
            return baseName + "_" + timestamp + "_" + randomStr;
        } else {
            return baseName + "_" + timestamp + "_" + randomStr + "." + extension;
        }
    }
    
    /**
     * @brief 检查文件是否为图片类型
     * @param fileName 文件名
     * @return 是图片返回 true，否则 false
     */
    public static boolean isImage(String fileName) {
        String extension = getExtension(fileName).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") || 
               extension.equals("png") || extension.equals("gif") || 
               extension.equals("bmp") || extension.equals("webp");
    }
    
    /**
     * @brief 检查文件是否为文本类型
     * @param fileName 文件名
     * @return 是文本文件返回 true，否则 false
     */
    public static boolean isText(String fileName) {
        String extension = getExtension(fileName).toLowerCase();
        return extension.equals("txt") || extension.equals("md") || 
               extension.equals("json") || extension.equals("xml") || 
               extension.equals("html") || extension.equals("css") || 
               extension.equals("js") || extension.equals("java") || 
               extension.equals("c") || extension.equals("cpp") || 
               extension.equals("py") || extension.equals("go") || 
               extension.equals("php") || extension.equals("rb");
    }
    
    /**
     * @brief 获取文件大小的格式化表示
     * @param size 文件大小（字节）
     * @return 格式化后的大小字符串
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
    
    /**
     * @brief 读取文件内容为字符串
     * @param filePath 文件路径
     * @return 文件内容（UTF-8 编码）
     * @throws IOException 文件不存在或读取失败时抛出
     */
    public static String readFileToString(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在: " + filePath);
        }
        
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return new String(data, StandardCharsets.UTF_8);
        }
    }
    
    /**
     * @brief 写入字符串到文件
     * @param filePath 文件路径
     * @param content  要写入的内容
     * @throws IOException 写入失败时抛出
     */
    public static void writeStringToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * @brief 创建目录
     * @param dirPath 目录路径
     * @return 创建成功或目录已存在返回 true，否则 false
     */
    public static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return dir.isDirectory();
    }
    
    /**
     * @brief 删除文件
     * @param filePath 文件路径
     * @return 删除成功返回 true，否则 false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }
    
    /**
     * @brief 复制文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 复制成功返回 true，否则 false
     * @throws IOException 源文件不存在或复制失败时抛出
     */
    public static boolean copyFile(String sourcePath, String targetPath) throws IOException {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            throw new IOException("源文件不存在: " + sourcePath);
        }
        
        File targetFile = new File(targetPath);
        File targetDir = targetFile.getParentFile();
        if (targetDir != null && !targetDir.exists()) {
            targetDir.mkdirs();
        }
        
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        
        return targetFile.exists() && targetFile.length() == sourceFile.length();
    }
    
    /**
     * @brief 获取文件的 MIME 类型
     * @param fileName 文件名
     * @return MIME 类型字符串
     */
    public static String getContentType(String fileName) {
        String extension = getExtension(fileName).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            case "txt":
                return "text/plain";
            case "html":
            case "htm":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            case "pdf":
                return "application/pdf";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            case "tar":
                return "application/x-tar";
            case "gz":
                return "application/gzip";
            default:
                return "application/octet-stream";
        }
    }
}
