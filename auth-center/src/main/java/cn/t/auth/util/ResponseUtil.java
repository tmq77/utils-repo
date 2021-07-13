package cn.t.auth.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

public class ResponseUtil {

  public static void setResponse(HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
  }
  
  public static void write(HttpServletResponse response, String content) {
    try {
      PrintWriter out =response.getWriter();
      out.write(content);
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
