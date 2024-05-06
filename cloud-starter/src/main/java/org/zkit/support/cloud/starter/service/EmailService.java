package org.zkit.support.cloud.starter.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.zkit.support.cloud.starter.configuration.AliDMConfiguration;
import org.zkit.support.cloud.starter.entity.EmailData;

import java.text.MessageFormat;
import java.util.Locale;

@Component
@Slf4j
public class EmailService {

    private AliDMConfiguration configuration;
    private FreemarkerService freemarkerService;

    public boolean singleSendMail(EmailData data){
        // 如果是除杭州region外的其它region（如新加坡、澳洲Region），需要将下面的"cn-hangzhou"替换为"ap-southeast-1"、或"ap-southeast-2"。
        //请在环境变量中配置ALIBABA_CLOUD_ACCESS_KEY_ID，ALIBABA_CLOUD_ACCESS_KEY_SECRET。
        //参考文档：https://help.aliyun.com/document_detail/2361895.html
        IClientProfile profile = DefaultProfile.getProfile(configuration.getRegionId(), configuration.getAccessKeyId(), configuration.getAccessKeySecret());
        // 如果是除杭州region外的其它region（如新加坡region）， 需要做如下处理
        //try {
        //DefaultProfile.addEndpoint("dm.ap-southeast-1.aliyuncs.com", "ap-southeast-1", "Dm",  "dm.ap-southeast-1.aliyuncs.com");
        //} catch (ClientException e) {
        //e.printStackTrace();
        //}
        IAcsClient client = new DefaultAcsClient(profile);//建议初始化一次就可以，初始化多次也不影响功能，但是性能有损失
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            //request.setVersion("2017-06-22");// 如果是除杭州region外的其它region（如新加坡region）,必须指定为2017-06-22
            request.setAccountName(configuration.getAccountName());
            //request.setFromAlias("发信人昵称");//发信人昵称，长度小于15个字符。
            request.setAddressType(1);//0：为随机账号 1：为发信地址
            //request.setTagName("控制台创建的标签");
            request.setReplyToAddress(false);// 是否启用管理控制台中配置好回信地址（状态须验证通过），取值范围是字符串true或者false
            request.setToAddress(data.getEmail());
            //可以给多个收件人发送邮件，收件人之间用逗号分开，批量发信建议使用BatchSendMailRequest方式
            //request.setToAddress("邮箱1,邮箱2");
            request.setSubject(data.getSubject());
            //如果采用byte[].toString的方式的话请确保最终转换成utf-8的格式再放入htmlbody和textbody，若编码不一致则会被当成垃圾邮件。
            //注意：文本邮件的大小限制为3M，过大的文本会导致连接超时或413错误
            Locale locale = LocaleContextHolder.getLocale();
            String localeCode = MessageFormat.format("{0}_{1}", locale.getLanguage(), locale.getCountry());
            String html = freemarkerService.render("mail/" + data.getTemplate() + "_" + localeCode + ".ftl", data.getData());
            log.info(html);
            request.setHtmlBody(html);
            //SDK 采用的是http协议的发信方式, 默认是GET方法，有一定的长度限制。
            //若textBody、htmlBody或content的大小不确定，建议采用POST方式提交，避免出现uri is not valid异常
            //request.setMethod(MethodType.POST);
            //开启需要备案，0关闭，1开启
            //request.setClickTrace("0");
            //如果调用成功，正常返回httpResponse；如果调用失败则抛出异常，需要在异常中捕获错误异常码；错误异常码请参考对应的API文档;
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
            log.info(httpResponse.getRequestId());
        } catch (Exception e) {
            //捕获错误异常码
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Autowired
    public void setConfiguration(AliDMConfiguration configuration) {
        this.configuration = configuration;
    }

    @Autowired
    public void setFreemarkerService(FreemarkerService freemarkerService) {
        this.freemarkerService = freemarkerService;
    }
}
