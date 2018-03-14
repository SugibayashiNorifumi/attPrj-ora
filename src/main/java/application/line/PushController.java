package application.line;

import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class PushController {

    private final LineMessagingClient lineMessagingClient;

    PushController(LineMessagingClient lineMessagingClient) {
        this.lineMessagingClient = lineMessagingClient;
    }

    //リマインドをプッシュ
    @GetMapping("api_test")
    public void pushAlarm() throws InterruptedException, ExecutionException {

            BotApiResponse response = lineMessagingClient
                                            .pushMessage(new PushMessage("Ua9278df07f465e26b38f8afee0d76635",
                                                         new TemplateMessage("出勤確認",
                                                                 new ConfirmTemplate("出勤しますか？",
                                                                         new PostbackAction("ラベル", "データ"),
                                                                         new MessageAction("いいえ", "いいえ")
                                                                 )
                                                         )))
                                            .get();
            log.debug("Sent messages: {}", response);

    }
}