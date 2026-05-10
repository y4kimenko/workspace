package by.diplom.workspace.email.component;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Подтверждение почты");
        message.setText("""
                Ваш код подтверждения: %s
                
                Код действует 15 минут.
                Если вы не запрашивали подтверждение — проигнорируйте это письмо.
                """.formatted(code));

        mailSender.send(message);
    }

    public void sendWelcomeEmail(String to, String fullName, String nickname, String rawPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Добро пожаловать в Workspace!");
        message.setText("""
                Здравствуйте, %s!
                
                Вы были зарегистрированы в системе Workspace.
                
                Ваши данные для входа:
                  Логин: %s
                  Пароль: %s
                
                Рекомендуем сменить пароль после первого входа.
                """.formatted(fullName, nickname, rawPassword));
        mailSender.send(message);
    }

}