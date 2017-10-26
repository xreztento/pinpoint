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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/login")
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @ModelAttribute("login")
    public LoginForm getLoginForm() {
        logger.info("getLoginForm process");
        return new LoginForm();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(@ModelAttribute("login") LoginForm form, HttpSession session) {

        if (StringUtils.isEmpty(form.getUsername()) || StringUtils.isEmpty(form.getPassword())) {
            Map<String, String> result = new HashMap<>();
            result.put("errorCode", "500");
            result.put("errorMessage", "username or password is must not null!");
            return result;
        }
        String username = form.getUsername();
        String password = form.getPassword();
        String encodePassword = CodecUtils.getMd5Hex(password);

        List<User> userList = userService.selectUserByUserNameAndPassword(username, encodePassword);
        Map<String, String> result = new HashMap<>();
        if(userList.size() > 0){
            result.put("result", "SUCCESS");
            User user = userList.get(0);
            result.put("userId", user.getUserId());
            result.put("role", user.getRole());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getName());
            session.setAttribute("role", user.getRole());


        } else {
            result.put("result", "FAIL");
        }
        return result;
    }
}
