package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author 作成者氏名
 *
 */
@SpringBootApplication
public class WebApplication {

    /**
     * Webアプリケーションのエントリポイント
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(WebApplication.class, args);
    }
}
