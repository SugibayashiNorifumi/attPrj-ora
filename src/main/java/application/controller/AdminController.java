package application.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import application.dto.DayAttendance;
import application.entity.MOrg;
import application.entity.MSetting;
import application.entity.MUser;
import application.form.ListOutputForm;
import application.form.OrgForm;
import application.form.SettingForm;
import application.form.UserForm;
import application.service.DivisionService;
import application.service.ListOutputService;
import application.service.OrgService;
import application.service.SettingService;
import application.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * 管理者向け機能用 画面コントローラ.
 *
 * @author 作成者氏名
 *
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private DivisionService divisionService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ListOutputService listOutputService;

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
     * ユーザ・組織管理画面表示.
     *
     * @return String
     */
    @RequestMapping(value = "/user-org")
    public String getUserOrg(Model model) {
        model.addAttribute("authList", divisionService.getAuthList());
        return "admin/user-org";
    }

    /**
     * 組織検索
     *
     * @return
     */
    @RequestMapping(value = "/find-orgs", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findOrgs() {

        Map<String, Object> res = new HashMap<>();

        res.put("results", orgService.findOrgs(null));

        return res;
    }

    /**
     * ユーザ検索
     *
     * @return
     */
    @RequestMapping(value = "/find-users", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findUsers(@RequestParam(required = false) String orgCd) {

        Map<String, Object> res = new HashMap<>();

        res.put("results", userService.findUsers(orgCd, null));

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

        modelMapper.map(mSetting, settingForm);

        log.debug("settingForm : {} :", settingForm);

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
        if (bindingResult.hasErrors()) {
            return "admin/setting";
        }

        MSetting setting = modelMapper.map(settingForm, MSetting.class);

        settingService.registerSetting(setting);

        redirectAttributes.addFlashAttribute("updateSuccessMsg", "保存が完了しました");

        return "redirect:/admin/setting";
    }

    /**
     * 組織を登録する。
     * @param orgForm 組織フォーム
     * @return 組織一覧
     */
    @RequestMapping(value = "/orgs", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerOrg(OrgForm orgForm) {

        log.debug("requested org form: {}", orgForm);

        MOrg mOrg = modelMapper.map(orgForm, MOrg.class);

        orgService.registerOrg(mOrg);

        Map<String, Object> res = new HashMap<>();

        res.put("status", "OK");

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * ユーザを登録する。
     * @param userForm ユーザフォーム
     * @param bindingResult バインド結果
     * @return ユーザ登録結果
     */
    @RequestMapping(value = "/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerUser(@Valid UserForm userForm,
            BindingResult bindingResult) {

        log.debug("requested user form: {}", userForm);

        if (bindingResult.hasErrors()) {
            return genValidationErrorResponse(bindingResult);
        }

        MUser mUser = modelMapper.map(userForm, MUser.class);

        userService.registerUser(mUser);

        Map<String, Object> res = new HashMap<>();

        res.put("status", "OK");

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * 組織選択Select2データソースを取得する
     * @return
     */
    @RequestMapping(value = "/orgs/select2", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getOrgSelect2Data(@RequestParam(required = false) String name) {

        log.debug("request param name: {}", name);

        Map<String, Object> res = new HashMap<>();

        List<Map<String, Object>> data = orgService.findOrgs(name).stream()
                .map(org -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", org.getOrgCd());
                    item.put("text", org.getOrgName());
                    return item;
                }).collect(Collectors.toList());

        res.put("results", data);

        return res;
    }

    /**
     * ユーザ選択Select2データソースを取得する
     * @return
     */
    @RequestMapping(value = "/users/select2", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getUserSelect2Data(@RequestParam(required = false) String orgCd,
            @RequestParam(required = false) String name) {

        log.debug("request param name: {}", name);

        Map<String, Object> res = new HashMap<>();

        List<Map<String, Object>> data = userService.findUsers(orgCd, name).stream()
                .map(user -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", user.userId);
                    item.put("text", user.name);
                    return item;
                }).collect(Collectors.toList());

        res.put("results", data);

        return res;
    }

    /**
     * 権限選択Select2データソースを取得する
     * @return
     */
    @RequestMapping(value = "/auths/select2", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getAuthSelect2Data() {
        Map<String, Object> res = new HashMap<>();

        List<Map<String, Object>> data = divisionService.getAuthList().stream()
                .map(auth -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", auth.getDivCd());
                    item.put("text", auth.getDivCdContent());
                    return item;
                }).collect(Collectors.toList());

        res.put("results", data);

        return res;
    }

    /**
     * リスト出力画面を表示する。
     * @param listOutputForm リスト出力フォーム
     * @param model モデル
     * @return リスト出力画面
     */
    @RequestMapping(value = "/listOutput", method = RequestMethod.GET)
    public String listOutput(@ModelAttribute ListOutputForm listOutputForm, Model model) {

        //    	listOutputForm.setOutputYearMonth("");

        log.debug("listOutputForm : {} :", listOutputForm);

        return "admin/listOutput";
    }

    /**
     * 勤怠情報をCSV形式で出力.
     * @param listOutputForm リスト出力フォーム
     * @param model モデル
     * @return CSV形式の勤怠情報
     * @throws JsonProcessingException CSV変換時の例外
     */
    @RequestMapping(value = "/attendance.csv", method = RequestMethod.GET, produces = "text/csv; charset=SHIFT-JIS; Content-Disposition: attachment")
    @ResponseBody
    public Object attendanceCsv(@Valid ListOutputForm listOutputForm,
            BindingResult bindingResult,
            Model model) throws JsonProcessingException {
        log.debug("attendanceCsv : {} :", listOutputForm);
        CsvMapper mapper = new CsvMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        CsvSchema schema = mapper.schemaFor(DayAttendance.class).withHeader();
        return mapper.writer(schema)
                .writeValueAsString(listOutputService.getDayAttendanceList(listOutputForm.outputYearMonth));
    }

    /**
     * エンコードされたパスワード確認
     * @param passwords 平文パスワード
     * @return
     */
    @RequestMapping(value = "/encodePassword", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> encodePassword(@RequestParam(name = "passwords") String passwords) {
        return Arrays.stream(passwords.split(","))
                .collect(Collectors.toMap(
                        password -> password,
                        password -> passwordEncoder.encode(password)));
    }

    /**
     * Validation結果のBindingResultからエラーレスポンスエンティティを生成する。
     * @param result BindingResultインスタンス
     * @return ResponseEntity
     */
    private ResponseEntity<Map<String, Object>> genValidationErrorResponse(BindingResult result) {
        Map<String, List> errors = result.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, error -> new ArrayList<>(Arrays.asList(error)),
                        (a, b) -> {
                            a.add(b);
                            return a;
                        }));

        Map<String, Object> errorRes = new HashMap<>();
        errorRes.put("status", "NG");
        errorRes.put("errors", errors);

        return new ResponseEntity(errorRes, HttpStatus.BAD_REQUEST);
    }
}
