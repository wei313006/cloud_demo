package notify.template;

public class EmailTemplate {
    public static String checkCodeGenerator(String checkCode) {
        return new StringBuilder().append("""
                <!DOCTYPE html>
                    <html lang=en>
                    <head>
                        <meta charset=UTF-8>
                        <meta http-equiv=X-UA-Compatible content=IE=edge>
                        <meta name=viewport content=width=device-width, initial-scale=1.0>
                        <title>7TopGame.com</title>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                background-color: #f9f9f9; 
                               margin: 0; 
                                padding: 0;
                            }
                           .container {
                                max-width: 600px; 
                                min-height: 500px; 
                                margin: 40px auto; 
                                background-color: #ffffff; 
                                padding: 20px; 
                                border-radius: 8px; 
                                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); 
                               text-align: center;
                            }
                            .header {
                                font-size: 24px;
                                margin-bottom: 20px;
                            }
                            .code {
                               display: inline-block;
                                background-color: #f3f3f3; 
                               padding: 10px 20px;
                                font-size: 22px; 
                                font-weight: bold;
                                letter-spacing: 2px; 
                                border-radius: 5px;
                                margin: 20px 0; 
                            } 
                            .footer { 
                                font-size: 14px; 
                                color: #888; 
                               margin-top: 20px;
                            } 
                        </style> 
                    </head> 
                    <body> 
                        <div class=container> 
                            <div class=header>Email verification code</div> 
                            <p>Hello! Your email verification code is as follows:</p> 
                           <div class=code>
                """).append(checkCode).append("""
                        </div>
                        <p>Please enter the verification code within 5 minutes to complete the verification.</p> 
                        <p class=footer>If you have not requested this verification code, please ignore this email or contact customer 
                            support.</p>
                        <p class=footer>Thank you for your support.<br>[7TopGame.com]</p>
                    </div>
                </body>
                </html>
                        """).toString();
    }
}
