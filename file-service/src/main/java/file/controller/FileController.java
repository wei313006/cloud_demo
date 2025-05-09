package file.controller;

import file.service.MinioService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    @Resource
    private MinioService minioService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            return minioService.uploadFile(
                    originalFilename,
                    file.getInputStream(),
                    file.getContentType(),
                    file.getSize()
            );
        } catch (Exception e) {
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public void delete() {

    }

    @GetMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse response) {
        try (InputStream inputStream = minioService.downloadFile(filename)) {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String encodedName = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedName);
            response.getOutputStream().write(inputStream.readAllBytes());
            response.flushBuffer();
        } catch (Exception e) {
            throw new RuntimeException("下载失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public List<String> list(@RequestParam(value = "prefix", required = false, defaultValue = "") String prefix) {
        return minioService.listFiles(prefix);
    }
}
