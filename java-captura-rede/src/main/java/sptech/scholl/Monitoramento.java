package sptech.scholl;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.util.List;

public class Monitoramento {

    public Rede capturarRede() {
        Rede rede = new Rede();
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();

            List<NetworkIF> interfaces = hardware.getNetworkIFs();

            for (NetworkIF interfaceRede : interfaces) {
                interfaceRede.updateAttributes();
                Long bytesRecebidos = interfaceRede.getBytesRecv();
                Long bytesEnviados = interfaceRede.getBytesSent();

                if (bytesRecebidos == 0 && bytesEnviados == 0) {
                    continue;
                }

                rede.setNomeInterface(interfaceRede.getDisplayName());
                rede.setBytesRecebidos(bytesRecebidos);
                rede.setBytesEnviados(bytesEnviados);
                break;
            }
        } catch (Exception e) {
            System.out.println("Erro ao capturar a rede:");
            System.out.println(e.getMessage());
        }
        return rede;
    }
}

