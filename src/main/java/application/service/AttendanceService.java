package application.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;

import application.config.AppMesssageSource;
import application.dao.MSettingDao;
import application.dao.MUserDao;
import application.dao.TAttendanceDao;
import application.dao.TLineStatusDao;
import application.emuns.AttenanceCd;
import application.emuns.AuthCd;
import application.emuns.DelFlag;
import application.emuns.MenuCd;
import application.entity.MSetting;
import application.entity.MUser;
import application.entity.TAttendance;
import application.entity.TLineStatus;
import application.utils.CommonUtils;

/**
 * 勤怠情報操作サービス。
 */
@Service
@Transactional
public class AttendanceService {
    /** このクラスのロガー。 */
    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

    /** アクション定義：メニュー選択。 */
    public static final String ACTION_OPEN_MENU = "openMenu";

    /** アクション定義：修正 月日入力。 */
    public static final String ACTION_EDIT_DATE = "editDate";
    /** アクション定義：修正 出勤/退勤選択。 */
    public static final String ACTION_EDIT_TYPE_SELECTION = "editTypeSelection";
    /** アクション定義：修正 出勤時間入力。 */
    public static final String ACTION_EDIT_INPUT_TIME_ARRIVAL = "editInputTimeArrival";
    /** アクション定義：修正 退勤時間入力。 */
    public static final String ACTION_EDIT_INPUT_TIME_CLOCKOUT = "editInputTimeClockout";

    //    /** アクション定義：リスト 年月入力。 */
    //    public static final String ACTION_LIST_MONTH = "listMonth";
    /** アクション定義：リスト ユーザ選択(SKIPのケースあり)。 */
    public static final String ACTION_LIST_USER_SELECTION = "listUserSelection";
    //    /** アクション定義：リスト 出力。 */
    //    public static final String ACTION_LIST_OUTPUT = "listOutput";

    /** 勤怠情報DAO。 */
    @Autowired
    private TAttendanceDao tAttendanceDao;

    /** LINEステータス情報DAO。 */
    @Autowired
    private TLineStatusDao tLineStatusDao;

    /** ユーザマスタDAO。 */
    @Autowired
    private MUserDao mUserDao;

    /** 設定マスタDAO。 */
    @Autowired
    private MSettingDao mSettingDao;

    /**
     * LINEメニュー操作を受け付ける。
     * @param menuCd メニュー
     * @return 区分明細情報リスト
     */
    public void requestMenu(MenuCd menuCd, MessageEvent<?> evt, TextMessageContent message) {
        logger.debug("requestMenu() {}", menuCd);
        String replyToken = evt.getReplyToken();
        String lineId = evt.getSource().getUserId();
        // ステータス設定
        TLineStatus lineStatus = getLineSutatus(lineId);
        lineStatus.setMenuCd(menuCd.getDivCd());
        lineStatus.setActionName(ACTION_OPEN_MENU);
        lineStatus.setContents(message.getText());
        setLineSutatus(lineStatus);

        switch (menuCd) {
        case ARRIVAL:
            // 出勤
            putArrivalNow(lineId, replyToken);
            break;
        case CLOCK_OUT:
            // 退勤
            putClockOutNow(lineId, replyToken);
            break;
        case REWRITING:
            // 修正
            String msgRewriting = AppMesssageSource.getMessage("line.editMonthDate");
            LineAPIService.repryMessage(replyToken, msgRewriting);
            break;
        case LIST_OUTPUT:
            // リスト
            String msgOutp0ut = AppMesssageSource.getMessage("line.listYearMonth");
            LineAPIService.repryMessage(replyToken, msgOutp0ut);
            break;
        }
    }

    /**
     * LINEからの文字列入力を受け付ける。
     * @param text 入力内容
     */
    public void requestText(String text, MessageEvent<?> evt, TextMessageContent message) {
        logger.debug("requestText() {}", text);
        String replyToken = evt.getReplyToken();
        String lineId = evt.getSource().getUserId();
        TLineStatus lineStatus = getLineSutatus(lineId);
        MenuCd menuCd = MenuCd.get(lineStatus.getMenuCd());
        switch (menuCd) {
        case REWRITING:
            editAction(lineId, replyToken, lineStatus, text);
            break;
        case LIST_OUTPUT:
            listAction(lineId, replyToken, lineStatus, text);
            break;
        default:
            // ステータス設定
            lineStatus.setMenuCd("empty");
            lineStatus.setActionName(null);
            lineStatus.setContents(text);
            tLineStatusDao.save(lineStatus);

            // 対象なし
            String msg = AppMesssageSource.getMessage("line.selectMenu");
            LineAPIService.repryMessage(replyToken, msg);
            break;
        }
    }

    /**
     * 出勤を保存する。
     * @param lineId LINE識別子
     * @param replyToken リプライTOKEN
     */
    public void putArrivalNow(String lineId, String replyToken) {
        Integer userId = toUserId(lineId);
        String attendanceDay = CommonUtils.toYyyyMmDd();
        TAttendance entity = getTAttendance(userId, AttenanceCd.ARRIVAL.getCode(), attendanceDay);
        if (entity.getAttendanceTime() != null) {
            String msg = AppMesssageSource.getMessage("line.api.err.savedArrival");
            LineAPIService.repryMessage(replyToken, msg);
            return;
        }
        // 保存
        Date attendanceTime = new Date();
        entity.setAttendanceTime(attendanceTime);
        tAttendanceDao.save(entity);

        // 完了メッセージ
        String msg = AppMesssageSource.getMessage("line.arrival", CommonUtils.toHMm(attendanceTime));
        LineAPIService.repryMessage(replyToken, msg);
    }

    /**
     * 退勤を保存する。
     * @param lineId LINE識別子
     * @param replyToken リプライTOKEN
     */
    public void putClockOutNow(String lineId, String replyToken) {
        Integer userId = toUserId(lineId);
        String attendanceDay = CommonUtils.toYyyyMmDd();
        TAttendance entity = getTAttendance(userId, AttenanceCd.CLOCK_OUT.getCode(), attendanceDay);
        if (entity.getAttendanceTime() != null) {
            String msg = AppMesssageSource.getMessage("line.api.err.savedClockOut");
            LineAPIService.repryMessage(replyToken, msg);
            return;
        }

        // 保存
        Date attendanceTime = new Date();
        entity.setAttendanceTime(attendanceTime);
        tAttendanceDao.save(entity);
        // 完了メッセージ
        String msg = AppMesssageSource.getMessage("line.clockOut", CommonUtils.toHMm(attendanceTime));
        LineAPIService.repryMessage(replyToken, msg);
    }

    /**
     * メニュー「編集」を処理する。
     * @param lineId LINE識別子
     * @param replyToken リプライTOKEN
     * @param lineStatus 前回のLINE操作
     * @param text 入力内容
     */
    public void editAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {
        String nextAction = null;
        String msg;
        switch (StringUtils.trimToEmpty(lineStatus.getActionName())) {
        case ACTION_OPEN_MENU:
            // 月日を特定
            String mmDd = CommonUtils.toMonthDate(text);
            if (mmDd == null) {
                msg = AppMesssageSource.getMessage("line.editMonthDate");
                LineAPIService.repryMessage(replyToken, msg);
                return;
            }
            // ボタン送信
            String title = AppMesssageSource.getMessage("line.selectAttendanceCd");
            List<String> buttons = new ArrayList<>();
            buttons.add(AttenanceCd.ARRIVAL.getName());
            buttons.add(AttenanceCd.CLOCK_OUT.getName());
            LineAPIService.pushButtons(lineId, title, buttons);
            lineStatus.setContents(mmDd);
            nextAction = ACTION_EDIT_TYPE_SELECTION;
            break;
        case ACTION_EDIT_TYPE_SELECTION:
            AttenanceCd cd = AttenanceCd.getByName(text);
            Integer userId = toUserId(lineId);
            String mmdd = lineStatus.getContents();
            String yyyymmdd = CommonUtils.toYyyyMmDdByMmDd(mmdd);
            if (cd == null) {
                break;
            }
            // 登録済勤怠を取得
            TAttendance attendance = tAttendanceDao.getByPk(userId, cd.getCode(), yyyymmdd);
            String mmddhhmm;
            if (attendance != null) {
                mmddhhmm = CommonUtils.toMDhMm(attendance.getAttendanceTime());
            } else {
                mmddhhmm = AppMesssageSource.getMessage("word.noneInput");
            }
            // メッセージ送信
            msg = AppMesssageSource.getMessage("line.currentAttendance", cd.getName(), mmddhhmm) + '\n'
                    + AppMesssageSource.getMessage("line.newAttendanceInput", cd.getName());
            LineAPIService.repryMessage(replyToken, msg);
            // 次のアクション指定
            lineStatus.setContents(yyyymmdd);
            if (cd == AttenanceCd.ARRIVAL) {
                nextAction = ACTION_EDIT_INPUT_TIME_ARRIVAL;
            } else {
                nextAction = ACTION_EDIT_INPUT_TIME_CLOCKOUT;
            }
            break;
        case ACTION_EDIT_INPUT_TIME_ARRIVAL:
            nextAction = saveAttendance(lineId, replyToken, lineStatus, text, AttenanceCd.ARRIVAL);
            break;
        case ACTION_EDIT_INPUT_TIME_CLOCKOUT:
            nextAction = saveAttendance(lineId, replyToken, lineStatus, text, AttenanceCd.CLOCK_OUT);
            break;
        }

        if (nextAction == null) {
            // 対象無し
            msg = AppMesssageSource.getMessage("line.selectMenu");
            LineAPIService.repryMessage(replyToken, msg);
        }

        // ステータス更新
        lineStatus.setActionName(nextAction);
        setLineSutatus(lineStatus);
    }

    /**
     * 勤怠情報を保存する。
     * @param lineId LINE識別子
     * @param replyToken リプライTOKEN
     * @param lineStatus 前回のLINE操作
     * @param text 入力内容
     * @param attenanceCd 勤怠区分
     * @return 次のアクションコード
     */
    private String saveAttendance(
            String lineId, String replyToken, TLineStatus lineStatus, String text, AttenanceCd attendanceCd) {
        String nextAction = null;
        String hhmm = CommonUtils.toHourMinute(text);
        Integer userId = toUserId(lineId);
        String yyyyMMdd = lineStatus.getContents();

        if (hhmm == null) {
            String msg = AppMesssageSource.getMessage("line.newAttendanceInput", attendanceCd.getName());
            LineAPIService.repryMessage(replyToken, msg);
            return lineStatus.getActionName();
        }

        // 勤怠取得
        TAttendance attendance = getTAttendance(userId, attendanceCd.getCode(), yyyyMMdd);
        // 勤怠保存
        attendance.setEditFlg(DelFlag.ON.getVal());
        attendance.setAttendanceTime(CommonUtils.parseDate(yyyyMMdd + hhmm, "yyyyMMddHHmm"));
        tAttendanceDao.save(attendance);
        // 保存通知
        String dateTime = CommonUtils.toMDhMm(attendance.getAttendanceTime());
        String msg = AppMesssageSource.getMessage("line.saveAttendance", attendanceCd.getName(), dateTime);
        LineAPIService.repryMessage(replyToken, msg);

        nextAction = ACTION_EDIT_DATE;
        return nextAction;
    }

    /**
     * メニュー「リスト」を処理する。
     * @param lineId リクエスト送信者のLINE識別子
     * @param replyToken リプライTOKEN
     * @param lineStatus 前回のLINE操作
     * @param text 入力内容
     */
    public void listAction(String lineId, String replyToken, TLineStatus lineStatus, String text) {
        String nextAction = null;
        String yyyyMm = null;
        MUser user = mUserDao.getByLineId(lineId);
        String msg;
        switch (StringUtils.trimToEmpty(lineStatus.getActionName())) {
        case ACTION_OPEN_MENU:
            // 年月を特定
            yyyyMm = CommonUtils.toYearMonth(text);
            lineStatus.setContents(yyyyMm);
            if (yyyyMm == null) {
                msg = AppMesssageSource.getMessage("line.listYearMonth");
                LineAPIService.repryMessage(replyToken, msg);
                return;
            }
            if (AuthCd.ADMIN.getCode().equals(user.getAuthCd())) {
                // 全社員の選択肢を送信
                nextAction = pushUserSelectionForAdmin(lineId, yyyyMm);
                if (nextAction == null) {
                    // 選択肢が存在しない場合、自分を出力
                    replyAttendanceList(replyToken, yyyyMm, user.getUserId());
                    nextAction = ACTION_OPEN_MENU;
                }
            } else if (AuthCd.MANAGER.getCode().equals(user.getAuthCd())) {
                // 管理下メンバーのみ
                nextAction = pushUserSelectionForManager(lineId, yyyyMm);
                if (nextAction == null) {
                    // 選択肢が存在しない場合、自分を出力
                    replyAttendanceList(replyToken, yyyyMm, user.getUserId());
                    nextAction = ACTION_OPEN_MENU;
                }
            } else {
                // 管理下メンバーなし(自分のみ)
                replyAttendanceList(replyToken, yyyyMm, user.getUserId());
                // 再入力を許容
                nextAction = ACTION_OPEN_MENU;
            }

            break;
        case ACTION_LIST_USER_SELECTION:
            // 選択したユーザの勤怠を表示
            yyyyMm = lineStatus.getContents();
            Integer targetUserId = CommonUtils.toIntegerSeprator(text, " ", 0);
            if (targetUserId != null) {
                replyAttendanceList(replyToken, yyyyMm, targetUserId);
                // 再選択を許容する
                nextAction = ACTION_LIST_USER_SELECTION;
            } else {
                // 不良操作
                nextAction = null;
            }
            break;
        default:
            // 対象無し
            msg = AppMesssageSource.getMessage("line.selectMenu");
            LineAPIService.repryMessage(replyToken, msg);
            break;
        }
        // ステータス更新
        lineStatus.setActionName(nextAction);
        setLineSutatus(lineStatus);
    }

    /**
     * 管理者として選択可能なユーザの選択肢をプッシュする。<br>
     * ユーザが存在しない場合、自分自身のリストを出力する。
     * @param lineId LINE識別子
     * @param yyyyMm 対象の年月
     * @return 実施アクション名, 選択肢が存在しない場合null
     */
    public String pushUserSelectionForAdmin(String lineId, String yyyyMm) {
        List<MUser> userList = mUserDao.findAdminMembers(lineId, yyyyMm);
        String nextAction = null;
        if (userList.size() >= 2) {
            List<String> buttons = new ArrayList<>();
            for (MUser user : userList) {
                buttons.add(user.userId + " " + user.name);
            }
            String title = AppMesssageSource.getMessage("line.selectUserByList");
            LineAPIService.pushButtons(lineId, title, buttons);
            nextAction = ACTION_LIST_USER_SELECTION;
        }
        return nextAction;
    }

    /**
     * 上長として選択可能なユーザの選択肢をプッシュする。<br>
     * ユーザが存在しない場合、自分自身のリストを出力する。
     * @param lineId LINE識別子
     * @param yyyyMm 対象の年月
     * @param user 上長
     * @return 実施アクション名, 選択肢が存在しない場合null
     */
    public String pushUserSelectionForManager(String lineId, String yyyyMm) {
        List<MUser> userList = mUserDao.findManagerMembers(lineId, yyyyMm);
        String nextAction = null;
        if (userList.size() >= 2) {
            List<String> buttons = new ArrayList<>();
            for (MUser user : userList) {
                buttons.add(user.userId + " " + user.name);
            }
            String title = AppMesssageSource.getMessage("line.selectUserByList");
            LineAPIService.pushButtons(lineId, title, buttons);
            nextAction = ACTION_LIST_USER_SELECTION;
        }
        return nextAction;
    }

    /**
     * １ユーザの勤怠リストをリプライで出力する。
     * @param yyyyMm 対象年月
     * @param userId ユーザID
     */
    private void replyAttendanceList(String replyToken, String yyyyMm, Integer userId) {
        // 管理下メンバーなし(自分のみ)
        String list = getList(userId, yyyyMm);
        LineAPIService.repryMessage(replyToken, list);
    }

    /**
     * 1ヵ月分の勤怠情報を取得する。
     * @param userId ユーザID
     * @param yyyyMm 取得対象年月
     * @return 1ヵ月分の勤怠情報
     */
    private String getList(Integer userId, String yyyyMm) {
        StringBuilder res = new StringBuilder();
        final String ON = DelFlag.ON.getVal();
        // 保存値取得
        MUser user = mUserDao.getByPk(userId);
        if (user == null) {
            return AppMesssageSource.getMessage("line.api.err.notFoundAttendance");
        }

        MSetting setting = mSettingDao.get();
        List<TAttendance> list = tAttendanceDao.selectByMonth(userId, yyyyMm);

        // 日付順のセット＜出勤日＞
        Set<String> dateKeySet = new TreeSet<>();
        // 出勤マップ＜出勤日, 勤怠行＞
        Map<String, TAttendance> openMap = new HashMap<>();
        // 退勤マップ＜出勤日, 勤怠行＞
        Map<String, TAttendance> closeMap = new HashMap<>();
        // 特記マップ＜出勤日, 特記(遅刻早退など)＞
        Map<String, String> optionMap = new HashMap<>();
        // 各マップに集計
        for (TAttendance row : list) {
            String key = row.getAttendanceDay();
            dateKeySet.add(key);
            if (AttenanceCd.ARRIVAL.getCode().equals(row.getAttendanceCd())) {
                openMap.put(key, row);
                String option = getAttendanceOption(setting, row) + optionMap.getOrDefault(key, "");
                optionMap.put(key, option);
            } else {
                closeMap.put(key, row);
                String option = optionMap.getOrDefault(key, "") + getAttendanceOption(setting, row);
                optionMap.put(key, option);
            }
        }

        // タイトル
        res.append(user.getName()).append(' ');
        res.append(CommonUtils.parseDateText(yyyyMm, "yyyyMM", "yyyy年M月"));
        res.append('\n');
        // 表示形式に変換
        for (String attendanceDay : dateKeySet) {
            String date = CommonUtils.parseDateText(attendanceDay, "yyyyMMdd", "M/d");
            // 日付
            res.append(date).append(' ');
            TAttendance open = openMap.get(attendanceDay);
            if (open != null) {
                String openHhMm = CommonUtils.toHMm(open.getAttendanceTime());
                // 出勤日時
                res.append(openHhMm);
                if (ON.equals(open.getEditFlg())) {
                    res.append(AppMesssageSource.getMessage("mark.edit"));
                }
            }
            res.append('～');
            TAttendance close = closeMap.get(attendanceDay);
            if (close != null) {
                String closeHhMm = CommonUtils.toHMm(close.getAttendanceTime());
                // 退勤日時
                res.append(closeHhMm);
                if (ON.equals(close.getEditFlg())) {
                    res.append(AppMesssageSource.getMessage("mark.edit"));
                }
            }
            String option = optionMap.get(attendanceDay);
            if (StringUtils.isNotEmpty(option)) {
                res.append(' ').append(option);
            }

            // LINEの改行
            res.append('\n');
        }
        if (dateKeySet.isEmpty()) {
            res.append(AppMesssageSource.getMessage("line.api.err.notFoundAttendance"));
        }

        return res.toString();
    }

    /**
     * 勤怠情報を取得する。
     * @param userId ユーザID
     * @param attendancdCd 勤怠区分コード
     * @param attendanceDay 出勤日(yyyymmdd形式)
     * @return 勤怠情報。存在しない場合、初期値をセットした新規行
     */
    private TAttendance getTAttendance(Integer userId, String attendancdCd, String attendanceDay) {
        TAttendance res = tAttendanceDao.getByPk(userId, attendancdCd, attendanceDay);
        if (res == null) {
            res = new TAttendance();
            res.setUserId(userId);
            res.setAttendanceCd(attendancdCd);
            res.setAttendanceDay(attendanceDay);
            res.setEditFlg(DelFlag.OFF.getVal());
        }
        return res;
    }

    /**
     * 特記事項を取得する。
     * @param setting 設定
     * @param attendance 勤怠
     * @return 特記事項(早退,遅刻,休出)
     */
    private String getAttendanceOption(MSetting setting, TAttendance attendance) {
        StringBuilder res = new StringBuilder();

        Set<Integer> businessDay = getBusinessDay(setting);
        Calendar datetime = Calendar.getInstance(CommonUtils.JST);
        datetime.setTime(attendance.getAttendanceTime());
        Integer day = datetime.get(Calendar.DAY_OF_WEEK);
        boolean isBusinessDay = businessDay.contains(day);

        if (AttenanceCd.ARRIVAL.getCode().equals(attendance.getAttendanceCd())) {
            // 出勤
            if (isBusinessDay) {
                String openStr = attendance.getAttendanceDay() + setting.getOpenTime() + setting.getOpenMinutes();
                Date openDatetime = CommonUtils.parseDate(openStr, "yyyyMMddHHmm");
                if (openDatetime.getTime() < attendance.getAttendanceTime().getTime()) {
                    // 遅刻
                    res.append(AppMesssageSource.getMessage("word.lateStart"));
                }
            } else {
                // 休出
                res.append(AppMesssageSource.getMessage("word.holidayWork"));
            }
        } else {
            // 退勤
            if (isBusinessDay) {
                String closeStr = attendance.getAttendanceDay() + setting.getCloseTime() + setting.getCloseMinutes();
                Date closeDatetime = CommonUtils.parseDate(closeStr, "yyyyMMddHHmm");
                if (attendance.getAttendanceTime().getTime() < closeDatetime.getTime()) {
                    // 早退
                    res.append(AppMesssageSource.getMessage("word.leaveEarly"));
                }
            }
        }

        return res.toString();
    }

    /**
     * 営業曜日を取得する。
     * @param setting 設定
     * @return 営業曜日セット＜Calendarクラスの曜日＞
     */
    private Set<Integer> getBusinessDay(MSetting setting) {
        Set<Integer> res = new HashSet<>();
        final String ON = DelFlag.ON.getVal();
        if (ON.equals(setting.getBusinessFlagSun())) {
            res.add(Calendar.SUNDAY);
        }
        if (ON.equals(setting.getBusinessFlagMon())) {
            res.add(Calendar.MONDAY);
        }
        if (ON.equals(setting.getBusinessFlagTue())) {
            res.add(Calendar.TUESDAY);
        }
        if (ON.equals(setting.getBusinessFlagWed())) {
            res.add(Calendar.WEDNESDAY);
        }
        if (ON.equals(setting.getBusinessFlagThu())) {
            res.add(Calendar.THURSDAY);
        }
        if (ON.equals(setting.getBusinessFlagFri())) {
            res.add(Calendar.FRIDAY);
        }
        if (ON.equals(setting.getBusinessFlagSat())) {
            res.add(Calendar.SATURDAY);
        }
        return res;
    }

    /**
     * LINE操作を保存する。
     * @param lineStatus LINEステータス
     */
    private void setLineSutatus(TLineStatus lineStatus) {
        lineStatus.setRequestTime(new Date());
        tLineStatusDao.save(lineStatus);
    }

    /**
     * 前回のLINE操作を取得する。
     * @param lineId 送信元LINE識別子
     * @return LINEステータス。存在しない場合、初期値をセットした新規行
     */
    private TLineStatus getLineSutatus(String lineId) {
        TLineStatus res = tLineStatusDao.getByPk(lineId);
        if (res == null) {
            res = new TLineStatus();
            res.setLineId(lineId);
            MUser user = mUserDao.getByLineId(lineId);
            res.setUserId(user.getUserId());
        }
        return res;
    }

    /**
     * LINE識別子をユーザIDに変換する。
     * @param lineId LINE識別子
     * @return ユーザID
     */
    private Integer toUserId(String lineId) {
        MUser user = mUserDao.getByLineId(lineId);
        Integer res = null;
        if (user != null) {
            res = user.getUserId();
        }
        return res;
    }

}
