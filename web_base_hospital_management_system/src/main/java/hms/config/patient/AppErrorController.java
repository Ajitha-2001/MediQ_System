package hms.config.patient;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.*;

@Controller
public class AppErrorController implements ErrorController {

  private final ErrorAttributes errorAttributes;

  @Autowired
  public AppErrorController(ErrorAttributes errorAttributes) {
    this.errorAttributes = errorAttributes;
  }

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request, Model model) {
    var web = new ServletWebRequest(request);
    var opts = ErrorAttributeOptions.of(MESSAGE, EXCEPTION, STACK_TRACE, BINDING_ERRORS);
    var attrs = errorAttributes.getErrorAttributes(web, opts);
    model.addAttribute("err", attrs);
    return "error";
  }
}
