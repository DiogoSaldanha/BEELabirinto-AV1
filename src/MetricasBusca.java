// Basicamente guarda as métricas de uma execução de busca, para permitir a comparação entre algoritmos exigida pela atividade:
// - qualidade da resposta: quantidade de passos (movimentos) do caminho;
// - desempenho: tempo de execução e memória utilizada.
public class MetricasBusca {

    private String algoritmo;      // nome do algoritmo
    private String heuristica;     // heurística usada (null quando sem informação)
    private boolean encontrou;    // se encontrou solução
    private int passos;         // movimentos do caminho (posicoes - 1); OU -1 se não achou
    private int nosExpandidos;    // quantos nós foram expandidos
    private int fronteiraMaxima;  // maior tamanho da fronteira durante a busca
    private long tempoNs;      //tempo de execução em nanossegundo
    private long memoriaBytes;    // variação aproximada de memória, em bytes

    public MetricasBusca(String algoritmo) {
        this.algoritmo = algoritmo;
        this.heuristica = null;
        this.encontrou = false;
        this.passos = -1;
        this.nosExpandidos = 0;
        this.fronteiraMaxima = 0;
        this.tempoNs = 0L;
        this.memoriaBytes = 0L;
    }

    public String getAlgoritmo()      { return algoritmo; }
    public String getHeuristica()     { return heuristica; }
    public boolean isEncontrou()      { return encontrou; }
    public int getPassos()            { return passos; }
    public int getNosExpandidos()     { return nosExpandidos; }
    public int getFronteiraMaxima()   { return fronteiraMaxima; }
    public long getTempoNs()          { return tempoNs; }
    public long getMemoriaBytes()     { return memoriaBytes; }

    public void setHeuristica(String heuristica)       { this.heuristica = heuristica; }
    public void setEncontrou(boolean encontrou)        { this.encontrou = encontrou; }
    public void setPassos(int passos)                  { this.passos = passos; }
    public void setNosExpandidos(int nosExpandidos)    { this.nosExpandidos = nosExpandidos; }
    public void setFronteiraMaxima(int fronteiraMaxima){ this.fronteiraMaxima = fronteiraMaxima; }
    public void setTempoNs(long tempoNs)               { this.tempoNs = tempoNs; }
    public void setMemoriaBytes(long memoriaBytes)     { this.memoriaBytes = memoriaBytes; }

    // Converte o tempo de nanossegundos para milissegundos.
    public double getTempoMs() {
        return tempoNs / 1_000_000.0;
    }

    @Override
    public String toString() {
        String nome = algoritmo + (heuristica != null ? " (" + heuristica + ")" : "");
        if (!encontrou) {
            return String.format(
                "%-24s | SEM SOLUCAO | expandidos=%d | fronteira_max=%d | tempo=%.3f ms | memoria=%d bytes",
                nome, nosExpandidos, fronteiraMaxima, getTempoMs(), memoriaBytes);
        }
        return String.format(
            "%-24s | passos=%d | expandidos=%d | fronteira_max=%d | tempo=%.3f ms | memoria=%d bytes",
            nome, passos, nosExpandidos, fronteiraMaxima, getTempoMs(), memoriaBytes);
    }
}