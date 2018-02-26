package application.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * WEBアプリケーション設定クラス
 * @author 作成者氏名
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean(name="validator")
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource());
        return localValidatorFactoryBean;
    }

    /**
     * メッセージをUTF-8で管理する。
     * @return
     */
    @Bean(name = "messageSource")
    public MessageSource messageSource()
    {
        ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
        bean.setBasename("classpath:Messages_jp");
        bean.setDefaultEncoding("UTF-8");
        return bean;
    }

}
