package cn.nfsn.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class IndexController {

    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("socket.html");
        mav.addObject("uid", 1231);
        return mav;
    }
}