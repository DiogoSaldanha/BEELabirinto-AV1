// Comparação das buscas SEM informação: BFS x DFS sobre o MESMO labirinto.
// A ideia é mostrara diferença de qualidade (número de passos) e de desempenho (nós expandidos, fronteira, tempo, memoria)
// Rodamos com a mesma entrada e mesma saída.
public class ComparaSemInformacao {

    public static void main(String[] args) {

        boolean debug = false;

        // um labirinto compartilhado pelos dois algoritmos.
        Labirinto labirinto = new Labirinto(10, 10, 30, debug);

        Posicao entrada = labirinto.getPosicaoAtual();
        Posicao saida = labirinto.getPosicaoSaida();

        System.out.println("Labirinto:");
        labirinto.print(null);
        System.out.println("Entrada: " + entrada + " | Saída: " + saida
                + " | DLR: " + labirinto.getDLR(entrada, saida));

        Busca busca = new Busca(labirinto, debug);

        // BFS
        Posicao[] caminhoBfs = busca.buscarLargura();
        MetricasBusca mBfs = busca.getUltimaMetrica();
        System.out.println("\n===== BFS (Busca em Largura) =====");
        labirinto.print(caminhoBfs);

        // DFS (mesmo labirinto)
        Posicao[] caminhoDfs = busca.buscarProfundidade();
        MetricasBusca mDfs = busca.getUltimaMetrica();
        System.out.println("\n===== DFS (Busca em Profundidade) =====");
        labirinto.print(caminhoDfs);

        // Tabela comparativa
        System.out.println("\n===== Comparativo (sem informacao) =====");
        System.out.println(MetricasBusca.cabecalho());
        System.out.println(mBfs.linhaTabela());
        System.out.println(mDfs.linhaTabela());
    }
}