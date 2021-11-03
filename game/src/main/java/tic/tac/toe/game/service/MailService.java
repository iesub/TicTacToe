package tic.tac.toe.game.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Getter
@Setter
public class MailService {

    @Autowired
    public JavaMailSender emailSender;

    @Value("${server.ip}")
    String ip;

    @Value("${server.port}")
    String port;

    @Value("${application.mail}")
    String mail;

    public MailService(){}

    public MailService(JavaMailSender emailSender){
        this.emailSender = emailSender;
    }

    public boolean sendActivationURL(String userMail, Long userId) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String htmlMsg = "<h2>Вы зарегистрировались на сайте TicTacToe</h2>" +
                "<h3>Перейдите по ссылке ниже, для активации вашего аккаунта.</h3>" +
                "<a href = 'http://"+ip+":"+port+"/registration/activate/"+ userId +"'>Активация</a>";
        try {
            helper.setText(htmlMsg, true);
            helper.setTo(userMail);
            helper.setSubject("Регистрация в TicTacToe");
            helper.setFrom(mail);
            emailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

}