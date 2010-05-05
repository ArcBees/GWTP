package com.philbeaudoin.gwtp.dispatch.server;

import java.security.SecureRandom;

import com.google.inject.Singleton;

@Singleton
public class SecureRandomSingleton extends SecureRandom {
  private static final long serialVersionUID = 462441711297897572L;
}
