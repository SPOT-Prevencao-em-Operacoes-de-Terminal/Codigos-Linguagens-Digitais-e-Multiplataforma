
package sptech.scholl;

import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.NetworkInterface;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            System.out.println("===== SPOT =====");
            System.out.println("Monitoramento de rede");

            // conexão com o banco
            Conexao conexao = new Conexao();
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conexao.getDataSource());

            GerenciadorMonitoramento gerenciador = new GerenciadorMonitoramento(jdbcTemplate);

            // Identificação do servidor
            String uuid = obterUuidMaquina();
            System.out.println("UUID da máquina: " + uuid);

            Integer idServidor = gerenciador.obterIdServidor(uuid);
            System.out.println("Servidor identificado: " + idServidor);

            // Verificacao e cadastro dos limites
            if (!gerenciador.verificarLimites(idServidor)) {
                System.out.println("Configurando os limites do servidor...");
                gerenciador.cadastrarLimitesPadrao(idServidor);

            } else {
                System.out.println("Os limites já estão configurados.");
            }

            // Captura de rede
            Monitoramento monitoramento = new Monitoramento();

            long totalAnterior = 0L;
            boolean primeiraCaptura = true;
            int idComponenteRede = 4;

            // leitura anterior
            GrauAlerta grauAnterior = GrauAlerta.NORMAL;

            while (true) {
                Rede rede = monitoramento.capturarRede();

                System.out.println("Interface: " + rede.getNomeInterface());
                System.out.println("Bytes recebidos: " + rede.getBytesRecebidos());
                System.out.println("Bytes enviados: " + rede.getBytesEnviados());

                long totalAtual = rede.getBytesRecebidos() + rede.getBytesEnviados();

                // Primeira captura, para referencia.
                if (primeiraCaptura) {
                    System.out.println("Primeira captura usada como referência.");
                    primeiraCaptura = false;

                } else {
                    long diferenca = totalAtual - totalAnterior;

                    if (diferenca < 0) {
                        System.out.println("Contador reiniciado. Nova referência.");

                    } else {
                        double trafego = (diferenca / 1024.0 / 1024.0) / 10.0;
                        System.out.println("Tráfego: " + trafego + " MB/s");

                        // leitura inserida no banco
                        Long idLeitura = gerenciador.inserirLeitura(trafego, idComponenteRede, idServidor);
                        System.out.println("Leitura inserida no banco!");

                        // consulta o limite
                        Double limite = gerenciador.buscarLimite(idServidor, idComponenteRede);
                        // Classifica o trafego real da rede
                        GrauAlerta grau = gerenciador.verificarGrauAlerta(trafego,limite);

                        // Verifica se houve mudança de estado
                        if (grau != grauAnterior) {

                            System.out.println("Mudança de estado: " + grauAnterior + " -> " + grau);
                            List<String> destinatarios = gerenciador.buscarEmailsUsuariosAtivos(idServidor);

                            if (destinatarios.isEmpty()) {
                                System.out.println("Nenhum usuário ativo possui e-mail cadastrado.");

                            } else {
                                String dataHora =  LocalDateTime.now().format(
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                                );

                                // Envia para todos os usuários encontrados
                                int emailsEnviados = 0;
                                for (String destinatario : destinatarios) {
                                    try {
                                        EnviarEmail.enviarAlertaRede(
                                                destinatario,
                                                "Servidor " + idServidor,
                                                trafego,
                                                limite,
                                                grauAnterior.toString(),
                                                grau.toString(),
                                                dataHora
                                        );
                                        emailsEnviados++;

                                    } catch (Exception e) {
                                        System.out.println("Erro ao enviar um dos alertas.");
                                        System.out.println(e.getMessage());
                                    }
                                }

                                System.out.println("Envio finalizado: " + emailsEnviados +
                                        " de " + destinatarios.size() + " e-mails enviados."
                                );
                            }
                            // Atualiza o estado do grau
                            grauAnterior = grau;
                        }
                        // Registra o grau do alerta para cada leitura
                        gerenciador.registrarAlerta(grau, idLeitura);
                    }
                }

                // atualiza a referência da próxima captura
                totalAnterior = totalAtual;
                System.out.println("Aguardando 10 segundos...");
                Thread.sleep(10000);

                System.out.println("-------------------------");
            }

        } catch (Exception e) {
            System.out.println("Erro no programa:");
            System.out.println(e.getMessage());

            e.printStackTrace();
        }
    }

    // Obtém o UUID da máquina
    public static String obterUuidMaquina() throws Exception {
        String sistema = System.getProperty("os.name").toLowerCase();

        if (sistema.contains("linux")) {

            for (NetworkInterface rede : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                byte[] mac = rede.getHardwareAddress();

                if (!rede.isLoopback() && mac != null && mac.length == 6) {
                    StringBuilder hexadecimal = new StringBuilder();

                    for (byte b : mac) {
                        hexadecimal.append(String.format("%02x", b & 0xff));
                    }
                    return Long.toString(Long.parseLong(hexadecimal.toString(), 16));
                }
            }
            throw new IllegalStateException("Não foi possível obter o MAC da máquina.");

        }

        // Windows: consulta o MachineGuid
        Process processo = new ProcessBuilder(
                "reg",
                "query",
                "HKLM\\SOFTWARE\\Microsoft\\Cryptography",
                "/v",
                "MachineGuid"
        ).redirectErrorStream(true).start();

        String linha;

        try (BufferedReader leitor = new BufferedReader(new InputStreamReader(processo.getInputStream()))) {

            while ((linha = leitor.readLine()) != null) {
                if (linha.contains("MachineGuid")) {
                    String[] partes = linha.trim().split("\\s+");
                    processo.waitFor();

                    return partes[partes.length - 1];
                }
            }
        }

        int resultado = processo.waitFor();
        throw new IllegalStateException("Não foi possível obter o UUID. Código: " + resultado);
    }
}
