package models.Utility;

import models.Users;
import play.Configuration;

public class Template {

    public static String urlString = "http://" + Configuration.root().getString("server.hostname");
    public static String registrationEmail = "Hi there account was created successfully created. You can login any time to the system.Thank you!";
    public static String transcriptEnabled = "Dear student, the transcript is enabled. You can login ILPD MIS to access your transcript.Thank you!";
    public static String transcriptPrintEnabled = "Dear student, the transcript printing is enabled. You can login ILPD MIS to access print button.Thank you!";
    public static String marksApproved = "Dear student, the marks results are approved by Registrar. You can login ILPD MIS to access your result.Thank you!";
    public static String appEmail = "Your account is successfully created";
    public static String newEmailCreated = "Dear student, kindly verify notification from ILPD MIS.";

    public static String registrationConfirmation(Users users , String emailSet ) {
        String body = ("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "    <!--[if !mso]><!--><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" /><!--<![endif]-->\n" +
                "    <meta name=\"viewport\" content=\"width=device-width\" />\n" +
                "    <title> </title>\n" +
                "    <style type=\"text/css\">\n" +
                ".btn a:hover,\n" +
                ".footer__links a:hover {\n" +
                "  opacity: 0.8;\n" +
                "}\n" +
                ".wrapper .footer__share-button:hover {\n" +
                "  color: #ffffff !important;\n" +
                "  opacity: 0.8;\n" +
                "}\n" +
                "a[x-apple-data-detectors] {\n" +
                "  color: inherit !important;\n" +
                "  text-decoration: none !important;\n" +
                "  font-size: inherit !important;\n" +
                "  font-family: inherit !important;\n" +
                "  font-weight: inherit !important;\n" +
                "  line-height: inherit !important;\n" +
                "}\n" +
                ".column {\n" +
                "  padding: 0;\n" +
                "  text-align: left;\n" +
                "  vertical-align: top;\n" +
                "}\n" +
                ".mso .font-avenir,\n" +
                ".mso .font-cabin,\n" +
                ".mso .font-open-sans,\n" +
                ".mso .font-ubuntu {\n" +
                "  font-family: sans-serif !important;\n" +
                "}\n" +
                ".mso .font-bitter,\n" +
                ".mso .font-merriweather,\n" +
                ".mso .font-pt-serif {\n" +
                "  font-family: Georgia, serif !important;\n" +
                "}\n" +
                ".mso .font-lato,\n" +
                ".mso .font-roboto {\n" +
                "  font-family: Tahoma, sans-serif !important;\n" +
                "}\n" +
                ".mso .font-pt-sans {\n" +
                "  font-family: \"Trebuchet MS\", sans-serif !important;\n" +
                "}\n" +
                ".mso .footer p {\n" +
                "  margin: 0;\n" +
                "}\n" +
                "@media only screen and (-webkit-min-device-pixel-ratio: 2), only screen and (min--moz-device-pixel-ratio: 2), only screen and (-o-min-device-pixel-ratio: 2/1), only screen and (min-device-pixel-ratio: 2), only screen and (min-resolution: 192dpi), only screen and (min-resolution: 2dppx) {\n" +
                "  .fblike {\n" +
                "    background-image: url(https://i7.createsend1.com/static/eb/master/13-the-blueprint-3/images/fblike@2x.png) !important;\n" +
                "  }\n" +
                "  .tweet {\n" +
                "    background-image: url(https://i8.createsend1.com/static/eb/master/13-the-blueprint-3/images/tweet@2x.png) !important;\n" +
                "  }\n" +
                "  .linkedinshare {\n" +
                "    background-image: url(https://i10.createsend1.com/static/eb/master/13-the-blueprint-3/images/lishare@2x.png) !important;\n" +
                "  }\n" +
                "  .forwardtoafriend {\n" +
                "    background-image: url(https://i9.createsend1.com/static/eb/master/13-the-blueprint-3/images/forward@2x.png) !important;\n" +
                "  }\n" +
                "}\n" +
                "@media only screen and (max-width: 620px) {\n" +
                "  .wrapper .size-18,\n" +
                "  .wrapper .size-20 {\n" +
                "    font-size: 17px !important;\n" +
                "    line-height: 26px !important;\n" +
                "  }\n" +
                "  .wrapper .size-22 {\n" +
                "    font-size: 18px !important;\n" +
                "    line-height: 26px !important;\n" +
                "  }\n" +
                "  .wrapper .size-24 {\n" +
                "    font-size: 20px !important;\n" +
                "    line-height: 28px !important;\n" +
                "  }\n" +
                "  .wrapper .size-26 {\n" +
                "    font-size: 22px !important;\n" +
                "    line-height: 31px !important;\n" +
                "  }\n" +
                "  .wrapper .size-28 {\n" +
                "    font-size: 24px !important;\n" +
                "    line-height: 32px !important;\n" +
                "  }\n" +
                "  .wrapper .size-30 {\n" +
                "    font-size: 26px !important;\n" +
                "    line-height: 34px !important;\n" +
                "  }\n" +
                "  .wrapper .size-32 {\n" +
                "    font-size: 28px !important;\n" +
                "    line-height: 36px !important;\n" +
                "  }\n" +
                "  .wrapper .size-34,\n" +
                "  .wrapper .size-36 {\n" +
                "    font-size: 30px !important;\n" +
                "    line-height: 38px !important;\n" +
                "  }\n" +
                "  .wrapper .size-40 {\n" +
                "    font-size: 32px !important;\n" +
                "    line-height: 40px !important;\n" +
                "  }\n" +
                "  .wrapper .size-44 {\n" +
                "    font-size: 34px !important;\n" +
                "    line-height: 43px !important;\n" +
                "  }\n" +
                "  .wrapper .size-48 {\n" +
                "    font-size: 36px !important;\n" +
                "    line-height: 43px !important;\n" +
                "  }\n" +
                "  .wrapper .size-56 {\n" +
                "    font-size: 40px !important;\n" +
                "    line-height: 47px !important;\n" +
                "  }\n" +
                "  .wrapper .size-64 {\n" +
                "    font-size: 44px !important;\n" +
                "    line-height: 50px !important;\n" +
                "  }\n" +
                "  .divider {\n" +
                "    Margin-left: auto !important;\n" +
                "    Margin-right: auto !important;\n" +
                "  }\n" +
                "  .btn a {\n" +
                "    display: block !important;\n" +
                "    font-size: 14px !important;\n" +
                "    line-height: 24px !important;\n" +
                "    padding: 12px 10px !important;\n" +
                "    width: auto !important;\n" +
                "  }\n" +
                "  .btn--shadow a {\n" +
                "    padding: 12px 10px 13px 10px !important;\n" +
                "  }\n" +
                "  .image img {\n" +
                "    height: auto;\n" +
                "    width: 100%;\n" +
                "  }\n" +
                "  .layout,\n" +
                "  .column,\n" +
                "  .preheader__webversion,\n" +
                "  .header td,\n" +
                "  .footer,\n" +
                "  .footer__left,\n" +
                "  .footer__right,\n" +
                "  .footer__inner {\n" +
                "    width: 320px !important;\n" +
                "  }\n" +
                "  .preheader__snippet,\n" +
                "  .layout__edges {\n" +
                "    display: none !important;\n" +
                "  }\n" +
                "  .preheader__webversion {\n" +
                "    text-align: center !important;\n" +
                "  }\n" +
                "  .layout--full-width {\n" +
                "    width: 100% !important;\n" +
                "  }\n" +
                "  .layout--full-width tbody,\n" +
                "  .layout--full-width tr {\n" +
                "    display: table;\n" +
                "    Margin-left: auto;\n" +
                "    Margin-right: auto;\n" +
                "    width: 320px;\n" +
                "  }\n" +
                "  .column,\n" +
                "  .layout__gutter,\n" +
                "  .footer__left,\n" +
                "  .footer__right {\n" +
                "    display: block;\n" +
                "    Float: left;\n" +
                "  }\n" +
                "  .footer__inner {\n" +
                "    text-align: center;\n" +
                "  }\n" +
                "  .footer__links {\n" +
                "    Float: none;\n" +
                "    Margin-left: auto;\n" +
                "    Margin-right: auto;\n" +
                "  }\n" +
                "  .footer__right p,\n" +
                "  .footer__share-button {\n" +
                "    display: inline-block;\n" +
                "  }\n" +
                "  .layout__gutter {\n" +
                "    font-size: 20px;\n" +
                "    line-height: 20px;\n" +
                "  }\n" +
                "  .layout--no-gutter.layout--has-border:not(.layout--full-width),\n" +
                "  .layout--has-gutter.layout--has-border .column__background {\n" +
                "    width: 322px !important;\n" +
                "  }\n" +
                "  .layout--has-gutter.layout--has-border {\n" +
                "    left: -1px;\n" +
                "    position: relative;\n" +
                "  }\n" +
                "}\n" +
                "@media only screen and (max-width: 320px) {\n" +
                "  .border {\n" +
                "    display: none;\n" +
                "  }\n" +
                "  .layout--no-gutter.layout--has-border:not(.layout--full-width),\n" +
                "  .layout--has-gutter.layout--has-border .column__background {\n" +
                "    width: 320px !important;\n" +
                "  }\n" +
                "  .layout--has-gutter.layout--has-border {\n" +
                "    left: 0 !important;\n" +
                "  }\n" +
                "}\n" +
                "</style>\n" +
                "    \n" +
                "  <style type=\"text/css\">\n" +
                "body,.wrapper{background-color:#b7bdc4}.wrapper h1{color:#44596b;font-size:26px;line-height:34px}.wrapper h2{color:#44596b;font-size:20px;line-height:28px}.wrapper h3{color:#44596b;font-size:16px;line-height:24px}.wrapper a{color:#386994}.wrapper a:hover{color:#1c354a !important}@media only screen and (max-width: 620px){.wrapper h1{}.wrapper h1{font-size:22px;line-height:31px}.wrapper h2{}.wrapper h2{font-size:17px;line-height:26px}.wrapper h3{}.wrapper p{}}.column,.column__background td{color:#8e8e8e;font-size:14px;line-height:21px}.column,.column__background td{font-family:Avenir,sans-serif}.mso .column,.mso .column__background td{font-family:sans-serif !important}.border{background-color:#7f8a96}.layout--no-gutter.layout--has-border:not(.layout--full-width),.layout--has-gutter.layout--has-border .column__background,.layout--full-width.layout--has-border{border-top:1px solid \n" +
                "#7f8a96;border-bottom:1px solid #7f8a96}.wrapper blockquote{border-left:4px solid #7f8a96}.divider{background-color:#7f8a96}.wrapper .btn a{color:#fff}.wrapper .btn a{font-family:Avenir,sans-serif}.mso .wrapper .btn a{font-family:sans-serif !important}.wrapper .btn a:hover{color:#fff !important}.btn--flat a,.btn--shadow a,.btn--depth a{background-color:#386994}.btn--ghost a{border:1px solid #386994}.preheader--inline,.footer__left{color:#606b75}.preheader--inline,.footer__left{font-family:Avenir,sans-serif}.mso .preheader--inline,.mso .footer__left{font-family:sans-serif !important}.wrapper .preheader--inline a,.wrapper .footer__left a{color:#606b75}.wrapper .preheader--inline a:hover,.wrapper .footer__left a:hover{color:#606b75 !important}.header__logo{color:#c3ced9}.header__logo{font-family:Roboto,Tahoma,sans-serif}.mso .header__logo{font-family:Tahoma,sans-serif !important}.wrapper \n" +
                ".header__logo a{color:#c3ced9}.wrapper .header__logo a:hover{color:#fff !important}.footer__share-button{background-color:#5c5f62}.footer__share-button{font-family:Avenir,sans-serif}.mso .footer__share-button{font-family:sans-serif !important}.layout__separator--inline{font-size:20px;line-height:20px;mso-line-height-rule:exactly}\n" +
                "</style><meta name=\"robots\" content=\"noindex,nofollow\" />\n" +
                "<meta property=\"og:title\" content=\"My First Campaign\" />\n" +
                "</head>\n" +
                "<!--[if mso]>\n" +
                "  <body class=\"mso\">\n" +
                "<![endif]-->\n" +
                "<!--[if !mso]><!-->\n" +
                "  <body class=\"full-padding\" style=\"margin: 0;padding: 0;-webkit-text-size-adjust: 100%;background-color: #fdfdfd;\">\n" +
                "<!--<![endif]-->\n" +
                "    <div class=\"wrapper\" style=\"background-color: #fdfdfd; margin-bottom: -15px;\">\n" +
                "      <table style=\"border-collapse: collapse;table-layout: fixed;color: #606b75;font-family: Avenir,sans-serif;\" align=\"center\">\n" +
                "        <tbody><tr>\n" +
                "          <td class=\"preheader__snippet\" style=\"padding: 10px 0 5px 0;vertical-align: top;width: 280px;\">\n" +
                "            \n" +
                "          </td>\n" +
                "          <td class=\"preheader__webversion\" style=\"text-align: right;padding: 10px 0 5px 0;vertical-align: top;width: 280px;\">\n" +
                "            \n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </tbody></table>\n" +
                "      <table class=\"layout layout--no-gutter\" style=\"border-collapse: collapse;table-layout: fixed;Margin-left: auto;Margin-right: auto;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff; border: 1px solid lightgray;\" align=\"center\" emb-background-style>\n" +
                "        <tbody><tr>\n" +
                "          <td class=\"column\" style=\"padding: 0;text-align: left;vertical-align: top;color: #8e8e8e;font-size: 14px;line-height: 21px;font-family: Avenir,sans-serif;width: 600px;\">\n" +
                "      \n" +
                "            <div style=\"Margin-left: 20px;Margin-right: 20px;Margin-top: 20px;\">\n" +
                "      <div style=\"line-height:10px;font-size:1px\">&nbsp;</div>\n" +
                "    </div>\n" +
                "    \n" +
                "            <div style=\"Margin-left: 20px;Margin-right: 20px;\">\n" +
                "      <h1 style=\"Margin-top: 0;Margin-bottom: 20px;font-style: normal;font-weight: normal;color: #44596b;font-size: 26px;line-height: 34px;text-align: center;\"><strong>INSTITUTE OF LEGAL PRACTICE AND DEVELOPMENT <span style=\"color: #F15A24;\">ILPD</span></strong></h1>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"Margin-left: 20px;Margin-right: 20px;\">\n" +
                "      <h1 style=\"Margin-top: 0;Margin-bottom: 20px;font-style: normal;font-weight: normal;color: gray;font-size: 18px;line-height: 34px;text-align: center;\"><strong>Welcome!</strong></h1>\n" +
                "    </div>\n" +
                "    \n") +
                "            <div style=\"Margin-left: 20px;Margin-right: 20px;\">\n" +
                "      <blockquote style=\"Margin-top: 0;Margin-bottom: 20px;Margin-left: 0;Margin-right: 0;padding-left: 14px;border-left: 4px solid #7f8a96;\"><p class=\"size-16\" style=\"Margin-top: 0;Margin-bottom: 20px;font-size: 16px;line-height: 24px;\"><em>" + emailSet + "<br/><br/>\n" +
                "      Thank you.&nbsp;</em></p></blockquote>\n" +
                "    </div>\n" +
                "    \n" +
                "            <div style=\"Margin-left: 20px;Margin-right: 20px;Margin-bottom: 24px;\">\n" +
                "      <div class=\"btn btn--flat\" style=\"text-align:center;\">\n" +
                "        <img src=\"https://ilpd.ac.rw/typo3conf/ext/site_config/Resources/Public/images/logo-2.png\" style=\"height:120px;\">" +
                "      </div>\n" +
                "    </div>\n" +
                "    \n" +
                "    \n" +
                "            <div style=\"Margin-left: 20px;Margin-right: 20px;Margin-bottom: 24px;\">\n" +
                "      <div style=\"text-align:center;\">\n" +
                "        " +
                "      </div>\n" +
                "    </div>\n" +
                "    \n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </tbody></table>\n" +
                "\n" +
                "      <div style=\"font-size: 20px;line-height: 20px;mso-line-height-rule: exactly;\">&nbsp;</div>\n" +
                "    \n" +
                "      <table class=\"footer\" style=\"border-collapse: collapse;table-layout: fixed;Margin-right: auto;Margin-left: auto;border-spacing: 0;width: 560px;\" align=\"center\">\n" +
                "        <tbody><tr>\n" +
                "          <td style=\"padding: 0 0 0 0;\">\n" +
                "            <table class=\"footer__left\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;color: #606b75;font-family: Avenir,sans-serif;width: 100%; text-align: center;\">\n" +
                "              <tbody><tr>\n" +
                "                <td class=\"footer__inner\" style=\"padding: 0;font-size: 12px;line-height: 19px;\">\n" +
                "                  \n" +
                "                  <div>\n" +
                "                    <div>&copy; 2022 ILPD MIS. All Rights Reserved.</div>\n" +
                "                  </div>\n" +
                "\n" +
                "                  <div>\n" +
                "                    " +
                "                  </div>\n" +
                "                  <div class=\"footer__permission\" style=\"Margin-top: 18px;\">\n" +
                "                    \n" +
                "                  </div>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </tbody></table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </tbody></table>\n" +
                "      \n" +
                "    </div>\n" +
                "  \n" +
                "</body></html>\n";
        return body;
    }

}
