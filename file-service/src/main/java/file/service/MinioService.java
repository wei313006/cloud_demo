package file.service;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author abing
 * @created 2025/4/16 16:50
 */

//@Slf4j
@Component
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String url;

    public MinioService(@Value("${minio.url}") String endpoint,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String generateTempFileLink(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("生成连接失败", e);
        }
    }

    /**
     * 上传文件
     */
    public String uploadFile(String objectName, InputStream inputStream, String contentType, long size) {
        try {
            // 确保 bucket 存在
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            return generateLink(objectName);
        } catch (Exception e) {
//            log.error("上传文件失败", e);
            throw new RuntimeException("上传文件失败", e);
        }
    }

    public String generateLink(String objectName) {
        return url + "/" + bucketName + "/" + objectName;
    }

    /**
     * 下载文件
     */
    public InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
//            log.error("下载文件失败", e);
            throw new RuntimeException("下载文件失败", e);
        }
    }

    /**
     * 列出文件列表
     */
    public List<String> listFiles(String prefix) {
        List<String> objectNames = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                objectNames.add(result.get().objectName());
            }

        } catch (Exception e) {
//            log.error("列出文件失败", e);
            throw new RuntimeException("列出文件失败", e);
        }
        return objectNames;
    }

    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("删除文件失败", e);
        }
    }
}
