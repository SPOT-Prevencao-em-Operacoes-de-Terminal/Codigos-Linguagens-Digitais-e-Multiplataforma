package sptech.scholl;

public class Rede {
    private String nomeInterface;
    private Long bytesRecebidos;
    private Long bytesEnviados;

    // Cria o objeto Rede sem os valores
    public Rede() {
    }

    public String getNomeInterface() {
        return nomeInterface;
    }

    public void setNomeInterface(String nomeInterface) {
        this.nomeInterface = nomeInterface;
    }

    public Long getBytesRecebidos() {
        return bytesRecebidos;
    }

    public void setBytesRecebidos(Long bytesRecebidos) {
        this.bytesRecebidos = bytesRecebidos;
    }

    public Long getBytesEnviados() {
        return bytesEnviados;
    }

    public void setBytesEnviados(Long bytesEnviados) {
        this.bytesEnviados = bytesEnviados;
    }


}