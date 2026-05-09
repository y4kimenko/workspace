package by.diplom.workspace.email.component;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Подтверждение почты");
        message.setText("""
                Ваш код подтверждения: %s
                
                Код действует 15 минут.
                Если вы не запрашивали подтверждение — проигнорируйте это письмо.
                """.formatted(code));

        mailSender.send(message);
    }
}