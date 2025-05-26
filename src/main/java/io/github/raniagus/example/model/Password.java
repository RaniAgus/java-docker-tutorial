package io.github.raniagus.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

@Embeddable
public class Password {
  @Column(name = "password")
  private String value;
  @Column(name = "password_salt")
  private String salt;

  public Password(String value) {
    this.salt = RandomStringUtils.secure().next(16);
    this.value = DigestUtils.sha256Hex(value + salt);
  }

  protected Password() {}

  public boolean matches(String value) {
    return this.value.equals(DigestUtils.sha256Hex(value + salt));
  }
}
