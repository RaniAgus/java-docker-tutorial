package io.github.raniagus.example.model;

import javax.persistence.Embeddable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

@Embeddable
public class Password {
  private String hashedValue;
  private String salt;

  public Password(String value) {
    this.salt = RandomStringUtils.random(16);
    this.hashedValue = DigestUtils.sha256Hex(value + salt);
  }

  protected Password() {}

  public boolean matches(String value) {
    return hashedValue.equals(DigestUtils.sha256Hex(value + salt));
  }
}
