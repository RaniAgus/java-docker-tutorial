package io.github.raniagus.example.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

@Embeddable
public class Password {
  @Column(name = "password")
  private String password;
  @Column(name = "password_salt")
  private String passwordSalt;

  public Password(String value) {
    this.passwordSalt = RandomStringUtils.random(16);
    this.password = DigestUtils.sha256Hex(value + passwordSalt);
  }

  protected Password() {}

  public boolean matches(String value) {
    return password.equals(DigestUtils.sha256Hex(value + passwordSalt));
  }
}
