// Comparação das buscas COM informação sobre o MESMO labirinto:
// BFS (referencia otima cega), A* Manhattan, A* Euclidiana, Guloso Manhattan e Guloso Euclidiana.
// Mostra o mapa do A* e do Guloso (pra ver o contraste de caminho) e a tabela com as 5 linhas.
// Nao altera nenhuma classe original do projeto.
public class ComparaComInformacao {

    public static void main(String[] args) {

        boolean debug = false;

        Labirinto labirinto = new Labirinto(10, 10, 30, debug);

        Posicao entrada = labirinto.getPosicaoAtual();
        Posicao saida = labirinto.getPosicaoSaida();

        System.out.println("Labirinto:");
        labirinto.print(null);
        System.out.println("Entrada: " + entrada + " | Saida: " + saida
                + " | DLR: " + labirinto.getDLR(entrada, saida));

        Busca busca = new Busca(labirinto, debug);

        // Todas as buscas no mesmo labirinto.
        Posicao[] cBfs = busca.buscarLargura();        MetricasBusca mBfs = busca.getUltimaMetrica();
        Posicao[] cAM  = busca.buscarAEstrela(false);  MetricasBusca mAM  = busca.getUltimaMetrica(); // A* Manhattan
        Posicao[] cAE  = busca.buscarAEstrela(true);   MetricasBusca mAE  = busca.getUltimaMetrica(); // A* Euclidiana
        Posicao[] cGM  = busca.buscarGuloso(false);    MetricasBusca mGM  = busca.getUltimaMetrica(); // Guloso Manhattan
        Posicao[] cGE  = busca.buscarGuloso(true);     MetricasBusca mGE  = busca.getUltimaMetrica(); // Guloso Euclidiana

        System.out.println("\n===== A* (Manhattan) =====");
        labirinto.print(cAM);

        System.out.println("\n===== Guloso (Manhattan) =====");
        labirinto.print(cGM);

        System.out.println("\n===== Comparativo (com informacao) =====");
        System.out.println(MetricasBusca.cabecalho());
        System.out.println(mBfs.linhaTabela());
        System.out.println(mAM.linhaTabela());
        System.out.println(mAE.linhaTabela());
        System.out.println(mGM.linhaTabela());
        System.out.println(mGE.linhaTabela());
    }
}