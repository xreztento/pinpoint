package com.navercorp.pinpoint.web.controller;

import com.navercorp.pinpoint.web.controller.form.NewPasswordForm;
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
@RequestMapping(value = "/newPassword")
public class NewPasswordController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @ModelAttribute("newPasswordAttribute")
    public NewPasswordForm getNewPasswordForm() {
        logger.info("getNewPasswordForm process");
        return new NewPasswordForm();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> updateUserWithPassword(@ModelAttribute("newPasswordAttribute") NewPasswordForm form, HttpSession session) {

        Map<String, String> result = new HashMap<>();

        if(session.getAttribute("userId") == null){
            result.put("errorCode", "403");
            result.put("errorMessage", "no login!");
            return result;
        }

        System.out.println(form.getUserId());
        System.out.println(form.getNewPassword());
        System.out.println(form.getOldPassword());


        if (StringUtils.isEmpty(form.getUserId()) || StringUtils.isEmpty(form.getNewPassword()) || StringUtils.isEmpty(form.getOldPassword())) {
            result.put("errorCode", "500");
            result.put("errorMessage", "password information is must not null!");
            return result;
        }
        String userId = form.getUserId();
        String newPassword = form.getNewPassword();
        String oldPassword = form.getOldPassword();
        String encodeNewPassword = CodecUtils.getMd5Hex(newPassword);
        String encodeOldPassword = CodecUtils.getMd5Hex(oldPassword);
        int updateResult = userService.updateUserWithPassword(userId, encodeOldPassword, encodeNewPassword);
        if(updateResult > 0){
            result.put("result", "SUCCESS");
        } else {
            result.put("result", "FAIL");

        }

        return result;
    }
}
