package by.diplom.workspace.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

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