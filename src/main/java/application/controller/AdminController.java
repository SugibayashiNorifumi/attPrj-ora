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

import application.entity.MSetting;
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
 * 管理者向け機能用 画面コントローラ。
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
     * @return 画面のパス
     */
    @RequestMapping(value = "/user-org")
    public String getUserOrg(Model model) {
        return "admin/user-org";
    }

    /**
     * 組織検索を実行する。
     *
     * @return 組織検索結果
     */
    @RequestMapping(value = "/find-orgs", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findOrgs() {

        return null;
    }

    /**
     * 組織を取得する。
     * @param orgCd 組織コード
     * @return 組織情報
     */
    @RequestMapping(value = "/find-org", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findOrg(@RequestParam(required = true) String orgCd) {

        return null;
    }


    /**
     * ユーザ検索を実行する。
     *
     * @return ユーザ検索結果
     */
    @RequestMapping(value = "/find-users", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findUsers(@RequestParam(required = false) String orgCd) {

        return null;
    }

    /**
     * ユーザを取得する。
     * @param userId ユーザID
     * @return 組織情報
     */
    @RequestMapping(value = "/find-user", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findUser(@RequestParam(required = true) Integer userId) {

        return null;
    }

    /**
     * 設定画面を開く。
     *
     * @param settingForm 設定フォーム
     * @return 画面のパス
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String setting(@ModelAttribute SettingForm settingForm) {

        MSetting mSetting = settingService.getSetting().orElse(new MSetting());

        modelMapper.map(mSetting, settingForm);

        log.debug("settingForm : {} :", settingForm);

        return "admin/setting";
    }

    /**
     * 設定情報を登録する。
     * @param settingForm 設定フォーム
     * @param bindingResult バインド結果
     * @param redirectAttributes リダイレクト先にパラメータを渡すためのオブジェクト
     * @return 画面のパス
     */
    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public String saveSetting(@ModelAttribute @Valid SettingForm settingForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
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
     * @return 登録結果
     */
    @RequestMapping(value = "/orgs", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerOrg(OrgForm orgForm) {

        return null;
    }

    /**
     * 組織を更新する。
     * @param orgForm 組織フォーム
     * @return 更新結果
     */
    @RequestMapping(value = "/org-update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrgs(OrgForm orgForm) {

        return null;
    }

    /**
     * 組織を削除する。
     * @param orgCd 組織コード
     * @return 削除結果
     */
    @RequestMapping(value = "/org-delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteOrg(@RequestParam(value = "orgCd") String orgCd) {

        return null;
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

        return null;
    }

    /**
     * ユーザを更新する。
     * @param userForm ユーザフォーム
     * @return 更新結果
     */
    @RequestMapping(value = "/user-update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUser(UserForm userForm) {

        return null;
    }

    /**
     * ユーザを削除する。
     * @param userIds ユーザID
     * @return 削除結果
     */
    @RequestMapping(value = "/user-delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestParam(value = "userIds") List<String> userIds) {

        return null;
    }

    /**
     * 組織選択Select2データソースを取得する。
     * @return 組織選択Select2データソース
     */
    @RequestMapping(value = "/orgs/select2", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getOrgSelect2Data(@RequestParam(required = false) String name) {

        return null;
    }

    /**
     * ユーザ選択Select2データソースを取得する。
     * @return ユーザ選択Select2データソース
     */
    @RequestMapping(value = "/users/select2", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getUserSelect2Data(@RequestParam(required = false) String orgCd,
            @RequestParam(required = false) String name) {

        return null;
    }

    /**
     * 権限選択Select2データソースを取得する。
     * @return 権限選択Select2データソース
     */
    @RequestMapping(value = "/auths/select2", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getAuthSelect2Data() {

    	return null;
    }

    /**
     * リスト出力画面を表示する。
     * @param listOutputForm リスト出力フォーム
     * @param model モデル
     * @return リスト出力画面
     */
    @RequestMapping(value = "/listOutput", method = RequestMethod.GET)
    public String listOutput(@ModelAttribute ListOutputForm listOutputForm, Model model) {

        return null;
    }

    /**
     * 勤怠情報をCSV形式で出力する.
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

        return null;
    }

    /**
     * パスワードをハッシュ化する。
     * @param passwords 平文パスワード
     * @return ハッシュ化パスワード
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

        return new ResponseEntity<Map<String, Object>>(errorRes, HttpStatus.BAD_REQUEST);
    }
}
