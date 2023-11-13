package io.github.raniagus.example;

import io.github.raniagus.example.helpers.Environment;
import io.github.raniagus.example.mail.MailSender;

public class Main {
  public static void main(String[] args) {
    new MailSender(Environment.getVariable("SENDGRID_API_KEY")).sendMailSync(
        args[0],
        "Sending with Twilio SendGrid is Fun",
        "and easy to do anywhere, even with Java"
    );
    System.out.println("Email sent successfully!");
  }
}
