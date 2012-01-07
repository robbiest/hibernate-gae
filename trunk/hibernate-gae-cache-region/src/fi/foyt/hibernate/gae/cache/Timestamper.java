package fi.foyt.hibernate.gae.cache;

public class Timestamper {

  public static long next () {
    sequence++;
    return System.currentTimeMillis() + sequence;
  }
  
  private static int sequence = 0;
}
