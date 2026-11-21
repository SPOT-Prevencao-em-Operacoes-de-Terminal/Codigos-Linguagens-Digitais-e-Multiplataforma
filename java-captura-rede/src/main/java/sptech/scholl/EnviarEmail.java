package sptech.scholl;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EnviarEmail {
    private static final String EMAIL_SPOT = "spot.systemmonitoring@gmail.com";

    public static void enviarAlertaRede(
            String destinatario,
            String servidor,
            double trafego,
            double limite,
            String estadoAnterior,
            String estadoAtual,
            String dataHora
    ) throws Exception {
        //Recuperar a senha de app
        String senha = System.getenv("SPOT_EMAIL_SENHA");

        if (senha == null || senha.isEmpty()) {
            throw new Exception("Senha de app não configurada!");
        }

        //Configurar o servidor SMTP
        Properties propriedades = new Properties();

        propriedades.put("mail.smtp.host", "smtp.gmail.com");
        propriedades.put("mail.smtp.port", "587");
        propriedades.put("mail.smtp.auth", "true");
        propriedades.put("mail.smtp.starttls.enable", "true");
        propriedades.put("mail.smtp.starttls.required", "true");

        // 3. Configurar autenticação
        Session sessao = Session.getInstance(propriedades, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_SPOT, senha);
            }
        }
        );

        //assunto do e-mail
        String assunto;

        if (estadoAtual.equals("ATENCAO")) {
            assunto = "SPOT | ALERTA DE ATENÇÃO: Uso de Rede";
        } else if (estadoAtual.equals("CRITICO")) {
            assunto = "SPOT | ALERTA CRÍTICO: Uso de Rede";
        } else {
            assunto = "SPOT | NORMALIZAÇÃO: Uso de Rede";
        }

        //Montar o conteúdo do e-mail
        String conteudo =
                "Olá!\n\n" +
                        "O sistema SPOT identificou uma mudança no estado " +
                        "de monitoramento do servidor.\n" +

                        "Servidor: " + servidor + "\n\n" +
                        "Componente: Uso de REDE\n" +
                        "Tráfego atual: " + trafego + " MB/s\n" +
                        "Limite configurado: " + limite + " MB/s\n" +
                        "Estado anterior: " + estadoAnterior + "\n" +
                        "Estado atual: " + estadoAtual + "\n" +
                        "Data e horário: " + dataHora + "\n\n";

        if (estadoAtual.equals("ATENCAO")) {
            conteudo += "ATENÇÃO! O tráfego de rede está próximo do limite configurado.\n" +
                    "Recomendamos acompanhar o comportamento do servidor.";

        } else if (estadoAtual.equals("CRITICO")) {
            conteudo +="ALERTA CRÍTICO! O tráfego de rede atingiu ou ultrapassou o limite configurado.\n" +
                    "Recomendamos verificar imediatamente o servidor.";

        } else {
            conteudo += "NORMALIZAÇÃO! O tráfego de rede voltou a uma condição normal.\n" +
                    "O monitoramento continua ativo.";
        }

        conteudo += "\n\nEsta é uma notificação automática do sistema SPOT.";

        //Preparar a mensagem
        MimeMessage email = new MimeMessage(sessao);

        email.setFrom(new InternetAddress(EMAIL_SPOT));
        email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        email.setSubject(assunto, "UTF-8");
        email.setText(conteudo, "UTF-8");

        //Enviar o e-mail
        Transport.send(email);
        System.out.println("E-mail de " + estadoAtual + " enviado com sucesso!");
    }
}
