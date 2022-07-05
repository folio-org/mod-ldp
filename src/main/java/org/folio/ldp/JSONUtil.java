package org.folio.ldp;

public class JSONUtil {

  public static String unescape(String input) {
    StringBuilder builder = new StringBuilder();

    int i = 0;
    while (i < input.length()) {
      char delimiter = input.charAt(i); i++; // consume letter or backslash

      if(delimiter == '\\' && i < input.length()) {

        // consume first after backslash
        char ch = input.charAt(i); i++;

        if(ch == '\\' || ch == '/' || ch == '"' || ch == '\'') {
          builder.append(ch);
        }
        else if(ch == 'n') builder.append('\n');
        else if(ch == 'r') builder.append('\r');
        else if(ch == 't') builder.append('\t');
        else if(ch == 'b') builder.append('\b');
        else if(ch == 'f') builder.append('\f');
        else if(ch == 'u') {

          StringBuilder hex = new StringBuilder();

          // expect 4 digits
          if (i+4 > input.length()) {
            throw new RuntimeException("Not enough unicode digits! ");
          }
          for (char x : input.substring(i, i + 4).toCharArray()) {
            if(!Character.isLetterOrDigit(x)) {
              throw new RuntimeException("Bad character in unicode escape.");
            }
            hex.append(Character.toLowerCase(x));
          }
          i+=4; // consume those four digits.

          int code = Integer.parseInt(hex.toString(), 16);
          builder.append((char) code);
        } else {
          throw new RuntimeException("Illegal escape sequence: \\"+ch);
        }
      } else { // it's not a backslash, or it's the last character.
        builder.append(delimiter);
      }
    }

    return builder.toString();
  }
  
}
