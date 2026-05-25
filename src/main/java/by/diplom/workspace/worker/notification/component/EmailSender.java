package by.diplom.workspace.worker.notification.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    // Аутентификация ────────────────────────────────────────────────────────
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

    // Безопасность аккаунта (системные, отправляются всегда) ───────────────

    /**
     * Уведомление об изменении пароля.
     * Отправляется на текущую основную почту после смены пароля.
     * Системное — не зависит от настроек уведомлений пользователя.
     *
     * @param toPrimaryEmail основная почта пользователя
     * @param fullName       полное имя пользователя
     */
    public void sendPasswordChangedNotification(String toPrimaryEmail, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toPrimaryEmail);
        message.setSubject("Пароль вашего аккаунта был изменён");
        message.setText("""
                Здравствуйте, %s!
                
                Пароль вашего аккаунта в системе Workspace был изменён.
                
                Если это были не вы — немедленно обратитесь к администратору \
                и смените пароль как можно скорее.
                
                Если вы сами изменили пароль — проигнорируйте это письмо.
                """.formatted(fullName));
        mailSender.send(message);
    }

    /**
     * Уведомление об изменении основной почты.
     * Отправляется на СТАРУЮ основную почту, чтобы пользователь знал о смене.
     * Системное — не зависит от настроек уведомлений пользователя.
     *
     * @param toOldPrimaryEmail старая основная почта
     * @param fullName          полное имя пользователя
     * @param newPrimaryEmail   новая основная почта
     */
    public void sendPrimaryEmailChangedNotification(
            String toOldPrimaryEmail,
            String fullName,
            String newPrimaryEmail
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toOldPrimaryEmail);
        message.setSubject("Основная почта вашего аккаунта изменена");
        message.setText("""
                Здравствуйте, %s!
                
                Основная почта вашего аккаунта в системе Workspace была изменена.
                
                Новая основная почта: %s
                Этот адрес (%s) больше не является основным.
                
                Если это были не вы — немедленно обратитесь к администратору.
                """.formatted(fullName, newPrimaryEmail, toOldPrimaryEmail));
        mailSender.send(message);
    }

    /**
     * Уведомление о добавлении новой верифицированной почты.
     * Отправляется на текущую основную почту.
     * Системное — не зависит от настроек уведомлений пользователя.
     *
     * @param toPrimaryEmail   текущая основная почта
     * @param fullName         полное имя пользователя
     * @param newVerifiedEmail добавленный и подтверждённый email-адрес
     */
    public void sendNewVerifiedEmailAddedNotification(
            String toPrimaryEmail,
            String fullName,
            String newVerifiedEmail
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toPrimaryEmail);
        message.setSubject("К вашему аккаунту добавлена новая почта");
        message.setText("""
                Здравствуйте, %s!
                
                К вашему аккаунту в системе Workspace был добавлен \
                и подтверждён новый email-адрес: %s
                
                Если это были не вы — немедленно обратитесь к администратору.
                """.formatted(fullName, newVerifiedEmail));
        mailSender.send(message);
    }

    /**
     * Уведомление об удалении верифицированной почты.
     * Отправляется на текущую основную почту. Системное —
     * не зависит от настроек уведомлений пользователя.
     *
     * @param toPrimaryEmail основная почта пользователя
     * @param fullName       полное имя пользователя
     * @param removedEmail   удалённый email-адрес
     */
    public void sendVerifiedEmailRemovedNotification(
            String toPrimaryEmail,
            String fullName,
            String removedEmail
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toPrimaryEmail);
        message.setSubject("Email-адрес удалён из вашего аккаунта");
        message.setText("""
                Здравствуйте, %s!
                
                Из вашего аккаунта в системе Workspace был удалён email-адрес: %s
                
                Если это были не вы — немедленно обратитесь к администратору.
                """.formatted(fullName, removedEmail));
        mailSender.send(message);
    }

    /**/
    public void sendUserCreationRequestReviewNotification(String toEmail, String fullName, String departmentName, String positionName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Ваша заявка на создание аккаунта находится на рассмотрении");
        message.setText("""
                Основная информация по заявке:
                ФИО: %s
                Отдел: %s
                Должность: %s
                
                После рассмотрения заявки вы получите дополнительное уведомление на эту почту.
                """.formatted(fullName, departmentName, positionName));
        mailSender.send(message);
    }

    public void sendAccountCreationRequestRejectedPushNotification(String toEmail, String fullName, String response) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Заявка на создание аккаунта отклонена");
        message.setText("""
                Здравствуйте, %s!
                
                По результатам рассмотрения ваша заявка на создание аккаунта была отклонена.
                
                Причина отклонения:
                %s
                
                Для уточнения информации вы можете обратиться к администратору системы.
                """.formatted(fullName, response));
        mailSender.send(message);
    }
}