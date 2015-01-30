package org.whut.platform.fundamental.mail;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-28
 * Time: 下午8:34
 * To change this template use File | Settings | File Templates.
 */


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.whut.platform.fundamental.config.Constants;
import org.whut.platform.fundamental.config.FundamentalConfigProvider;
import org.whut.platform.fundamental.logger.PlatformLogger;

/**
 * MIME邮件服务类.
 *
 * 生成的的html格式邮件, 并带有附件
 *
 * @author calvin quanxiwei
 *
 */
public class MailSender implements InitializingBean {
    String ATTACHMENT_URL = "attachment_url";
    String ATTACHMENT_NAME = "attachment_name";

    private static final String DEFAULT_ENCODING = "utf-8";
    private static final PlatformLogger LOGGER = PlatformLogger
            .getLogger(MailSender.class);
    private JavaMailSender mailSender;
    private boolean mailInTest;
    private boolean sendTestMail;
    private String testMailSendTo;
    private String systemMailSender;
    private String encode;
    private String bccMail = "";
    private String ccMail = "";
    private MailContentEngine mailContentEngine;

    public void sendMail(String receivers, String subject, String content) {
        sendMail(systemMailSender, receivers, subject, content);
    }

    public void sendMail(String sender, String receivers, String subject,
                         String content) {
        sendMail(sender, receivers, subject, content, null);
    }

    public void sendMail(String sender, String receivers, String subject,
                         String content, Map<String, String> pros) {
        sendMail(sender, receivers, ccMail, bccMail, subject, content, pros);
    }

    /**
     *
     * @param senderForPass
     * @param receiversForPass
     * @param cc
     * @param bcc
     * @param subjectForPass
     * @param contentForPass
     * @param map
     */
    public void sendMail(String senderForPass, String receiversForPass,
                         String cc, String bcc, String subjectForPass,
                         String contentForPass, Map<String, String> map) {
        String subject = subjectForPass;
        String sender = senderForPass;
        String receivers = receiversForPass;
        String content = contentForPass;
        if (isIllegal(receivers, subject, content)) {
            return;
        }
        Map<String, String> pros = ObjectUtils.equals(map, null) ? new HashMap<String, String>(
                0) : map;
        this.setDefaultProperty(pros);

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true,
                    this.encode);

            // 如果处于测试中
            if (mailInTest) {
                // 如果配置了不发送邮件
                if (!sendTestMail) {
                    LOGGER.info("mail [" + subject
                            + "] is in Test and doesn't send to [" + receivers
                            + "]...");

                    return;
                }

                subject = "[测试邮件]" + subject;
                content = new StringBuilder(content).append("<br />收件人：")
                        .append(receivers).append("<br />抄送: ").append(cc)
                        .append("<br />暗送: ").append(bcc).toString();
                receivers = testMailSendTo;
            }
            // 如果不是测试中，加上cc和bcc
            else {
                if (StringUtils.isNotBlank(bcc)) {
                    helper.setBcc(bcc);
                }
                if (StringUtils.isNotBlank(cc)) {
                    helper.setCc(cc);
                }
            }

            if (StringUtils.isBlank(sender)) {
                sender = systemMailSender;
            }

            helper.setTo(StringUtils.split(receivers, ","));
            helper.setFrom(sender);
            helper.setSubject(subject);

            String emailText = generateContent(content, pros);
            helper.setText(emailText, true);

            // 根据参数来判断是否需要加上附件内容
            if (StringUtils.isNotBlank(pros.get(ATTACHMENT_URL))) {
                File attachment = generateAttachment(pros.get(ATTACHMENT_URL));
                helper.addAttachment(pros.get(ATTACHMENT_NAME), attachment);
            }

            ClassLoader classLoader = Thread.currentThread()
                    .getContextClassLoader();
            Thread.currentThread().setContextClassLoader(
                    MimeMessage.class.getClassLoader());
            mailSender.send(msg);
            Thread.currentThread().setContextClassLoader(classLoader);

            LOGGER.info("邮件已发送至:-->" + receivers);
        } catch (MessagingException e) {
            LOGGER.error("构造邮件失败", e);
        } catch (Exception e) {
            LOGGER.error("发送邮件失败,receiver:-->" + receivers, e);
        }
    }

    /**
     *
     * @param receivers
     * @param subject
     * @param content
     * @return
     */
    private boolean isIllegal(String receivers, String subject, String content) {
        if (StringUtils.isBlank(receivers)) {
            LOGGER.info("接收人为空，邮件发送前验证失败。");
            return true;
        }

        if (StringUtils.isBlank(subject)) {
            LOGGER.info("邮件标题subject为空，邮件发送前验证失败。");
            return true;
        }

        if (StringUtils.isBlank(content)) {
            LOGGER.info("邮件内容content为空，邮件发送前验证失败。");
            return true;
        }

        return false;
    }

    private void setDefaultProperty(Map<String, String> pros) {
        this.encode = ObjectUtils.toString(StringUtils.trimToNull(this.encode),
                DEFAULT_ENCODING);
        if (StringUtils.isNotBlank(pros.get(ATTACHMENT_URL))
                && StringUtils.isBlank(pros.get(ATTACHMENT_NAME))) {
            LOGGER.info("邮件附件存在url，但是没有设置名称，置为默认值：附件.txt");
            pros.put(ATTACHMENT_NAME, "附件.txt");
        }
    }

    /**
     * 使用简单的方式，生成html格式内容.占位符替换
     */
    private String generateContent(String template, Map<String, String> pros)
            throws MessagingException {
        return mailContentEngine.generateContent(template, pros);
    }

    /**
     * 获取classpath中的附件.
     */
    private File generateAttachment(String attachmentPath)
            throws MessagingException {
        return new File(attachmentPath);
    }

    /**
     * Spring的MailSender.
     */
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 是否在测试中
     *
     * @param mailInTest
     */
    public void setMailInTest(boolean mailInTest) {
        this.mailInTest = mailInTest;
    }

    /**
     * 测试时候是否需要发送email
     *
     * @param sendTestMail
     */
    public void setSendTestMail(boolean sendTestMail) {
        this.sendTestMail = sendTestMail;
    }

    /**
     * 测试时的email发送给谁
     *
     * @param testMailSendTo
     */
    public void setTestMailSendTo(String testMailSendTo) {
        this.testMailSendTo = testMailSendTo;
    }

    /**
     * 系统sender
     *
     * @param systemMailSender
     */
    public void setSystemMailSender(String systemMailSender) {
        this.systemMailSender = systemMailSender;
    }

    /**
     * mail的内容生成引擎（模板解析）
     *
     * @param mailContentEngine
     */
    public void setMailContentEngine(MailContentEngine mailContentEngine) {
        this.mailContentEngine = mailContentEngine;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public void setBccMail(String bccMail) {
        this.bccMail = bccMail;
    }

    public void setCcMail(String ccMail) {
        this.ccMail = ccMail;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(FundamentalConfigProvider
                .get(Constants.SMTP_HOST));
        mailSenderImpl.setPort(Integer.parseInt(FundamentalConfigProvider.get(Constants.SMTP_PORT).trim()));
        mailSenderImpl.setUsername(FundamentalConfigProvider
                .get(Constants.SMTP_USERNAME));
        mailSenderImpl.setPassword(FundamentalConfigProvider
                .get(Constants.SMTP_PASSWORD));
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.auth",
                FundamentalConfigProvider.get(Constants.SMTP_AUTH));
        mailSenderImpl.setJavaMailProperties(javaMailProperties);

        setMailSender(mailSenderImpl);

        setEncode(FundamentalConfigProvider.get(Constants.EMAIL_ENCODE));
        setSystemMailSender(FundamentalConfigProvider
                .get(Constants.EMAIL_DEFAULT_SENDER));
        setBccMail(FundamentalConfigProvider.get(Constants.EMAIL_BCC_MAIL));
        setCcMail(FundamentalConfigProvider.get(Constants.EMAIL_CC_MAIL));
        setMailInTest(FundamentalConfigProvider
                .getBoolean(Constants.EMAIL_MAIL_IN_TEST));
        setSendTestMail(FundamentalConfigProvider
                .getBoolean(Constants.EMAIL_SEND_TEST_MAIL));
        setTestMailSendTo(FundamentalConfigProvider
                .get(Constants.EMAIL_TEST_MAIL_SENDTO));

        mailContentEngine = new MailContentEngine();
    }

}
