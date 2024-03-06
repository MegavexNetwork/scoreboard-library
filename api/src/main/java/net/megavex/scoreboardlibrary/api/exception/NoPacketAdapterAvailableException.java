package net.megavex.scoreboardlibrary.api.exception;

/**
 * Exception indicating that there is no packet adapter available for the current server version.
 * As a fallback, consider using the {@link net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary} implementation.
 */
public final class NoPacketAdapterAvailableException extends Exception {
  public NoPacketAdapterAvailableException() {
  }
}
