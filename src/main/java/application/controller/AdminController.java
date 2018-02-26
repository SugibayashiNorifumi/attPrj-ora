package application.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import application.entity.MSetting;
import application.form.SettingForm;
import application.service.OrgService;
import application.service.SettingService;
import application.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * 管理画面コントローラ
 */
@Slf4j
@Controller
@RequestMapping(value="/admin")
public class AdminController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private SettingService settingService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        return "admin/login";
    }

    @RequestMapping(value = "/login-error", method = RequestMethod.GET)
    public String loginError(Model model) {
        model.addAttribute("loginFailedErrorMsg", "ユーザIDまたはパスワードが正しくありません。");
        return "admin/login";
    }

    /**
     * ユーザ・組織管理画面表示
     * @return
     */
    @RequestMapping(value = "/user-org")
    public String getUserOrg() {
        return "admin/user-org";
    }

    /**
     * 組織検索
     *
     * @return
     */
    @RequestMapping(value = "/find-orgs", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> findOrgs() {

        Map<String, Object> res = new HashMap<>();

        res.put("results", orgService.findOrgs());

        return res;
    }

    /**
     * ユーザ検索
     *
     * @return
     */
    @RequestMapping(value = "/find-users", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> findUsers(@RequestParam(required = false) String orgCd) {

        Map<String, Object> res = new HashMap<>();

        res.put("results", userService.findUsers(orgCd));

        return res;
    }

    /**
     *
     * 設定画面
     *
     * @param settingForm
     * @param model
     * @return
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String setting(@ModelAttribute SettingForm settingForm,
                          Model model) {

        MSetting mSetting = settingService.getSetting().orElse(new MSetting());
        log.info("msetting : {} :", mSetting);
        modelMapper.map(mSetting, settingForm);

        log.info("settingForm : {} :", settingForm);

        return "admin/setting";
    }

    /**
     *
     * 設定情報を登録する
     *
     * @param settingForm
     * @param bindingResult
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public String saveSetting(@ModelAttribute @Valid SettingForm settingForm,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return "admin/setting";
        }

        MSetting setting = modelMapper.map(settingForm, MSetting.class);

        settingService.registerSetting(setting);

        redirectAttributes.addFlashAttribute("updateSuccessMsg", "保存が完了しました");

        return "redirect:/admin/setting";
    }
}
