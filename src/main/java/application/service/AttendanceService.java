package application.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;

import application.config.AppMesssageSource;
import application.dao.MUserDao;
import application.dao.TAttendanceDao;
import application.dao.TLineStatusDao;
import application.emuns.AttenanceCd;
import application.emuns.DelFlag;
import application.emuns.MenuCd;
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

    /** 勤怠情報DAO。 */
    @Autowired
    private TAttendanceDao tAttendancedDao;

    /** LINEステータス情報DAO。 */
    @Autowired
    private TLineStatusDao tLineStatusDao;

    /** ユーザマスタDAO。 */
    @Autowired
    private MUserDao mUserDao;

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
        lineStatus.setActionName(null);
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
            break;
        case LIST_OUTPUT:
            // リスト
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

        // ステータス設定
        lineStatus.setMenuCd("empty");
        lineStatus.setActionName(null);
        lineStatus.setContents(null);
        tLineStatusDao.save(lineStatus);

        // メニューから操作してください
        String msg = AppMesssageSource.getMessage("line.selectMenu");
        LineAPIService.repryMessage(replyToken, msg);
    }

    /**
     * 出勤をマークする。
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
        tAttendancedDao.save(entity);

        // 完了メッセージ
        String msg = AppMesssageSource.getMessage("line.arrival", CommonUtils.toHMm(attendanceTime));
        LineAPIService.repryMessage(replyToken, msg);
    }

    /**
     * 退勤をマークする。
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
        tAttendancedDao.save(entity);
        // 完了メッセージ
        String msg = AppMesssageSource.getMessage("line.clockOut", CommonUtils.toHMm(attendanceTime));
        LineAPIService.repryMessage(replyToken, msg);
    }

    /**
     * 勤怠情報を取得する。
     * @param userId ユーザID
     * @param attendancdCd 勤怠区分コード
     * @param attendanceDay 出勤日(yyyymmdd形式)
     * @return 勤怠情報。存在しない場合、初期値をセットした新規行
     */
    private TAttendance getTAttendance(Integer userId, String attendancdCd, String attendanceDay) {
        TAttendance res = tAttendancedDao.getByPk(userId, attendancdCd, attendanceDay);
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
