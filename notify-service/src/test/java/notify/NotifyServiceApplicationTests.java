package notify;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import notify.template.EmailTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.util.Random;

@DirtiesContext
@SpringBootTest
class NotifyServiceApplicationTests {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Test
    void contextLoads() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(username);
            helper.setTo("2089239345@qq.com");
            helper.setSubject("测试邮件 - 带附件");
            helper.setText(EmailTemplate.checkCodeGenerator(randomCheckCode()),true);

            File file = new File("F:\\UserDesktop\\DesktopTxT\\copy.sql");
            FileSystemResource resource = new FileSystemResource(file);
            helper.addAttachment("测试文件.sql", resource);

            mailSender.send(message);
            System.out.println("✅ 邮件发送成功");
        } catch (Exception e) {
            System.out.println("❌ 邮件发送失败，收件人: {}" + e);
        }
    }

    public String randomCheckCode (){
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

}
