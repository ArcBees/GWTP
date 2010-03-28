package com.philbeaudoin.gwtp.dispatch.shared.secure;

import com.philbeaudoin.gwtp.dispatch.shared.Action;

/**
 * A simple username/password authentication request. If successful, a session
 * has been created and a {@link SecureSessionResult} is returned.
 * 
 * @author David Peterson
 */
public class SimpleAuthenticationAction implements Action<SecureSessionResult> {

  private static final long serialVersionUID = -9035569517961908934L;

  private String username;

  private String password;

  SimpleAuthenticationAction() {
  }

  public SimpleAuthenticationAction( String username, String password ) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
