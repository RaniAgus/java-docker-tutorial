package io.github.raniagus;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class EmailService {
  private final SendGrid sendGrid;
  private final Email fromEmail;

  public EmailService() {
    this.fromEmail = new Email("SENDGRID_FROM_EMAIL");
    this.sendGrid = new Retrofit.Builder()
        .baseUrl("https://api.sendgrid.com/v3/mail/")
        .addConverterFactory(JacksonConverterFactory.create())
        .client(new OkHttpClient.Builder()
            .addInterceptor(chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + System.getenv("SENDGRID_API_KEY"))
                        .build()
                )
            )
            .build()
        )
        .build()
        .create(SendGrid.class);
  }

  public void sendMailSync(String to, String subject, String body) {
    try {
      var result = this.sendGrid.sendMail(
          new Mail(
              this.fromEmail,
              subject,
              new Email(to),
              new Content("text/plain", body)
          )
      ).execute();
      if (result.isSuccessful()) {
        return;
      }
      try (var errorBody = result.errorBody()) {
        throw new EmailSendingException(errorBody != null ? errorBody.string() : "Unknown error");
      }
    } catch (IOException e) {
      throw new EmailSendingException("Unknown error", e);
    }
  }
}
