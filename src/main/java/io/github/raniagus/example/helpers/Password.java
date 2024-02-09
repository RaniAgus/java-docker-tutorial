package io.github.raniagus.example.helpers;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class Password {
  private String value;
  private String salt;

  public Password(String value) {
    this(value, RandomStringUtils.random(16));
  }

  public Password(String value, String salt) {
    this.value = value;
    this.salt = salt;
  }

  public String getHashedValue() {
    return DigestUtils.sha256Hex(value + salt);
  }

  public String getSalt() {
    return salt;
  }

  public boolean matches(String hashedValue) {
    return getHashedValue().equals(hashedValue);
  }
}
