package io.github.raniagus.example.mail;

import com.sendgrid.helpers.mail.Mail;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SendGrid {
  @POST("send")
  Call<Void> sendMail(@Body Mail mail);
}
