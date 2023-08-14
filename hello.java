import java.util.*;

public class hello {
  public static void main(String args[]) {
    try (Scanner sc = new Scanner(System.in)) {
      int a = sc.nextInt();
      int b = sc.nextInt();
      int c = a + b;
      System.out.println(c);
    }
  }
}