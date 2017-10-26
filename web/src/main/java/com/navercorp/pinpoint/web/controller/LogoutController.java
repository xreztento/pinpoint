package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.controller.form.LoginForm;
import com.navercorp.pinpoint.web.service.UserService;
import com.navercorp.pinpoint.web.util.CodecUtils;
import com.navercorp.pinpoint.web.vo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/logout")
public class LogoutController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> logout(HttpSession session) {
        Map<String, String> result = new HashMap<>();

        if(session.getAttribute("userId") == null){
            result.put("errorCode", "403");
            result.put("errorMessage", "no login!");
            return result;
        } else {
            session.removeAttribute("userId");
            session.removeAttribute("username");
            session.removeAttribute("role");
            result.put("result", "SUCCESS");
        }
        return result;
    }
}
